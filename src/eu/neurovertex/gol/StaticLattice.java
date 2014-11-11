package eu.neurovertex.gol;

import java.awt.*;
import java.util.BitSet;

/**
 * This class represents an immutable cellular automaton configuration in the form of a bidimentional array of booleans.
 *
 * @author Neurovertex
 *         Date: 18/10/2014, 13:49
 */
public class StaticLattice implements Cloneable, Lattice {
	private BitSet lattice;
	private int width, height;
	private Connectivity connectivity = Connectivity.HORIZONTAL1D;

	/**
	 * Create an empty (all-dead) lattice of the given width and height
	 *
	 * @param width  Width of the lattice
	 * @param height Height of the lattice
	 */
	public StaticLattice(int width, int height) {
		this.width = width;
		this.height = height;
		this.lattice = new BitSet(width * height);
	}

	/**
	 * Creates a lattice whichs dimensions and value is copied from the given array of booleans.
	 *
	 * @param lattice Lattice to copy from
	 */
	public StaticLattice(boolean[][] lattice) {
		this.lattice = arrayToBitset(lattice);
		this.width = lattice.length;
		this.height = lattice[0].length;
	}

	/**
	 * Creates a lattice whichs values are copied from the given BitSet.
	 *
	 * @param lattice Lattice to copy from
	 */
	public StaticLattice(BitSet lattice, int width, int height) {
		this.lattice = (BitSet) lattice.clone();
		this.width = width;
		this.height = height;
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

	@Override
	public Connectivity getConnectivity() {
		return connectivity;
	}

	/**
	 * Returns the value of the given cell
	 *
	 * @param i X coordinate of the cell
	 * @param j Y coordinate of the cell
	 * @return Value of the cell at (i,j)
	 */
	public boolean get(int i, int j) {
		i = Math.floorMod(i, getWidth());
		j = Math.floorMod(j, getHeight());
		return (i >= 0 && i < getWidth() && j >= 0 && j < getHeight()) && lattice.get(j + height * i);
	}

	public Color getColor(int i, int j) {
		i = Math.floorMod(i, getWidth());
		j = Math.floorMod(j, getHeight());
		return (i >= 0 && i < getWidth() && j >= 0 && j < getHeight()) && get(i, j) ? Color.GREEN : Color.BLACK;
	}

	/**
	 * Applies the evolution function to the cells of the lattice and returns its successor
	 *
	 * @return The successor of the current configuration by its evolution function
	 */
	public StaticLattice iterate() {
		StaticLattice newLat = new StaticLattice(width, height);
		newLat.connectivity = connectivity;
		for (int i = 0; i < lattice.size(); i++) {
			newLat.lattice.set(i, GameOfLife.evolve2D(get(i, 0), get(i - 1, 0), get(i + 1, 0)));
		}
		return newLat;
	}

	public static BitSet iterate(BitSet set, int size) {
		BitSet newSet = new BitSet(set.size());
		for (int i = 0; i < size; i++) {
			newSet.set(i, GameOfLife.evolve2D(set.get(i), set.get(Math.floorMod(i - 1, size)), set.get(Math.floorMod(i + 1, size))));
		}
		return newSet;
	}

	/**
	 * @return a boolean array represnetation of the underlying bitset of this lattice
	 */
	public boolean[][] getLattice() {
		return bitsetToArray(lattice, width, height);
	}

	public BitSet getBitSet() {
		return (BitSet) lattice.clone();
	}

	/**
	 * Returns a clone of the current Lattice. This is preferrable to new Lattice(lattice.getLattice()), as the latter
	 * will clone the boolean array twice.
	 *
	 * @return A copy of this Lattice object
	 * @throws CloneNotSupportedException
	 */
	@Override
	protected StaticLattice clone() throws CloneNotSupportedException {
		StaticLattice newLat = (StaticLattice) super.clone();
		newLat.lattice = (BitSet) lattice.clone();
		return newLat;
	}

	private int getNeighbours(int i, int j) {
		int result = 0;
		for (int c = 0; c < connectivity.getNeighbours().length; c++)
			result += get(i + connectivity.getNeighbours()[c][0], j + connectivity.getNeighbours()[c][1]) ? 1 : 0;

		return result;
	}

	/**
	 * @return the number of live cells in the lattice
	 */
	public int getCellCount() {
		int count = 0;
		for (int i = 0; i < lattice.size(); i++)
			if (lattice.get(i))
				count++;
		return count;
	}

	public static boolean[][] generateRLattice(int w, int h, int cells) {
		boolean[][] lat = new boolean[w][h];
		for (int i = 0; i < cells; i++) {
			int x, y;
			do {
				x = (int) (Math.random() * w);
				y = (int) (Math.random() * h);
			} while (lat[x][y]);
			lat[x][y] = true;
		}
		return lat;
	}

	@Override
	public int hashCode() {
		return lattice.hashCode();
	}

	private static boolean[][] bitsetToArray(BitSet bitSet, int width, int height) {
		if (bitSet.size() < width * height)
			throw new IllegalArgumentException("Bitset size not equal to width*height");
		boolean[][] array = new boolean[width][height];
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++)
				array[i][j] = bitSet.get(j + height * i);
		return array;
	}

	private static BitSet arrayToBitset(boolean[][] array) {
		BitSet set = new BitSet(array.length * array[0].length);
		int i = 0;

		for (boolean[] line : array)
			for (boolean b : line)
				set.set(i++, b);
		return set;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof StaticLattice && lattice.equals(((StaticLattice) obj).lattice);
	}

	public void setConnectivity(Connectivity connectivity) {
		this.connectivity = connectivity;
	}

	@FunctionalInterface
	public interface LocalEvolutionFunction {
		boolean apply(boolean cell, boolean... neighbours);
	}
}
