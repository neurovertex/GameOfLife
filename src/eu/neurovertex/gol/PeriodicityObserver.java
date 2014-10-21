package eu.neurovertex.gol;

import java.util.*;
import java.util.function.Consumer;

import static eu.neurovertex.gol.CellularAutomaton.LatticeSnapshot;

/**
 * @author Neurovertex
 *         Date: 18/10/2014, 23:40
 */
public class PeriodicityObserver
		implements Observer {
	private final Map<Integer, LatticeSnapshot> lattices = new HashMap<>();
	private final CellularAutomaton automaton;
	private boolean observe = true;
	private Optional<Consumer<String>> output;

	public PeriodicityObserver(CellularAutomaton automaton) {
		this.automaton = automaton;
		LatticeSnapshot snap = automaton.snapshot();
		lattices.put(snap.hashCode(), snap);
		automaton.addObserver(this);
	}

	public void setOutput(Consumer<String> output) {
		this.output = Optional.of(output);
	}

	@Override
	public void update(Observable o, Object arg) {
		if (!"iterate".equals(arg)) {
			lattices.clear(); // Si l'on a modifie l'automate par set ou rand, reset l'observateur.
			observe = true;
		}
		if (observe) {
			LatticeSnapshot snap = automaton.snapshot();
			if (lattices.containsKey(snap.hashCode()) && lattices.get(snap.hashCode()).equals(snap)) {
				output.ifPresent(out -> out.accept(String.format("Periodicity detected, period = %d after %d iterations", automaton.getIteration() - lattices.get(snap.hashCode()).getIteration(), automaton.getIteration())));
				observe = false;
			} else
				lattices.put(snap.hashCode(), snap);
		}
	}
}
