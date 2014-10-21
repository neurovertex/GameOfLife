package eu.neurovertex.gol;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.IntStream;

/**
 * @author Neurovertex
 *         Date: 20/10/2014, 23:15
 */
public class BatchTest {
	private static final int ITERATIONS_PER_CONFIG = 8;
	private static double ratio;
	private static int size;
	private static CellularAutomaton.LocalEvolutionFunction function = new GameOfLife();

	private boolean foundLoop = false;

	public static void main(String[] args) throws IOException {
		PrintWriter out = new PrintWriter(new File("output.csv"));
		out.append("Resultats,");
		for (size = 10; size <= 100; size += 10)
			out.append(String.valueOf(size)).append("x").append(String.valueOf(size)).append(",");
		out.println();
		for (int r = 10; r < 80; r ++) {
			ratio = r/100.0;
			System.out.printf("Ratio %f%n", ratio);
			out.append(String.valueOf(ratio)).append(", ");
			for (size = 10; size <= 100; size += 10) {
				long average = (long)IntStream.range(0, ITERATIONS_PER_CONFIG).parallel().mapToLong(i -> iterateUntilLoop(ratio)).average().getAsDouble();
				System.out.printf("\t%d : %d%n", size, average);
				out.append(String.valueOf(average)).append(", ");
			}
			out.println();
		}
		out.close();
	}

	public static long iterateUntilLoop(double ratio) {
		CellularAutomaton automaton = new CellularAutomaton(size, size, function, true, true);
		final BatchTest test = new BatchTest(); // Wrapper around the boolean, so that the lambda below can alter its value
		new PeriodicityObserver(automaton).setOutput(e -> test.foundLoop = true);
		automaton.randomize(ratio);
		while (!test.foundLoop) {
			automaton.iterate();
		}
		return automaton.getIteration();
	}
}
