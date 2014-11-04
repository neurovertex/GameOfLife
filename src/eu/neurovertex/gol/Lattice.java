package eu.neurovertex.gol;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Jeremie
 * Date: 03/11/2014
 * Time: 22:15
 */
public interface Lattice {
	int[][] neighbours = {new int[]{1, 0}, new int[]{0, 1}, new int[]{-1, 0}, new int[]{0, -1},
												new int[]{1, -1}, new int[]{-1, -1}, new int[]{1, 1}, new int[]{-1, 1}};

	public boolean get(int i, int j);
	public Color getColor(int i, int j);
	public int getWidth();
	public int getHeight();

	public static boolean[][] cloneArray(boolean[][] array) {
		boolean[][] copy = new boolean[array.length][];
		for (int i = 0; i < array.length; i ++)
			copy[i] = array[i].clone();
		return copy;
	}
}
