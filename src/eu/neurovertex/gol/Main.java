package eu.neurovertex.gol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Neurovertex
 *         Date: 18/10/2014, 22:30
 */
public class Main {
	public static void main(String[] args) throws IOException {
		boolean[][] lat = new boolean[5][6];

		lat[1][3] = lat[2][3] = lat[3][3] = true; // horizontal bar
		lat[0][0] = true;

		Lattice lattice = new Lattice(lat);
		MainWindow window = new MainWindow();
		TransitionGraph graph = new TransitionGraph();
		graph.addNode(lattice);
		System.out.println(graph.calculateLevel(lattice));
		window.drawLattice(lattice);
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String line;
		System.out.println("hashcode : "+ lattice.hashCode());
		do {
			line = reader.readLine();
			lattice = lattice.iterate();
			window.drawLattice(lattice);
			System.out.println("hashcode : "+ lattice.hashCode());
		} while (!line.equals("exit"));
	}
}
