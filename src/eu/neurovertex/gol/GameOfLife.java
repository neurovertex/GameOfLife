package eu.neurovertex.gol;

/**
 * Implementation of Conways's Game of Life local evolution function
 * @author Neurovertex
 *         Date: 18/10/2014, 22:32
 */
public class GameOfLife implements Lattice.LocalEvolutionFunction {
	@Override
	public boolean apply(boolean cell, boolean... neighbours) {
		int living = 0;
		for (boolean st : neighbours)
			if (st)
				living ++;

		if (!cell)
			return living == 3;
		else
			return !(living < 2 || living > 3);
	}
}
