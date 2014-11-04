package eu.neurovertex.gol;

import java.awt.*;
import java.util.Arrays;

/**
 * This class represents an immutable cellular automaton configuration in the form of a bidimentional array of booleans.
 * @author Neurovertex
 *         Date: 18/10/2014, 13:49
 */
public class StaticLattice implements Cloneable, Lattice {
	public static final int[][] neighbours = {new int[]{1, 0}, new int[]{0, 1}, new int[]{-1, 0}, new int[]{0, -1},
												new int[]{1, -1}, new int[]{-1, -1}, new int[]{1, 1}, new int[]{-1, 1}};
	private static LocalEvolutionFunction function = GameOfLife::evolve;
	private boolean[][] lattice;
	private int width, height;

	/**
	 * Create an empty (all-dead) lattice of the given width and height
	 * @param width		Width of the lattice
	 * @param height	Height of the lattice
	 */
	public StaticLattice(int width, int height) {
		this.width = width;
		this.height = height;
		this.lattice = new boolean[width][height];
	}

	/**
	 * Creates a lattice whichs dimensions and value is copied from the given array of booleans.
	 * @param lattice	Lattice to copy from
	 */
	public StaticLattice(boolean[][] lattice) {
		this.lattice = lattice.clone();
		this.width = lattice.length;
		this.height = lattice[0].length;
	}

	/**
	 * @return the width of the lattice
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return the height of the lattice
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Returns the value of the given cell
	 * @param i		X coordinate of the cell
	 * @param j 	Y coordinate of the cell
	 * @return		Value of the cell at (i,j)
	 */
	public boolean get(int i, int j) {
		i = Math.floorMod(i, getWidth());
		j = Math.floorMod(j, getHeight());
		return (i >= 0 && i < getWidth() && j >= 0 && j < getHeight()) && lattice[i][j];
	}

	public Color getColor(int i, int j) {
		i = Math.floorMod(i, getWidth());
		j = Math.floorMod(j, getHeight());
		return (i >= 0 && i < getWidth() && j >= 0 && j < getHeight()) && lattice[i][j] ? Color.GREEN : Color.BLACK;
	}

	/**
	 * Applies the evolution function to the cells of the lattice and returns its successor
	 * @return The successor of the current configuration by its evolution function
	 */
	public StaticLattice iterate() {
		boolean[][] newLat = new boolean[width][height];
		for (int i = 0; i < getWidth(); i++)
			for (int j = 0; j < getHeight(); j++)
				newLat[i][j] = function.apply(get(i, j), getNeighbours(i, j));
		return new StaticLattice(newLat);
	}

	/**
	 * @return a copy of the underlying array of booleans of this lattice
	 */
	public boolean[][] getLattice() {
		return lattice.clone();
	}

	/**
	 * Returns a clone of the current Lattice. This is preferrable to new Lattice(lattice.getLattice()), as the latter
	 * will clone the boolean array twice.
	 * @return A copy of this Lattice object
	 * @throws CloneNotSupportedException
	 */
	@Override
	protected StaticLattice clone() throws CloneNotSupportedException {
		StaticLattice newLat = (StaticLattice)super.clone();
		newLat.lattice = lattice.clone();
		return newLat;
	}

	private boolean[] getNeighbours(int i, int j) {
		boolean[] result = new boolean[neighbours.length];
		for (int c = 0; c < neighbours.length; c++)
			result[c] = get(i + neighbours[c][0], j + neighbours[c][1]);

		return result;
	}

	/**
	 * @return the number of live cells in the lattice
	 */
	public int getCellCount() {
		int count = 0;
		for (boolean[] line : lattice)
			for (boolean b : line)
				if (b)
					count ++;
		return count;
	}

	public static boolean[][] generateRLattice(int w, int h, int cells) {
		boolean[][] lat = new boolean[w][h];
		for (int i = 0; i < cells; i ++) {
			int x, y;
			do {
				x = (int) (Math.random()*w);
				y = (int) (Math.random()*h);
			} while (lat[x][y]);
			lat[x][y] = true;
		}
		return lat;
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
		return obj instanceof StaticLattice && Arrays.deepEquals(lattice, ((StaticLattice) obj).lattice);
	}

	@FunctionalInterface
	public interface LocalEvolutionFunction {
		boolean apply(boolean cell, boolean... neighbours);
	}
}
