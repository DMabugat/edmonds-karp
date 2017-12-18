// Copyright (c) 2017 Daniel Mabugat
// Distributed under the MIT software license, see the accompanying
// file LICENSE or http://www.opensource.org/licenses/mit-license.php

import Graph.FlowNetwork;

public class source {
	
	public static void main (String[] args) {
		int[][] graph = { {0, 7, 5, 0, 0, 0},
						  {0, 0, 5, 4, 0, 0},
						  {0, 0, 0, 6, 4, 0},
						  {0, 0, 0, 0, 2, 5},
						  {0, 0, 0, 0, 0, 6},
						  {0, 0, 0, 0, 0, 0} };
		FlowNetwork network = new FlowNetwork(0, 5, graph);
		while(network.findPath()) {}
		System.out.printf("Max flow: %d\n", network.getMaxFlow());
	}
}
