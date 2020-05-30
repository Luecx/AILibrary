package newalgebra.network;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import core.tensor.Tensor;

public class LoadPokemon {

	public static ArrayList<Tensor> pokemon;
	
	public static void loadAllPokemon() {
		String folder = "C:\\Users\\Marcelo Carpenter\\Documents\\B&W_POKEMON\\";
		pokemon = new ArrayList<>();
		try {
			File poke = new File(folder);
			for(File p : poke.listFiles()) {
				BufferedImage bi = ImageIO.read(p);
				int w = bi.getWidth();
				int h = bi.getHeight();
				
				Tensor data = new Tensor(w*h);
				
				for(int i = 0; i < w; i++) {
					for(int j = 0; j < h; j++) {
						
						int c = bi.getRGB(i, j);
						int r = (c>>16)&0xff;
						int g = (c>>8)&0xff;
						int b = c&0xff;
						
						double v = ((r+g+b)/3.0)/255.0;
						data.set(v, j*w + i); //y*rowWidth + xpos
					}
				}
				
				pokemon.add(data);
			}
				
			
		}catch(Exception e) {e.printStackTrace();}
	}

}
