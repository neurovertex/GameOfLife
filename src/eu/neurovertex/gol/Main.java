package eu.neurovertex.gol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @author Neurovertex
 *         Date: 18/10/2014, 22:30
 */
public class Main {
	public static void main(String[] args) throws IOException, InterruptedException {
		boolean[][] lat = new boolean[10][10];

		lat[2][1] = lat[1][2] = lat[1][3] = lat[2][3] = lat[3][3] = true; // glider
		StaticLattice lattice = new StaticLattice(lat);
		MainWindow window = new MainWindow();
		GameOfLifeReverser.window = window;
		TransitionGraph graph = new TransitionGraph();
		graph.addNode(lattice);
		System.out.println(graph.calculateLevel(lattice));
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("hashcode : " + lattice.hashCode());
		Optional<StaticLattice> nextLat = Optional.of(lattice);
		String cmd = "";
		Set<StaticLattice> blacklist = new HashSet<>();
		do {
			lattice = nextLat.get();
			window.drawLattice(lattice);
			System.out.printf("hashcode : %d level = %d, successor hashcode : %d%n", lattice.hashCode(), graph.calculateLevel(lattice), lattice.iterate().hashCode());
			nextLat = new GameOfLifeReverser(lattice).pathFinder(blacklist);
			if (nextLat.isPresent() && graph.calculateLevel(nextLat.get()) > 0) {
				if (!nextLat.get().iterate().equals(lattice))
					System.out.println("NOT PREDECESSOR!");
			} else {
				blacklist.add(lattice);
				System.out.printf("No predecessor found or in loop. Blacklisting %d (BL size=%d).%n", lattice.hashCode(), blacklist.size());
				nextLat = Optional.of(lattice.iterate());
			}
			Thread.sleep(250);
			if (reader.ready())
				cmd = reader.readLine();
		} while (!cmd.equals("exit"));
		System.exit(0);
	}
}
