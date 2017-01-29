package graphGenerators;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import LabelledNode.LabelledNode;

public class preferentialAttachment {

	static int currentGraphSize = 0;
	static int totalNoEdges = 0;
	
    public static UndirectedGraph<LabelledNode, DefaultEdge> createGraph(UndirectedGraph<LabelledNode, DefaultEdge> g, int graphSize) {
        for (int newNodeIndex=0; newNodeIndex<graphSize; newNodeIndex++) {
        	g = addOneNode(g, newNodeIndex); 
        	currentGraphSize++;
        }
    	return g;
    }
    
    private static UndirectedGraph<LabelledNode, DefaultEdge> addOneNode(UndirectedGraph<LabelledNode, DefaultEdge> g, int newNodeIndex) {
    	LabelledNode newNode = new LabelledNode(Integer.toString(newNodeIndex), false);
    	g.addVertex(newNode);
        Object[] nodes = g.vertexSet().toArray();
        if (newNodeIndex != 0) {
        	g = formEdges(g, newNode, nodes, newNodeIndex);
        }
        return g;
    }
    
    private static UndirectedGraph<LabelledNode, DefaultEdge> formEdges(UndirectedGraph<LabelledNode, DefaultEdge> g, LabelledNode newNode, Object[] nodes, int newNodeIndex) {
    	for (int otherNodeIndex=0; otherNodeIndex<currentGraphSize; otherNodeIndex++) {
        	if (newNodeIndex != otherNodeIndex) {
    			LabelledNode otherNode = (LabelledNode) nodes[otherNodeIndex];
    			
    			if (totalNoEdges == 0) {
    				g.addEdge(newNode, otherNode);
    				totalNoEdges++;
    			} 
    			else {
    				double degreeOfOtherNode = g.degreeOf(otherNode);
    				double edgesTimesTwo = totalNoEdges*2;
    				double edgeProb = (degreeOfOtherNode/edgesTimesTwo);
    				if (Math.random() < edgeProb) {
            			g.addEdge(newNode, otherNode);	
            			//System.out.println("added edge between" + newNodeIndex + "and " +otherNodeIndex);
            			totalNoEdges++;
        			}
    			}
        	}
    	}
    	return g;
    }
}