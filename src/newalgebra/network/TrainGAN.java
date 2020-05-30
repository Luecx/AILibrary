package newalgebra.network;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import core.tensor.Tensor;
import newalgebra.builder.CellBuilder;
import newalgebra.cells.Cell;
import newalgebra.cells.Dimension;
import newalgebra.cells.Variable;
import newalgebra.element_operators.functions.SQNL;
import newalgebra.element_operators.functions.Sigmoid;
import newalgebra.network.loss.MSE;
import newalgebra.network.nodes.Dense;
import newalgebra.network.optimiser.Adam;
import newalgebra.network.weights.rng.Uniform;

public class TrainGAN {

	public static ArrayList<Tensor> data;
	private static Uniform rng;
	private static Uniform rngData;
	public static long seed = 17493L;
	public static GAN pokeGAN;
	
	public static void main(String[] args) {
		LoadPokemon.loadAllPokemon();
		data = LoadPokemon.pokemon;
		rng = new Uniform(-1, 1, seed);
		rngData = new Uniform(0, data.size(), seed);
		
		System.out.println("Size of training data : " + data.size());
		System.out.println("Uniform seed =  " + seed);
		
		CellBuilder builder = new CellBuilder();
        builder.add(new Variable(new Dimension(30)));
//        builder.add(new Dense(50));
//        builder.add(new SQNL());
//        builder.add(new Dense(50));
//        builder.add(new SQNL());
//        builder.add(new Dense(50));
//        builder.add(new SQNL());
        builder.add(new Dense(1200));
        builder.add(new Sigmoid());

        Cell gen = builder.build();
        
        builder = new CellBuilder();
        builder.add(new Variable(new Dimension(1200)));
        builder.add(new Dense(30));
        builder.add(new SQNL());
        builder.add(new Dense(30));
        builder.add(new SQNL());
        builder.add(new Dense(30));
        builder.add(new SQNL());
        builder.add(new Dense(1));
        builder.add(new Sigmoid());

        Cell adv = builder.build();
        
        pokeGAN = new GAN(gen, adv, new MSE(), new Adam(), new Adam());
        
        train(1000000);
	}

	public static void train(int iterations) {
		
		for(int i = 0; i < iterations; i++) {
			
			for(int j = 0; j < 1; j++) {
				double l = 0;
				l += pokeGAN.trainAdversarial(data.get((int)rngData.nextDouble()));
				l += pokeGAN.trainAdvGivenGen(randNoise(30));
				System.out.println(l);
			}
			for(int j = 0; j < 3; j++) {
				pokeGAN.trainGen(randNoise(30));
			}
			System.out.println(i);
			if(i%100 == 0) {
				writeCurrentGAN(i, "C:\\Users\\Marcelo Carpenter\\Documents\\AI_Generated_Test1\\", 10);
			}
		}
		
	}
	
	public static Tensor randNoise(int size) {
		Tensor t = new Tensor(size);
		for(int i = 0; i < size; i++) {
			t.set(rng.nextDouble(), i);
		}
		return t;
	}
	
	public static void writeCurrentGAN(int iteration, String dest, int count) {
		String nFolder = dest + iteration + "\\";
		File folder = new File(nFolder);
		folder.mkdir();
		
		for(int i = 0; i < count; i++) {
			Tensor out = pokeGAN.calcGenerative(randNoise(30))[0];
			
			BufferedImage im = new BufferedImage(40, 30, BufferedImage.TYPE_INT_ARGB);
			
			for(int j = 0; j < out.size(); j++) {
				int v = (int) (out.get(j) * 255);
				int rgb = (255<<24) | (v<<16) | (v<<8) | (v);
				int x = j%40;
				int y = j/40;
				im.setRGB(x, y, rgb);
			}
			
			try {
				ImageIO.write(im, "png", new File(nFolder + i + ".png"));
			} 
			catch (IOException e) {e.printStackTrace();}
			
		}
		
	}
	
}
