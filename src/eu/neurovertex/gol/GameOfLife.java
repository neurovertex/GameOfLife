package eu.neurovertex.gol;

/**
 * Implementation of Conways's Game of Life local evolution function
 * @author Neurovertex
 *         Date: 18/10/2014, 22:32
 */
public class GameOfLife implements CellularAutomaton.LocalEvolutionFunction {
	@Override
	public State apply(State cell, State... neighbours) {
		int living = 0;
		for (State st : neighbours)
			if (st == State.ONE)
				living ++;

		if (cell == State.ZERO)
			return (living == 3) ? State.ONE : State.ZERO;
		else
			return (living < 2 || living > 3) ? State.ZERO : State.ONE;
	}
}
