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
		StatLattice predecessor = new StatLattice(array, array);

		int iter = 0;
		do {
			System.out.printf("%n%d : ", iter);
			int i, j;
			do {
				i = random.nextInt(array.length);
				j = random.nextInt(array[0].length);
			} while (predecessor.successor[i][j] == array[i][j]);
			predecessor.toggleCell(i, j);
			System.out.printf("%d:%d", i, j);
			window.drawLattice(predecessor);
			try {
				Thread.sleep(100);
			} catch (InterruptedException ignore) {}
		} while (!Arrays.deepEquals(predecessor.successor, array) && ++iter < 10000);
		System.out.println();
		return (iter < 10000) ? Optional.of(new StaticLattice(predecessor.lat)) : Optional.empty();
	}

	private static class StatLattice implements Lattice {
		private final boolean[][] lat, successor, goal;
		private final int[][] neighbours;

		public StatLattice(boolean[][] lat, boolean[][] goal) {
			this.lat = lat.clone();
			this.goal = goal.clone();
			int w = lat.length, h = lat[0].length;
			successor = new boolean[w][h];
			neighbours = new int[w][h];
			for (int i = 0; i < w; i++)
				for (int j = 0; j < h; j++)
					if (lat[i][j])
						for (int[] diff : StaticLattice.neighbours) {
							neighbours[Math.floorMod(i + diff[0], w)][Math.floorMod(j + diff[1], h)]++;
						}

			for (int i = 0; i < w; i++)
				for (int j = 0; j < h; j++)
					successor[i][j] = evolve(lat[i][j], neighbours[i][j]);
		}

		public void toggleCell(int i, int j) {
			int inc = lat[i][j] ? -1 : 1;
			for (int[] diff : StaticLattice.neighbours) {
				int x = Math.floorMod(i + diff[0], lat.length), y = Math.floorMod(j + diff[1], lat[0].length);
				neighbours[x][y] += inc;
				successor[x][y] = evolve(lat[x][y], neighbours[x][y]);
			}
			lat[i][j] = !lat[i][j];
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
