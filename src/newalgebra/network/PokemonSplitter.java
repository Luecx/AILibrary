package newalgebra.network;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class PokemonSplitter {

	public static void main(String[] args) {
		String folder = "C:\\Users\\Marcelo Carpenter\\Documents\\B&W_POKEMON\\";
		String pokemon = "C:\\Users\\Marcelo Carpenter\\Documents\\poke.jpg";
		
		try {
			BufferedImage bi = ImageIO.read(new File(pokemon));
			int w = bi.getWidth();
			int h = bi.getHeight();

			for(int i = 0; i < w; i+=40) {
				for(int j = 0; j < h; j+=30) {
					
					BufferedImage subImage = bi.getSubimage(i, j, 40, 30);
					
					for(int x = 0; x < 40; x++) {
						for(int y = 0; y < 30; y++) {
							int c = subImage.getRGB(x, y);
//							int a = (c>>24)&0xff;
							int r = (c>>16)&0xff;
							int g = (c>>8)&0xff;
							int b = c&0xff;
							
							int v = (r+g+b)/3;
							subImage.setRGB(x, y, (255<<24) | (v<<16) | (v<<8) | (v));
							
						}
					}
					
					ImageIO.write(subImage, "png", new File(folder + "[" + i/40 + ", " + j/30 + "].png"));
					
				}
			}
			
		} 
		catch (IOException e) {e.printStackTrace();}
		
	}

}
