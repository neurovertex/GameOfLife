package eu.neurovertex.gol;

import java.awt.*;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;

/**
 * Implementation of Conways's Game of Life local evolution function
 *
 * @author Neurovertex
 *         Date: 18/10/2014, 22:32
 */
public class GameOfLife {
	public static final Random random = new Random(System.currentTimeMillis());
	public static MainWindow window;
	private StaticLattice lattice;

	public GameOfLife(StaticLattice lattice) {
		this.lattice = lattice;

	}

	public Optional<StaticLattice> findPredecessor() {
		boolean[][] array = lattice.getLattice();
		StatLattice predecessor = new StatLattice(new boolean[array.length][array[0].length], array);

		int iter = 0;
		do {
			System.out.printf("%n%d : ", iter);
			int maxi = 0, maxj = 0, maxUnsatisfied = -1, found = 0;
			for (int i = 0; i < array.length; i ++)
				for (int j = 0; j < array[0].length; j ++)
					if (!predecessor.satisfied[i][j] && predecessor.unsatisfiedNeighbours[i][j] >= maxUnsatisfied) {
						if (predecessor.unsatisfiedNeighbours[i][j] > maxUnsatisfied) {
							maxUnsatisfied = predecessor.unsatisfiedNeighbours[i][j];
							maxi = i;
							maxj = j;
							found = 1;
						} else if (random.nextDouble() < 1.0/(++found)){
							maxi = i;
							maxj = j;
						}
					}
			if (random.nextDouble() < 0.3) {
				int[] nudge = Lattice.neighbours[random.nextInt(Lattice.neighbours.length)];
				maxi = Math.floorMod(maxi + nudge[0], array.length);
				maxj = Math.floorMod(maxj + nudge[1], array[0].length);
			}
			predecessor.toggleCell(maxi, maxj);
			System.out.printf("%d:%d", maxi, maxj);
			window.drawLattice(predecessor);
			try {
				Thread.sleep(10);
			} catch (InterruptedException ignore) {
			}
		} while (!Arrays.deepEquals(predecessor.successor, array) && ++iter < 10000);
		System.out.println();
		return (iter < 10000) ? Optional.of(new StaticLattice(predecessor.lat)) : Optional.empty();
	}

	private static class StatLattice implements Lattice {
		private final boolean[][] lat, successor, goal, satisfied;
		private final int[][] liveNeighbours, unsatisfiedNeighbours;

		public StatLattice(boolean[][] lat, boolean[][] goal) {
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
					successor[i][j] = evolve(lat[i][j], liveNeighbours[i][j]);
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
				successor[x][y] = evolve(lat[x][y], liveNeighbours[x][y]);
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

	public static boolean evolve(boolean cell, boolean... neighbours) {
		int living = 0;
		for (boolean st : neighbours)
			if (st)
				living++;

		return evolve(cell, living);
	}

	public static boolean evolve(boolean cell, int neighbours) {
		if (!cell)
			return neighbours == 3;
		else
			return !(neighbours < 2 || neighbours > 3);
	}
}
