package a03; // comment to execute

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;

/**
 * CS6375: Machine Learning
 * Assignment 03: K-Means Clustering Algorithm
 * 
 * @author Rahul Nalawade
 * Apr 01, 2018
 */

// K-Means Clustering Implementation
public class KMeans {
	public static void main(String[] args) {
		if (args.length < 3) {
			System.out.println("Usage: Kmeans <input-image> <k> <output-image>");
			return;
		}

		try {
			BufferedImage originalImage = ImageIO.read(new File(args[0]));
			BufferedImage kmeansJpg = kmeans_helper(originalImage, Integer.parseInt(args[1]));
			ImageIO.write(kmeansJpg, "png", new File(args[2]));
		}

		catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	private static boolean converge(int[] mean, int[] mean1) {
		for (int i = 0; i < mean1.length; i++)
			if (mean[i] != mean1[i])
				return false;

		return true;
	}

	private static BufferedImage kmeans_helper(BufferedImage originalImage, int k) {
		int w = originalImage.getWidth();
		int h = originalImage.getHeight();
		BufferedImage kmeansImage = new BufferedImage(w, h, originalImage.getType());
		Graphics2D g = kmeansImage.createGraphics();
		g.drawImage(originalImage, 0, 0, w, h, null);

		// Read rgb values from the image.
		int[] rgb = new int[(w * h)];
		int count = 0;
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				rgb[count++] = kmeansImage.getRGB(i, j);
			}
		}

		// Call kmeans algorithm: update the rgb values to compress image.
		kmeans(rgb, k);

		// Write the new rgb values to the image.
		count = 0;
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				kmeansImage.setRGB(i, j, rgb[count++]);
			}
		}

		// Return the compressed image
		return kmeansImage;
	}

	// Your k-means code goes here
	// Update the array rgb by assigning each entry in the rgb array to its cluster
	// center
	private static void kmeans(int[] rgb, int k) {
		if (rgb.length < k) {
			System.out.println("pixel length<k!!!");
			return;
		}
		double thresholdDist = 0.0;
		double Dist = 0;
		int center = 0;
		int[] nR = new int[k];
		int[] nG = new int[k];
		int[] nB = new int[k];
		int[] mean = new int[k];
		int[] mean1 = new int[k];
		int[] total = new int[k];
		int[] cluster = new int[rgb.length];
		// Initially assigning random centers for k clusters
		for (int i = 0; i < k; i++) {
			Random random = new Random();
			mean1[i] = rgb[random.nextInt(rgb.length)];
		}

		do {
			for (int i = 0; i < mean1.length; i++) {
				mean[i] = mean1[i];
				total[i] = nR[i] = nG[i] = nB[i] = 0;
			}
			// Finding closest center
			for (int i = 0; i < rgb.length; i++) {
				thresholdDist = Double.MAX_VALUE;
				for (int j = 0; j < mean1.length; j++) {
					Color d = new Color(rgb[i]);
					Color e = new Color(mean1[j]);
					int dR = d.getRed() - e.getRed();
					int dG = d.getGreen() - e.getGreen();
					int dB = d.getBlue() - e.getBlue();
					Dist = Math.sqrt(dR * dR + dG * dG + dB * dB);
					if (Dist < thresholdDist) {
						thresholdDist = Dist;
						center = j;
					}
				}
				cluster[i] = center;
				total[center]++;
				Color c = new Color(rgb[i]);
				nR[center] += c.getRed();
				nG[center] += c.getGreen();
				nB[center] += c.getBlue();
				// System.out.println("r"+nR[center]);
			}
			// set center values
			for (int i = 0; i < mean1.length; i++) {
				int aR = findAverage(nR[i], total[i]);
				int aG = findAverage(nG[i], total[i]);
				int aB = findAverage(nB[i], total[i]);
				mean1[i] = ((aR & 0x000000FF) << 16) | ((aG & 0x000000FF) << 8) | ((aB & 0x000000FF));
			}
		} while (!converge(mean, mean1));
		for (int i = 0; i < rgb.length; i++) {
			rgb[i] = mean1[cluster[i]];
		}
	}

	private static int findAverage(double s, double k) {
		int a = (int) (s / k);
		return a;
	}

}