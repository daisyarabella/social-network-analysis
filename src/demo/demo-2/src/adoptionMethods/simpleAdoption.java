package adoptionMethods;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.NeighborIndex;
import org.jgrapht.ext.ExportException;
import org.jgrapht.graph.DefaultEdge;

import LabelledNode.LabelledNode;

public class simpleAdoption {
	static int Yt = 0; // Y(t)
 	static int Ytadd1 = 0; // Y(t+1)
 	static int intAdoptionCount = 0;
 	static int extAdoptionCount = 0;
 	static int t = 0; 
 	static boolean internalAdoptionHappen = false;
 	
 	public static UndirectedGraph<LabelledNode, DefaultEdge> adopt(UndirectedGraph<LabelledNode, DefaultEdge> graph, double p, NeighborIndex<LabelledNode, DefaultEdge> ni, 
 			int graphSize, FileWriter timestepfw, FileWriter linearfw) throws IOException {
 	do {
     	internalAdoptionHappen = false;
     	if (Yt == 0) {
     		graph = initAdoption(graph, p);
     	}
        try {
        	Exporter.exporter.exportGraphML(graph);
    	} catch (ExportException e) {
    		// TODO Auto-generated catch block
   			e.printStackTrace();
   		}
    	graph = internalAdoption(graph,ni,p);
    	if (!internalAdoptionHappen) {       			
    		graph = externalAdoption(graph,ni,p);			
    	}
        	//exportCSV files
        	timestepfw.write(t + "," + Yt + "," + extAdoptionCount + "," + intAdoptionCount + "\n");
        	linearfw.write(Ytadd1-Yt + "," + 1 + "," + Yt + "," + Yt*Yt + "\n");
        	System.out.println("t: " +t+ "\t Y(t+1): " +Ytadd1+ "\t Y(t): " +Yt);
        	t++;
       	} while (Yt < graphSize);
     	timestepfw.close();
     	linearfw.close();
     	System.out.println("External adoptions: " + extAdoptionCount + ", Internal adoptions: " +intAdoptionCount);
     	System.out.println("Finished");
		return graph;
	}

	public static UndirectedGraph<LabelledNode, DefaultEdge> initAdoption(UndirectedGraph<LabelledNode, DefaultEdge> g, double p) {
    	Object[] nodes = g.vertexSet().toArray(); 
    	for (Object node: nodes) {
    		if (Math.random() < p) {
    	    	((LabelledNode) node).setAdoptedStatus(true);
    			Yt++;
    			Ytadd1++;
    			extAdoptionCount++;
    		}
    	}
    	return g;
    }
    
    // method to make all neighbours of adopted nodes adopted
    private static UndirectedGraph<LabelledNode, DefaultEdge> internalAdoption(UndirectedGraph<LabelledNode, DefaultEdge> g, NeighborIndex<LabelledNode, DefaultEdge> ni, double p) {
    	Yt = Ytadd1;
    	Object[] nodes = g.vertexSet().toArray(); 
    	for (Object node : nodes) {
    		// if node has adopted, getNeighbours of node
    		if (((LabelledNode) node).getAdoptedStatus()) {
    			Set<LabelledNode> neighbors = ni.neighborsOf((LabelledNode) node);
    			// assign all neighbours with adopted attribute
    			for (Object neighbor : neighbors) {
    				if (!((LabelledNode) neighbor).getAdoptedStatus()) {
    					((LabelledNode) neighbor).setAdoptedStatus(true); 
    					Ytadd1++;
    					intAdoptionCount++;
    					internalAdoptionHappen = true;
    				}
    			} 
    		}
    	}
		return g;
    }
    
    private static UndirectedGraph<LabelledNode, DefaultEdge> externalAdoption(UndirectedGraph<LabelledNode, DefaultEdge> g, NeighborIndex<LabelledNode, DefaultEdge> ni, double p) {
    	Yt = Ytadd1;
    	Object[] nodes = g.vertexSet().toArray(); 
    	for (Object node : nodes) {
    		if (!((LabelledNode) node).getAdoptedStatus()) {
    				if (Math.random() < p) {
    					((LabelledNode) node).setAdoptedStatus(true);
    					Ytadd1++;
    					extAdoptionCount++;
    			}
    		}
    	}
		return g;
    } 
}