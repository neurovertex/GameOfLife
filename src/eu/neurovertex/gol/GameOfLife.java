package eu.neurovertex.gol;

/**
 * Implementation of Conways's Game of Life local evolution function
 *
 * @author Neurovertex
 *         Date: 18/10/2014, 22:32
 */
public class GameOfLife {
	private GameOfLife() {}
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
