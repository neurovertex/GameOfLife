package eu.neurovertex.gol;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Neurovertex
 *         Date: 18/10/2014, 22:30
 */
public class Main {
	public static void main(String[] args) throws IOException {
		final CellularAutomaton automaton = new CellularAutomaton(100, 100, new GameOfLife(), true, true);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | UnsupportedLookAndFeelException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}

		MainWindow window = new MainWindow();
		window.getAutomatonPanel().setAutomaton(automaton);
		new PeriodicityObserver(automaton).setOutput(window::setOutput); // Set the window's output bar upon detecting periodicity
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		reader.readLine();

	}
}
