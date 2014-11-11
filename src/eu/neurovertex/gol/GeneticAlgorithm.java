package eu.neurovertex.gol;

import java.util.*;


@SuppressWarnings("ALL")
public class GeneticAlgorithm {
	public static Random random = new Random(2);

	public static StaticLattice generateRandomLattice(int size) {
		boolean[][] lattice = new boolean[size][size];
		for (int i = 0; i < 50; ++i) {
			int x = random.nextInt(size);
			int y = random.nextInt(size);
			if (lattice[x][y]) {
				i--;
			}
			lattice[x][y] = true;
		}
		return new StaticLattice(lattice);
	}

	public static StaticLattice mutation(StaticLattice individu, int nbMutations) {
		boolean[][] array = individu.getLattice();
		for (int i = 0; i < nbMutations; ++i) {
			int x = random.nextInt(individu.getWidth());
			int y = random.nextInt(individu.getHeight());
			array[x][y] = !array[x][y];
		}

		return new StaticLattice(array);
	}

	public static void afficheLattice(StaticLattice individu) {
		boolean[][] array = individu.getLattice();
		for (int y = 0; y < individu.getHeight(); ++y) {
			for (int x = 0; x < individu.getWidth(); ++x) {
				if (array[x][y]) {
					System.out.print("1 ");
				} else {
					System.out.print("0 ");
				}
			}
			System.out.print("\n");
		}
	}

	public static void main(String[] args) {

		final int TAILLEGRILLE = 100, TAILLEPOP = 15,
				NBGENERATIONS = 25, NBMUTATIONS = 10;

		List<StaticLattice> generation = new ArrayList<>(50);
		for (int i = 0; i < TAILLEPOP; ++i) {
			generation.add(generateRandomLattice(TAILLEGRILLE));
		}

		TransitionGraph graph = new TransitionGraph();
		for (int i = 0; i < NBGENERATIONS; ++i) {
			StaticLattice meilleur = generation.get(0);
			int max = graph.calculateLevel(meilleur);
			for (int j = 1; j < generation.size(); j++) {
				int level = graph.calculateLevel(generation.get(j));
				if (level > max) {
					max = level;
					meilleur = generation.get(j);
				}
			}
			System.out.printf("Generation %d, meilleur : %d%n", i, graph.calculateLevel(meilleur));
			//afficheLattice(meilleur);
			generation.clear();
			generation.add(meilleur);
			for (int j = 0; j < TAILLEPOP / 3; ++j) {
				generation.add(mutation(meilleur, NBMUTATIONS));
			}
			for (int j = 0; j < TAILLEPOP / 3; ++j) {
				generation.add(mutation(meilleur, NBMUTATIONS * 5));
			}
			for (int j = 0; j < TAILLEPOP - (2 * TAILLEPOP / 3 + 1); ++j) {
				generation.add(mutation(meilleur, NBMUTATIONS * 10));
			}
		}
		Collections.sort(generation, Comparator.comparingInt(graph::calculateLevel));
		System.out.println(graph.calculateLevel(generation.get(generation.size() - 1)));
		afficheLattice(generation.get(generation.size() - 1));

	}
}
