package eu.neurovertex.gol;

import java.awt.*;
import java.util.*;
import java.util.function.Consumer;

/**
 * @author Neurovertex
 *         Date: 04/11/2014, 17:14
 */
public class GameOfLifeReverser {
	public static final Random random = new Random(System.currentTimeMillis());
	public static MainWindow window;
	private final StaticLattice goalLattice;

	public GameOfLifeReverser(StaticLattice goalLattice) {
		this.goalLattice = goalLattice;
	}

	/* STOCHASTIC SEARCH */

	public Optional<StaticLattice> stochasticSearch() {
		boolean[][] array = goalLattice.getLattice();
		StochasticStatLattice predecessor = new StochasticStatLattice(new boolean[array.length][array[0].length], array);

		int iter = 0;
		do {
			System.out.printf("%n%d : ", iter);
			int x = 0, y = 0, unsatisfied = -1;
			for (int i = 0; i < array.length; i++)
				for (int j = 0; j < array[0].length; j++)
					if (!predecessor.satisfied[i][j] && (unsatisfied == -1 || random.nextInt(20) < (8 - unsatisfied + predecessor.unsatisfiedNeighbours[i][j]))) {
						unsatisfied = predecessor.unsatisfiedNeighbours[i][j];
						x = i;
						y = j;
					}
			if (random.nextDouble() < 0.3) {
				int[] nudge = Lattice.neighbours[random.nextInt(Lattice.neighbours.length)];
				x = Math.floorMod(x + nudge[0], array.length);
				y = Math.floorMod(y + nudge[1], array[0].length);
			}
			predecessor.toggleCell(x, y);
			System.out.printf("%d:%d", x, y);
			window.drawLattice(predecessor);
			try {
				Thread.sleep(10);
			} catch (InterruptedException ignore) {
			}
		} while (!Arrays.deepEquals(predecessor.successor, array) && ++iter < 10000);
		System.out.println();
		return (iter < 10000) ? Optional.of(new StaticLattice(predecessor.lat)) : Optional.empty();
	}

	static class StochasticStatLattice implements Lattice {
		private final boolean[][] lat, successor, goal, satisfied;
		private final int[][] liveNeighbours, unsatisfiedNeighbours;

		public StochasticStatLattice(boolean[][] lat, boolean[][] goal) {
			this.lat = Lattice.cloneArray(lat);
			this.goal = Lattice.cloneArray(goal);
			int w = lat.length, h = lat[0].length;
			successor = new boolean[w][h];
			satisfied = new boolean[w][h];
			liveNeighbours = new int[w][h];
			unsatisfiedNeighbours = new int[w][h];
			for (int i = 0; i < w; i++)
				for (int j = 0; j < h; j++)
					if (lat[i][j])
						for (int[] diff : Lattice.neighbours) {
							liveNeighbours[Math.floorMod(i + diff[0], w)][Math.floorMod(j + diff[1], h)]++;
						}

			for (int i = 0; i < w; i++)
				for (int j = 0; j < h; j++) {
					successor[i][j] = GameOfLife.evolve(lat[i][j], liveNeighbours[i][j]);
					satisfied[i][j] = successor[i][j] == goal[i][j];
					if (!satisfied[i][j])
						for (int[] diff : Lattice.neighbours)
							unsatisfiedNeighbours[Math.floorMod(i + diff[0], w)][Math.floorMod(j + diff[1], h)]++;
				}
		}

		public void toggleCell(int i, int j) {
			int inc = lat[i][j] ? -1 : 1;
			lat[i][j] = !lat[i][j];
			for (int[] diff : Lattice.neighbours) {
				int x = Math.floorMod(i + diff[0], lat.length), y = Math.floorMod(j + diff[1], lat[0].length);
				liveNeighbours[x][y] += inc;
				successor[x][y] = GameOfLife.evolve(lat[x][y], liveNeighbours[x][y]);
				if (satisfied[x][y] != (successor[x][y] == goal[x][y])) {
					int signus = satisfied[x][y] ? -1 : 1;
					for (int[] d : Lattice.neighbours)
						unsatisfiedNeighbours[Math.floorMod(i + d[0], getWidth())][Math.floorMod(j + d[1], getHeight())] += signus;
				}
				satisfied[x][y] = (successor[x][y] == goal[x][y]);
			}
		}

		@Override
		public boolean get(int i, int j) {
			return lat[i][j];
		}

		@Override
		public Color getColor(int i, int j) {
			Color col = successor[i][j] == goal[i][j] ? Color.GREEN : Color.RED;
			if (!lat[i][j])
				col = col.darker().darker().darker();
			return col;
		}

		@Override
		public int getWidth() {
			return lat.length;
		}

		@Override
		public int getHeight() {
			return lat[0].length;
		}
	}

	/* PATHFINDER SEARCH */

	public Optional<StaticLattice> pathFinder() {
		return pathFinder(new HashSet<>());
	}

	public Optional<StaticLattice> pathFinder(Set<StaticLattice> blacklist) {
		SortedSet<StatLattice> set = new TreeSet<>();
		Set<StaticLattice> tested = new HashSet<>();
		tested.addAll(blacklist);
		set.add(new StatLattice(goalLattice, goalLattice.getCellCount()));

		int iter = 0;
		while ((set.size() > 0) && (((set.first().unsatisfied > 0) && (iter < 10000)) || blacklist.contains(set.first().lattice))) {
			StatLattice first = set.first();
			set.remove(first);
			if (!blacklist.contains(first.lattice)) {
				generateNeighbours(first, set::add, tested);
				if (iter%1000 == 0)
					System.out.printf("Iter %d : dist=%d, size=%d%n", iter, set.first().unsatisfied, set.size());
			}
			tested.add(first.lattice);
			iter++;
		}

		if (set.size() > 0 && set.first().unsatisfied == 0)
			return Optional.of(set.first().lattice);
		else
			return Optional.empty();
	}

	private void generateNeighbours(StatLattice parent, Consumer<? super StatLattice> consumer, Set<StaticLattice> avoid) {
		boolean[][] base = parent.lattice.getLattice();
		int count = parent.lattice.getCellCount();
		StaticLattice lat;
		for (int i = 0; i < parent.getWidth(); i++)
			for (int j = 0; j < parent.getWidth(); j++) {
				base[i][j] = !base[i][j];
				lat = new StaticLattice(base);
				if (!avoid.contains(lat))
					consumer.accept(new StatLattice(lat, count + (base[i][j] ? 1 : -1)));
				base[i][j] = !base[i][j];
			}
	}

	private class StatLattice implements Comparable<StatLattice>, Lattice {
		private final StaticLattice lattice;
		private final int unsatisfied, count;

		public StatLattice(StaticLattice lat, int count) {
			this.lattice = lat;
			StaticLattice successor = lat.iterate();
			int unsatis = 0;
			for (int i = 0; i < lat.getWidth(); i++)
				for (int j = 0; j < lat.getHeight(); j++)
					if (successor.get(i, j) != goalLattice.get(i, j))
						unsatis++;
			unsatisfied = unsatis;
			this.count = count;
		}

		@Override
		public int compareTo(StatLattice o) {
			if (unsatisfied != o.unsatisfied)
				return Integer.compare(unsatisfied, o.unsatisfied);
			else if (count != o.count)
				return Integer.compare(count, o.count);
			else
				return Integer.compare(hashCode(), o.hashCode()); // To avoid returning 0, as SortedSets consider it as meaning objects are the same
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof StatLattice && lattice.equals(((StatLattice) obj).lattice);
		}

		@Override
		public int hashCode() {
			return lattice.hashCode();
		}

		@Override
		public int getWidth() {
			return lattice.getWidth();
		}

		@Override
		public int getHeight() {
			return lattice.getHeight();
		}

		@Override
		public boolean get(int i, int j) {
			return lattice.get(i, j);
		}

		@Override
		public Color getColor(int i, int j) {
			return lattice.getColor(i, j);
		}
	}
}
