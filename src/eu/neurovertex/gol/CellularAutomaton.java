package eu.neurovertex.gol;

import java.util.Arrays;
import java.util.Observable;

/**
 * @author Neurovertex
 *         Date: 18/10/2014, 13:49
 */
public class CellularAutomaton extends Observable {
	private static final int[][] neighbours4 = {new int[]{1, 0}, new int[]{0, 1}, new int[]{-1, 0}, new int[]{0, -1}},
								 neighbours8 = {new int[]{1, -1}, new int[]{-1, -1}, new int[]{1, 1}, new int[]{-1, 1}};
	private State[][] lattice;
	private LocalEvolutionFunction function;
	private boolean toroidal, connect8;
	private int width, height;
	private long iteration = 0;

	public CellularAutomaton(int width, int height, LocalEvolutionFunction function, boolean toroidal, boolean connect8) {
		this.width = width;
		this.height = height;
		this.function = function;
		this.toroidal = toroidal;
		this.connect8 = connect8;
		this.lattice = new State[width][height];
		for (int i = 0; i < width; i ++)
			Arrays.fill(lattice[i], State.getDefault());
	}

	public CellularAutomaton(State[][] lattice, LocalEvolutionFunction function, boolean toroidal, boolean connect8) {
		this(lattice.length, lattice[0].length, function, toroidal, connect8);
		for (int i = 0; i < lattice.length; i++)
			System.arraycopy(lattice[i], 0, this.lattice[i], 0, lattice[i].length);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void set(int i, int j, State s) {
		lattice[i][j] = s;
		iteration = 0;
		setChanged();
		notifyObservers("set");
	}

	public State get(int i, int j) {
		if (toroidal) {
			i = Math.floorMod(i, getWidth());
			j = Math.floorMod(j, getHeight());
		}
		return (i >= 0 && i < getWidth() && j >= 0 && j < getHeight()) ? lattice[i][j] : State.getDefault();
	}

	public void iterate() {
		State[][] newLat = new State[width][height];
		for (int i = 0; i < getWidth(); i++)
			for (int j = 0; j < getHeight(); j++)
				newLat[i][j] = function.apply(get(i, j), getNeighbours(i, j));
		this.lattice = newLat;
		iteration ++;
		setChanged();
		notifyObservers("iterate");
	}

	public void randomize(double ratio) {
		for (int i = 0; i < width; i ++)
			for (int j = 0; j < height; j++)
				if (Math.random() < ratio)
					lattice[i][j] = State.values()[((int) (Math.random() * (State.values().length - 1)) + 1)];
				else
					lattice[i][j] = State.getDefault();
		iteration = 0;
		setChanged();
		notifyObservers("rand");
	}

	public void resize(int width, int height) {
		this.width = width;
		this.height = height;
		this.lattice = new State[width][height];
		for (int i = 0; i < width; i ++)
			Arrays.fill(lattice[i], State.getDefault());
		setChanged();
		notifyObservers("resize");
	}

	public void setToroidal(boolean toroidal) {
		this.toroidal = toroidal;
		setChanged();
		notifyObservers("toroidal");
	}

	private State[] getNeighbours(int i, int j) {
		State[] result = new State[connect8 ? neighbours4.length + neighbours8.length : neighbours4.length];
		for (int c = 0; c < neighbours4.length; c++)
			result[c] = get(i + neighbours4[c][0], j + neighbours4[c][1]);
		if (connect8)
			for (int c = 0; c < neighbours8.length; c++)
				result[c+neighbours4.length] = get(i + neighbours8[c][0], j + neighbours8[c][1]);

		return result;
	}

	public LatticeSnapshot snapshot() {
		return new LatticeSnapshot(this);
	}

	public long getIteration() {
		return iteration;
	}

	@FunctionalInterface
	public interface LocalEvolutionFunction {
		State apply(State cell, State... neighbours);
	}

	public static class LatticeSnapshot {
		private final State[] lattice;
		private final int hashcode, width, height;
		private final long iteration;

		private LatticeSnapshot(CellularAutomaton lat) {
			this.width = lat.getWidth();
			this.height = lat.getHeight();
			this.iteration = lat.getIteration();
			lattice = new State[lat.getWidth() * lat.getHeight()];
			for (int i = 0; i < lat.width; i ++)
				System.arraycopy(lat.lattice[i], 0, lattice, i*lat.height, lat.height);
			hashcode = Arrays.hashCode(lattice);
		}

		@Override
		public int hashCode() {
			return hashcode;
		}

		@Override
		public boolean equals(Object obj) {
			return (obj instanceof LatticeSnapshot) && Arrays.equals(lattice, ((LatticeSnapshot) obj).lattice);
		}

		public State get(int i, int j) {
			return lattice[i*height + j];
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}

		public long getIteration() {
			return iteration;
		}
	}
}
