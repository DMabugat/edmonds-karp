package Graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * The Flow Network class allows a directed graph with
 * weighted edges to function as a flow network with
 * flow capacities (as whole numbers).
 * 
 * Main goal of this class is to find the maximum flow
 * of a network through the use of the Edmonds-Karp
 * modification to the Ford-Fulkerson algorithm.
 * 
 * Our search for an augmenting path between the starting
 * node and the sink node is chosen by finding the shortest
 * path through a Breadth-First search of the flow network.
 * The residual graph is then updated with the augmented path's
 * flow. The restriction is that each edge cannot exceed its capacity,
 * but channels flow in the opposite direction proportional to the amount
 * of capacity used in an augmenting path.
 * 
 * @author Daniel Mabugat
 */
public class FlowNetwork {
	private ArrayList<ArrayList<Edge>> graph;
	private int vertices;
	private int start;
	private int sink;
	private int maxFlow;
	
	/**
	 * Private inner class Edge provides structure to
	 * the flow network by containing partial data of 
	 * a directed edge. Only serves to contain the "to"
	 * node as well as the current capacity of the edge.
	 * The "from" node is denoted by the index of the 
	 * outermost ArrayList in the two dimensional ArrayList
	 * structure called "Graph."
	 */
	private class Edge {
		int to;
		int capacity;
		
		Edge(int to, int capacity) {
			this.to = to;
			this.capacity = capacity;
		}
	}
	
	/**
	 * Constructor that initializes the flow network's
	 * various parameters to default values and constructs
	 * the directed graph that will represent the network.
	 * 
	 * @param start			Index of the starting node
	 * @param sink			Index of the sink node
	 * @param graph			Graph data of the flow network layout
	 * 						Integer at [i][j] represents the capacity
	 * 						on a directed edge going from node i to j 
	 */
	public FlowNetwork(int start, int sink, int[][] graph) {
		this.start = start;
		this.sink = sink;
		this.vertices = graph.length;
		this.maxFlow = 0;
		this.graph = new ArrayList<>(vertices);
		
		for (int i = 0; i < vertices; i += 1) {
			this.graph.add(new ArrayList<>());
		}
		
		for (int i = 0; i < vertices; i += 1) {
			for (int j = 0; j < vertices; j += 1) {
				if (graph[i][j] != 0)
					this.graph.get(i).add(new Edge(j, graph[i][j]));
			}
		}
	}
	
	/**
	 * findPath is a method that is responsible for
	 * finding an augmenting path between the starting node
	 * and the sink node while updating the residual graph.
	 * Method is designed to be called within a "While" loop
	 * condition to run indefinitely until we no longer find
	 * an augmenting path, and thus finding the max flow at
	 * the last iteration.
	 * 
	 * @return		Whether or not we find an augmenting path
	 */
	public boolean findPath() {
		int[] parentArray = new int[vertices];
		boolean[] visitedArray = new boolean[vertices];
		Queue<Integer> bfsQueue = new LinkedList<>();
		visitedArray[start] = true;
		bfsQueue.add(start);
		
		// Conduct Breadth-First search to find augmenting path
		while(!bfsQueue.isEmpty()) {
			if (visitedArray[sink]) 
				break;
			for (Edge edge : graph.get(bfsQueue.peek())) {
				if (visitedArray[edge.to])
					continue;
				parentArray[edge.to] = bfsQueue.peek();
				visitedArray[edge.to] = true;
				if (edge.to == sink)
					break;
				bfsQueue.add(edge.to);
			}
			bfsQueue.remove();
		}
		
		// If we find a path, update the residual graph
		if (visitedArray[sink]) {
			updateResidualGraph(parentArray);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Helper method of the FlowNetwork's findPath method. Updates
	 * the residual graph data structure in the FlowNetwork's field.
	 * Takes in an array that symbolizes the augmenting path found in
	 * the findPath method.
	 * 
	 * @param parentArray		Array of node parents where the index
	 * 							of the array corresponds to the node index
	 * 							and the value at the index corresponds to
	 * 							the index of the parent.
	 */
	private void updateResidualGraph(int[] parentArray) {
		int currentNode = sink;
		int currentParent;
		int bottleneck = Integer.MAX_VALUE;
		
		// Find the bottleneck of the augmenting path
		while (currentNode != start) {
			currentParent = parentArray[currentNode];
			Iterator<Edge> e_iter = graph.get(currentParent).iterator();
			Edge e = findEdge(e_iter, currentNode);
			if (e.capacity < bottleneck)
				bottleneck = e.capacity;
			currentNode = currentParent;
		}
		
		// Update the residual graph
		currentNode = sink;
		int diff;
		while (currentNode != start) {
			currentParent = parentArray[currentNode];
			Iterator<Edge> e_iter = graph.get(currentParent).iterator();
			Edge e = findEdge(e_iter, currentNode);
			graph.get(currentParent).remove(e);
			diff = e.capacity - bottleneck;
			e.capacity = diff;
			graph.get(currentNode).add(new Edge(currentParent, bottleneck));
			if (diff != 0)
				graph.get(currentParent).add(e);
			currentNode = currentParent;
		}
		
		// Update the current max flow
		maxFlow += bottleneck;
	}
	
	/**
	 * Helper method for the FlowNetwork's updateResidualGraph
	 * method. Iterates through all accessible nodes from the parent 
	 * node and returns the edge from the parent to the current node.
	 * 
	 * @param edgeIter			Parent node adjacency iterator
	 * @param currentNode		Current node we're searching for
	 * @return					Edge going from parent to current node
	 */
	private Edge findEdge(Iterator<Edge> edgeIter, int currentNode) {
		while(edgeIter.hasNext()) {
			Edge e = edgeIter.next();
			if (e.to == currentNode) {
				return e;
			}
		}
		return new Edge(0, 0);
	}
	
	public int getMaxFlow() {
		return maxFlow;
	}
}
