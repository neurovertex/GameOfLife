package eu.neurovertex.gol;

import java.util.Arrays;

/**
 * @author Neurovertex
 *         Date: 18/10/2014, 13:49
 */
public class Lattice implements Cloneable {
	private static final int[][] neighbours4 = {new int[]{1, 0}, new int[]{0, 1}, new int[]{-1, 0}, new int[]{0, -1}},
								 neighbours8 = {new int[]{1, -1}, new int[]{-1, -1}, new int[]{1, 1}, new int[]{-1, 1}};
	private boolean[][] lattice;
	private static LocalEvolutionFunction function = new GameOfLife();
	private int width, height;

	public Lattice(int width, int height) {
		this.width = width;
		this.height = height;
		this.lattice = new boolean[width][height];
	}

	public Lattice(boolean[][] lattice) {
		this.lattice = lattice.clone();
		this.width = lattice.length;
		this.height = lattice[0].length;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public boolean get(int i, int j) {
		i = Math.floorMod(i, getWidth());
		j = Math.floorMod(j, getHeight());
		return (i >= 0 && i < getWidth() && j >= 0 && j < getHeight()) && lattice[i][j];
	}

	public Lattice iterate() {
		boolean[][] newLat = new boolean[width][height];
		for (int i = 0; i < getWidth(); i++)
			for (int j = 0; j < getHeight(); j++)
				newLat[i][j] = function.apply(get(i, j), getNeighbours(i, j));
		return new Lattice(newLat);
	}

	public boolean[][] getLattice() {
		return lattice.clone();
	}

	private boolean[] getNeighbours(int i, int j) {
		boolean[] result = new boolean[neighbours4.length + neighbours8.length];
		for (int c = 0; c < neighbours4.length; c++)
			result[c] = get(i + neighbours4[c][0], j + neighbours4[c][1]);
		for (int c = 0; c < neighbours8.length; c++)
			result[c+neighbours4.length] = get(i + neighbours8[c][0], j + neighbours8[c][1]);

		return result;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		for (boolean[] line : lattice) {
			hash ^= ~Arrays.hashCode(line);
		}
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Lattice && Arrays.deepEquals(lattice, ((Lattice) obj).lattice);
	}

	@FunctionalInterface
	public interface LocalEvolutionFunction {
		boolean apply(boolean cell, boolean... neighbours);
	}
}
