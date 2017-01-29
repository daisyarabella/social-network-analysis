package graphGenerators;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import LabelledNode.LabelledNode;

public class bernoulli {
	
    public static UndirectedGraph<LabelledNode, DefaultEdge> createGraph(UndirectedGraph<LabelledNode, DefaultEdge> g, int graphSize, double edgeProb) {
    	g = addAllNodes(g, graphSize, edgeProb); 
    	return g;
    }
    
    private static UndirectedGraph<LabelledNode, DefaultEdge> addAllNodes(UndirectedGraph<LabelledNode, DefaultEdge> g, int graphSize, double edgeProb) {
        
        
        for (int newNodeIndex=0; newNodeIndex<graphSize; newNodeIndex++) {
        	LabelledNode newNode = new LabelledNode(Integer.toString(newNodeIndex), false);
        	g.addVertex(newNode);     	
        }
        Object[] nodes = g.vertexSet().toArray();
        for (int newNodeIndex=0; newNodeIndex<graphSize; newNodeIndex++) {
        	g = formEdges(g, nodes, newNodeIndex, graphSize, edgeProb);
        }
        return g;
    }
    
    private static UndirectedGraph<LabelledNode, DefaultEdge> formEdges(UndirectedGraph<LabelledNode, DefaultEdge> g, Object[] nodes, int newNodeIndex, int graphSize, double edgeProb) {
    	for (int otherNodeIndex=newNodeIndex+1; otherNodeIndex<graphSize; otherNodeIndex++) {
        	if (Math.random() < edgeProb) {
        		LabelledNode newNode = (LabelledNode) nodes[newNodeIndex];
        		LabelledNode otherNode = (LabelledNode) nodes[otherNodeIndex];
       			g.addEdge(newNode, otherNode);
        	}
        }
        return g;
    }
}