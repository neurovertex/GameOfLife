package eu.neurovertex.gol;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is a cache of all states already seen and calculated, as to avoid redundancy upon calculating successors.
 *
 * @author Neurovertex
 */
public class TransitionGraph {
	private Map<StaticLattice, GraphNode> map = new HashMap<>();

	public TransitionGraph() {
	}

	/**
	 * Add the given lattice as a node to the graph. If it already was a node, does nothing.
	 *
	 * @param node Lattice to add
	 */
	public void addNode(StaticLattice node) {
		addOrGet(node);
	}

	private GraphNode addOrGet(StaticLattice node) {
		GraphNode n = map.get(node);
		if (n == null)
			n = add(node);
		return n;
	}

	private GraphNode add(StaticLattice node) {
		GraphNode n = new GraphNode(node);
		map.put(node, n);
		return n;
	}

	/**
	 * Returns whether the given lattice exists within the transition graph
	 *
	 * @param node Lattice to search for
	 * @return True if the given lattice exists within the graph, false otherwise.
	 */
	public boolean contains(StaticLattice node) {
		return map.containsKey(node);
	}

	/**
	 * Returns an optional lattice that is the successor of the given node
	 *
	 * @param node Lattice to look up
	 * @return An Optional of the lattice succeeding to the given one, or an empty Optional if given node isn't
	 * in the graph, or if its successor isn't known.
	 */
	public Optional<StaticLattice> getSuccessor(StaticLattice node) {
		return Optional.of(map.get(node)).flatMap(n -> n.successor).map(n -> n.node);
	}

	/**
	 * Returns all known predecessors of the given node.
	 *
	 * @param node Lattice to look up
	 * @return A list of the knwon lattices in the graph that have the given lattice as successor
	 */
	public List<StaticLattice> getPredecessors(StaticLattice node) {
		return map.get(node).predecessors.stream().map(g -> g.node).collect(Collectors.toList());
	}

	/**
	 * Returns how far the given lattice is from its stable loop. A configuration that is part of its stable loop has
	 * a level of 0, any other configuration has a level equal to its successor's plus one.
	 *
	 * @param node Lattice to look up
	 * @return The level if it is known, -1 otherwise.
	 * @see eu.neurovertex.gol.TransitionGraph#calculateLevel(StaticLattice)
	 */
	public int getLevel(StaticLattice node) {
		return map.get(node).level;
	}

	/**
	 * Sets the latter parameter as the successor of the former, and add the former as predecessor of the latter. Either
	 * or both node will be added to the graph if not already present.
	 *
	 * @param node      Parent node
	 * @param successor Successor
	 */
	public void setSuccessor(StaticLattice node, StaticLattice successor) {
		GraphNode parent = addOrGet(node), succ = addOrGet(successor);
		parent.successor = Optional.of(succ);
		succ.predecessors.add(parent);
	}

	/**
	 * Returns the level of the given lattice in the graph as defined by getLevel(Lattice). Unlike getLevel, this function
	 * will calculate the level if it is unknown, adding all necessary successors to the graph until it reaches stability.
	 *
	 * @param node Lattice to look up
	 * @return The level of the given lattice in the graph
	 * @see eu.neurovertex.gol.TransitionGraph#getLevel(StaticLattice)
	 */
	public int calculateLevel(StaticLattice node) {
		return calculateLevel(addOrGet(node));
	}

	private int calculateLevel(GraphNode node) {
		if (node.level == -1) {
			Deque<GraphNode> lifo = new LinkedList<>();
			lifo.push(node);
			GraphNode current = node, next;
			boolean loopFound = false;
			int level = 0;
			while (!loopFound) { // Iterates until stability or until a known node is found
				StaticLattice child = null;
				next = (current.successor.isPresent()) ?
						current.successor.get() :
						map.get(child = current.node.iterate());
				if (next == null) {
					next = add(child);
					lifo.push(next);
				} else {
					if (lifo.contains(next)) // Loop reached
						loopFound = true;
					else if (next.level != -1) {
						level = next.level;
						break;
					} else
						lifo.push(next);
				}
				if (!current.successor.isPresent()) {
					next.predecessors.add(current);
					current.successor = Optional.of(next);
				}
				current = next;
			}
			if (loopFound)
				do { // Unstacks all elements of the LIFO that are part of the loop
					next = lifo.pop();
					next.level = 0;
				} while (next != current && lifo.size() > 0);
			while (lifo.size() > 0) // Unstacks the rest of the LIFO
				lifo.pop().level = ++level;
		}
		return node.level;
	}

	private class GraphNode {
		private StaticLattice node;
		private Optional<GraphNode> successor = Optional.empty();
		private Collection<GraphNode> predecessors = new HashSet<>();
		private int level = -1;

		public GraphNode(StaticLattice node) {
			this.node = node;
		}

		@Override
		public int hashCode() {
			return node.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof GraphNode && node.equals(((GraphNode) obj).node);
		}
	}
}
