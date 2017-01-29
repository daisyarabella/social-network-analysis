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

public class simple
{
	// Choose values for p and q based on study by Sultan, Farley, and Lehmann in 1990
 	static double p = 0.03; // Fixed coefficient of innovation
 	static double q = 0.2; // Fixed coefficient of imitation
 	static int totalAdopters = 0; // Y(t)
 	static int totalAdoptersAdd1 = 0; // Y(t+1)
 	static int totalPAdopters = 0; 
 	static int totalQAdopters = 0;
 	static int t = 0; 
 	static int fileCount = 1; // to create iteration graphs
 	
    public static void main(String [] args) throws ExportException, IOException
    {
    	File timestepData = new File("../data/timestepData.csv"); // file to record timestep data for plotting simple line graph
    	File linearEqs = new File("../data/linearEqs.csv"); // file to be read by python to calculate p and q. Includes S(t+1) data, and coefficients of a, b, c
    	FileWriter timestepfw = new FileWriter(timestepData.getAbsoluteFile());
    	FileWriter linearfw = new FileWriter(linearEqs.getAbsoluteFile());
    	timestepfw.write("t,Y(t+1),Y(t),Total P Adopters,Total Q Adopters\n");
    	linearfw.write("S(t+1),aCo,bCo,cCo\n");
    	
    	Scanner scanner = new Scanner(System.in);
        System.out.println("How many nodes?");
        int noNodes = scanner.nextInt();
        System.out.println("Insert edge probability:");
        double edgeProb = scanner.nextDouble();
         
    	//create a graph
        UndirectedGraph<LabelledNode, DefaultEdge> graph = initRandomGraph(noNodes, edgeProb);
        NeighborIndex<LabelledNode, DefaultEdge> ni = new NeighborIndex(graph);
        
     	
     	// Simple adoption
     	do {
     		adoption(graph,ni); // iteratively adopt by p and q until everyone is adopted
         	try {
    			exportGraphML(graph);
    		} catch (ExportException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
         	//exportCSV files
         	timestepfw.write(t + "," + totalAdoptersAdd1 + "," + totalAdopters + "," + totalPAdopters + "," + totalQAdopters +"\n");
         	linearfw.write(totalAdoptersAdd1-totalAdopters + "," + 1 + "," + totalAdopters + "," + totalAdopters*totalAdopters + "\n");
         	System.out.println("t: " +t+ "\t Y(t+1): " +totalAdoptersAdd1+ "\t Y(t): " +totalAdopters+  "\t p Adopters: " +totalPAdopters+ "\t q adopters: " +totalQAdopters);
     		t++;
        } while (totalAdopters < noNodes);
     	timestepfw.close();
     	linearfw.close();
     	
    }

    
    // method to create an initial graph for future manipulation
    private static UndirectedGraph<LabelledNode, DefaultEdge> initRandomGraph(int noNodes, double edgeProb)
    {
        UndirectedGraph<LabelledNode, DefaultEdge> g = new SimpleGraph<LabelledNode, DefaultEdge>(DefaultEdge.class);

        //add specified number of nodes
        for (int i=1; i<=noNodes; i++) {
        	LabelledNode node = new LabelledNode(Integer.toString(i), false);
        	g.addVertex(node);
        }
        
        //add x% of random edges
        Object[] nodes = g.vertexSet().toArray(); 
        for (int i=0; i<noNodes; i++) { 
        	for (int j=i+1; j<noNodes; j++) {
        		if (Math.random() < edgeProb) {
        			LabelledNode edgeNodeA = (LabelledNode) nodes[i];
        			LabelledNode edgeNodeB = (LabelledNode) nodes[j];
       				g.addEdge(edgeNodeA, edgeNodeB);
        		}
        	}
        }
        return g;
    }
    
    // method to make nodes adopted by p and q
    private static UndirectedGraph<LabelledNode, DefaultEdge> adoption(UndirectedGraph<LabelledNode, DefaultEdge> g, NeighborIndex<LabelledNode, DefaultEdge> ni) {
    	totalAdopters = totalAdoptersAdd1;
    	Object[] nodes = g.vertexSet().toArray(); 
    	for (Object node : nodes) {
    		// if node has adopted, getNeighbours of node
    		if (((LabelledNode) node).getAdoptedStatus()) {
    			Set<LabelledNode> neighbors = ni.neighborsOf((LabelledNode) node);
				// assign q% neighbours with adopted attribute
				for (Object neighbor : neighbors) {
					if (Math.random() < q && !((LabelledNode) neighbor).getAdoptedStatus()) {
						((LabelledNode) neighbor).setAdoptedStatus(true); 
						totalQAdopters++;
						totalAdoptersAdd1++;
					}
				}
			}
    		// make a further p of the non-adopted nodes adopted
    		else if (Math.random() < p) { 
    			((LabelledNode) node).setAdoptedStatus(true);
    			totalAdoptersAdd1++;
    			totalPAdopters++;
    		}
    	}
		return g;
	
    }   
    
    // export a graph into graphML format for use in Visone
    private static void exportGraphML(UndirectedGraph<LabelledNode, DefaultEdge> g) throws ExportException {
    	try {
			File file = new File("iteration" +fileCount+ ".graphml");

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

