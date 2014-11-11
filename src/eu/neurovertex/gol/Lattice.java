package eu.neurovertex.gol;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Jeremie
 * Date: 03/11/2014
 * Time: 22:15
 */
public interface Lattice {

	public boolean get(int i, int j);
	public Color getColor(int i, int j);
	public int getWidth();
	public int getHeight();

	public Connectivity getConnectivity();

	public static boolean[][] cloneArray(boolean[][] array) {
		boolean[][] copy = new boolean[array.length][];
		for (int i = 0; i < array.length; i ++)
			copy[i] = array[i].clone();
		return copy;
	}

	public enum Connectivity {
		CONNECT8, CONNECT4, HORIZONTAL1D, VERTICAL1D;

		public int[][] getNeighbours() {
			switch (this) {
				case CONNECT8:
					return new int[][]{new int[]{1, 0}, new int[]{0, 1}, new int[]{-1, 0}, new int[]{0, -1},
							new int[]{1, -1}, new int[]{-1, -1}, new int[]{1, 1}, new int[]{-1, 1}};
				case CONNECT4:
					return new int[][]{new int[]{1, 0}, new int[]{0, 1}, new int[]{-1, 0}, new int[]{0, -1}};
				case HORIZONTAL1D:
					return new int[][]{new int[]{1, 0}, new int[]{-1, 0}};
				case VERTICAL1D:
					return new int[][]{new int[]{0, 1}, new int[]{0, -1}};
			}
			return null;
		}
	}
}
