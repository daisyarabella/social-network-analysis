import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Set;

import java.awt.List;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.NeighborIndex;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.ext.ExportException;
import org.jgrapht.ext.GraphMLExporter;

import LabelledNode.LabelledNode;
import NameProvider.NameProvider;
import timestepData.timestepData;

public class preferentialSimple
{
 	static int Yt = 0; // Y(t)
 	static int Ytadd1 = 0; // Y(t+1)
 	static int intAdoptionCount = 0;
 	static int extAdoptionCount = 0;
 	static int t = 0; 
 	static int fileCount = 1; // to create iteration graphs
 	static boolean internalAdoptionHappen = false;
 	static int currentGraphSize = 0;
 	static int totalNoEdges = 0;
    
    public static void main(String [] args) throws ExportException, IOException
    {
    	File timestepData = new File("../../../data/PStimestepData.csv"); // file to record timestep data for plotting simple line graph
    	File linearEqs = new File("../../../data/PSlinearEqs.csv"); // file to be read by python to calculate p and q. Includes S(t+1) data, and coefficients of a, b, c
    	FileWriter timestepfw = new FileWriter(timestepData.getAbsoluteFile());
    	FileWriter linearfw = new FileWriter(linearEqs.getAbsoluteFile());
    	timestepfw.write("t,Y(t),External Adopters,Internal Adopters\n");
    	linearfw.write("S(t+1),aCo,bCo,cCo\n");
    	
    	Scanner scanner = new Scanner(System.in);
        System.out.println("How many nodes?");
        int graphSize = scanner.nextInt();
        System.out.println("Set coefficient of innovation (p):");
        double p = scanner.nextDouble();
         
    	//create a graph
        UndirectedGraph<LabelledNode, DefaultEdge> graph = initRandomGraph(graphSize);
        NeighborIndex<LabelledNode, DefaultEdge> ni = new NeighborIndex(graph);
        
     	
     	// Simple adoption
     	do {
     		internalAdoptionHappen = false;
     		if (Yt == 0) {
     			graph = initAdoption(graph, p);
     		}
         	try {
    			exportGraphML(graph);
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
     
    }

    
    // method to create an initial graph for future manipulation
    private static UndirectedGraph<LabelledNode, DefaultEdge> initRandomGraph(int graphSize)
    {
        UndirectedGraph<LabelledNode, DefaultEdge> g = new SimpleGraph<LabelledNode, DefaultEdge>(DefaultEdge.class);
        
        g = preferentialAttachment(g, graphSize);
        
        return g;
    }
    
    private static UndirectedGraph<LabelledNode, DefaultEdge> preferentialAttachment(UndirectedGraph<LabelledNode, DefaultEdge> g, int graphSize) {
        int totalEdges = 0;
        
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
        	g = formPrefAttEdges(g, newNode, nodes, newNodeIndex);
        }
        return g;
    }
    
    private static UndirectedGraph<LabelledNode, DefaultEdge> formPrefAttEdges(UndirectedGraph<LabelledNode, DefaultEdge> g, LabelledNode newNode, Object[] nodes, int newNodeIndex) {
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
            			totalNoEdges++;
        			}
    			}
        	}
    	}
    return g;
    }
    
    // method for initial adoption
    private static UndirectedGraph<LabelledNode, DefaultEdge> initAdoption(UndirectedGraph<LabelledNode, DefaultEdge> g, double p) {
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
    
    // export a graph into graphML format for use in Visone
    private static void exportGraphML(UndirectedGraph<LabelledNode, DefaultEdge> g) throws ExportException {
    	try {
			File file = new File("../../../graph-iterations/iteration" +fileCount+ ".graphml");

			// if file doesn't exist, create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			NameProvider np = new NameProvider();
			GraphMLExporter<LabelledNode, DefaultEdge> exporter = new GraphMLExporter<LabelledNode, DefaultEdge>();
	        exporter.setVertexLabelProvider(np);
	        exporter.exportGraph(g, fw);
			fileCount++;
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
