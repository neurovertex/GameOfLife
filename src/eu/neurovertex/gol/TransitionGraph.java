package eu.neurovertex.gol;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 * User: Jeremie
 * Date: 29/10/2014
 * Time: 18:53
 */
public class TransitionGraph {
	private Map<Lattice, GraphNode> map = new HashMap<>();

	public TransitionGraph() {

	}

	public void addNode(Lattice node) {
		addOrGet(node);
	}

	private GraphNode addOrGet(Lattice node) {
		GraphNode n = map.get(node);
		if (n == null)
			n = add(node);
		return n;
	}

	private GraphNode add(Lattice node) {
		GraphNode n = new GraphNode(node);
		map.put(node, n);
		return n;
	}

	public boolean contains(Lattice node) {
		return map.containsKey(node);
	}

	public Optional<Lattice> getSuccessor(Lattice node) {
		return Optional.of(map.get(node)).flatMap(n -> n.successor).map(n -> n.node);
	}

	public List<Lattice> getPredecessors(Lattice node) {
		return map.get(node).predecessors.stream().map(g -> g.node).collect(Collectors.toList());
	}

	public int getLevel(Lattice node) {
		return map.get(node).level;
	}

	public void setSuccessor(Lattice node, Lattice successor) {
		GraphNode n = map.get(node);
		if (n != null)
			n.successor = Optional.of(addOrGet(successor));
	}

	public void addPredecessor(Lattice node, Lattice predecessor) {
		GraphNode n = map.get(node);
		if (n != null)
			n.predecessors.add(addOrGet(predecessor));
	}

	public int calculateLevel(Lattice node) {
		return calculateLevel(addOrGet(node));
	}

	private int calculateLevel(GraphNode node) {
		if (node.level == -1) {
			Deque<GraphNode> lifo = new LinkedList<>();
			lifo.push(node);
			GraphNode current = node, next;
			boolean loopFound = false;
			int level = 0;
			while (!loopFound) {
				Lattice child = null;
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
				do {
					next = lifo.pop();
					next.level = 0;
				} while (next != current && lifo.size() > 0);
			while (lifo.size() > 0)
				lifo.pop().level = ++level;
		}
		return node.level;
	}

	private class GraphNode {
		private Lattice node;
		private Optional<GraphNode> successor = Optional.empty();
		private Collection<GraphNode> predecessors = new HashSet<>();
		private int level = -1;

		public GraphNode(Lattice node) {
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
