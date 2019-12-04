package neuralnetwork.data.mnist;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class MnistImageLoader {
	
	public static BufferedImage loadImage(String path) throws Exception{
		return resize(ImageIO.read(new File(path)),28,28);
	}

	public static BufferedImage resize(BufferedImage img, int newW, int newH) {
		Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
		BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = dimg.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();

		return dimg;
}  

	public static int[][] bufferedImageToArray(BufferedImage img) {
		int[][] arr = new int[img.getWidth()][img.getHeight()];

		for(int i = 0; i < img.getWidth(); i++)
		    for(int j = 0; j < img.getHeight(); j++)
		        arr[i][j] = img.getRGB(i, j);
		
		return arr;
	}

	public static int[][] bufferedImageRedToArray(BufferedImage img) {
		int[][] arr = new int[img.getWidth()][img.getHeight()];
		
		for(int i = 0; i < img.getWidth(); i++)
		    for(int j = 0; j < img.getHeight(); j++)
		        arr[i][j] = new Color(img.getRGB(i, j)).getRed();
		
		return arr;
	}
	
	public static double[] intArrayToDoubleArray(int[][] i) {
		double[] ar = new double[i.length * i[0].length];
		for(int j = 0 ; j < i.length; j ++){
			for(int n = 0; n < i[0].length; n++){
				ar[j * i.length + n] = (double)i[n][j] / (double)256;
			}
		}
		return ar;
	}
	
	public static double[] invert(double[] ar) {
		for(int j = 0 ; j < ar.length; j ++){
			ar[j] = 0.9999-ar[j];
		}
		return ar;
	}
	
}
