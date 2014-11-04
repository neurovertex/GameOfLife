package eu.neurovertex.gol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

/**
 * @author Neurovertex
 *         Date: 18/10/2014, 22:30
 */
public class Main {
	public static void main(String[] args) throws IOException {
		boolean[][] lat = new boolean[5][5];

		lat[2][1] = lat[1][2] = lat[1][3] = lat[2][3] = lat[3][3] = true; // glider

		StaticLattice lattice = new StaticLattice(lat);
		MainWindow window = new MainWindow();
		GameOfLife.window = window;
		TransitionGraph graph = new TransitionGraph();
		graph.addNode(lattice);
		System.out.println(graph.calculateLevel(lattice));
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("hashcode : " + lattice.hashCode());
		Optional<StaticLattice> nextLat = Optional.of(lattice);
		do {
			lattice = nextLat.get();
			window.drawLattice(lattice);
			System.out.println("hashcode : " + lattice.hashCode() + ", successor hashcode : " + lattice.iterate().hashCode());
			reader.readLine();
			nextLat = new GameOfLife(lattice).findPredecessor();
			if (nextLat.isPresent()) {
				if (!nextLat.get().iterate().equals(lattice))
					System.out.println("NOT PREDECESSOR!");
			}
		} while (nextLat.isPresent());
		System.out.println("No predecessor found");
	}
}
