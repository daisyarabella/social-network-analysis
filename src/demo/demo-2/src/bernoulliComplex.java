// Creates a random graph (Bernoulli) and undergo adoption process in Complex way. Writes all graph data to single files for line plot (timestepData) 
// and calculating p and q in python file (linearEqs)

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
import graphState.graphState;

public class bernoulliComplex
{   
    public static void main(String [] args) throws ExportException, IOException
    {
        
        File timestepData = new File("../../../data/timestepDataBernoulliComplex.csv"); // file to record timestep data for plotting simple line graph
    	File linearEqs = new File("../../../data/linearEqsBernoulliComplex.csv"); // file to be read by python to calculate p and q. Includes S(t+1) data, and coefficients of a, b, c
    	FileWriter timestepfw = new FileWriter(timestepData.getAbsoluteFile());
    	FileWriter linearfw = new FileWriter(linearEqs.getAbsoluteFile());
    	timestepfw.write("GraphNo, t,Y(t),External Adopters,Internal Adopters\n");
    	linearfw.write("GraphNo,Stadd1,aCo,bCo,cCo\n");
    	
    	Scanner scanner = new Scanner(System.in);
        System.out.println("How many nodes?");
        int noNodes = scanner.nextInt();
        System.out.println("Insert edge probability:");
        double edgeProb = scanner.nextDouble();
        System.out.println("Set coefficient of innovation (p):");
        double p = scanner.nextDouble();
        
        for (int graphNumber=1; graphNumber<=10; graphNumber++) {		
        	createAndAdopt(timestepfw, linearfw, noNodes, edgeProb, p, graphNumber);
        }    
     	timestepfw.close();
     	linearfw.close();
     	System.out.println("Finished");
    }


	private static void createAndAdopt(FileWriter timestepfw,
			FileWriter linearfw, int noNodes, double edgeProb, double p,int graphNumber)
			throws IOException {
		graphState state = new graphState();
		//create a graph
        UndirectedGraph<LabelledNode, DefaultEdge> graph = initRandomGraph(noNodes, edgeProb);
        NeighborIndex<LabelledNode, DefaultEdge> ni = new NeighborIndex(graph);
        
		// Complex adoption
     	do {
     		state.setIntAdoptionHappen(false);
     		if (state.getYt() == 0) {
     			graph = initAdoption(graph,p,state);
     		}
         	/*try {
    			exportGraphML(graph,state); 
    		} catch (ExportException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}*/ //-- commented out whilst running tests to spot trends
     		graph = internalAdoption(graph,ni,p,state);
     		if (state.getYt() == state.getYtadd1()) { 
     			state.incrementLoopNumber(); 
     		}
         	//exportCSV files
         	timestepfw.write(graphNumber + "," + state.gett() + "," + state.getYt() + "," + state.getExtAdoptionCount() + "," + state.getIntAdoptionCount() + "\n");
         	linearfw.write(graphNumber + "," + (state.getYtadd1()-state.getYt()) + "," + 1 + "," + state.getYt() + "," + state.getYt()*state.getYt() + "\n");
         	System.out.println("t: " +state.gett()+ "\t Y(t+1): " +state.getYtadd1()+ "\t Y(t): " +state.getYt());
     		state.incrementT();
 			System.out.println("Loop" + state.getLoopNumber()); 
        } while (state.getLoopNumber() < 1000); // while adoption can still happen??
     	//System.out.println("External adoptions: " + state.getExtAdoptionCount() + ", Internal adoptions: " +state.getIntAdoptionCount());
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
    
    // method for initial adoption
    private static UndirectedGraph<LabelledNode, DefaultEdge> initAdoption(UndirectedGraph<LabelledNode, DefaultEdge> g, double p, graphState state) {
    	Object[] nodes = g.vertexSet().toArray(); 
    	for (Object node: nodes) {
    		if (Math.random() < p) {
    	    	((LabelledNode) node).setAdoptedStatus(true);
    			state.incrementYt();
    			state.incrementYtadd1();
    			state.incrementExtAdoptionCount();
    		}
    	}
    	return g;
    }
    
    // method to make all neighbors of adopted nodes adopted
    private static UndirectedGraph<LabelledNode, DefaultEdge> internalAdoption(UndirectedGraph<LabelledNode, DefaultEdge> g, NeighborIndex<LabelledNode, DefaultEdge> ni, 
    		double p, graphState state) {
    	state.setYtAsYtadd1();
    	Object[] nodes = g.vertexSet().toArray(); 
    	for (Object node : nodes) {
    		// if node has adopted, getNeighbours of node
    		if (((LabelledNode) node).getAdoptedStatus()) {
    			Set<LabelledNode> neighbors = ni.neighborsOf((LabelledNode) node);
    			// assign all neighbours with adopted attribute
    			for (Object neighbor : neighbors) {
    				if (Math.random() < p && !((LabelledNode) neighbor).getAdoptedStatus()) { // this condition means complex adoption occurs
    					((LabelledNode) neighbor).setAdoptedStatus(true); 
    					state.incrementYtadd1();
    					state.incrementIntAdoptionCount();
    					state.setIntAdoptionHappen(true);
    				}
    			} 
    		}
    	}
		return g;
    }
    
    /*private static UndirectedGraph<LabelledNode, DefaultEdge> externalAdoption(UndirectedGraph<LabelledNode, DefaultEdge> g, NeighborIndex<LabelledNode, 
    		DefaultEdge> ni, double p, graphState state) {
    	state.setYtAsYtadd1();
    	Object[] nodes = g.vertexSet().toArray(); 
    	for (Object node : nodes) {
    		if (!((LabelledNode) node).getAdoptedStatus()) {
    				if (Math.random() < p) {
    					((LabelledNode) node).setAdoptedStatus(true);
    					state.incrementYtadd1();
    					state.incrementExtAdoptionCount();
    			}
    		}
    	}
		return g;
    }*/ // don't need for complex adoption   
    
    // export a graph into graphML format for use in Visone
    private static void exportGraphML(UndirectedGraph<LabelledNode, DefaultEdge> g, graphState state) throws ExportException {
    	try {
			File file = new File("../../../graph-iterations/iteration" +state.getFileCount()+ ".graphml");

			// if file doesn't exist, create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			NameProvider np = new NameProvider();
			GraphMLExporter<LabelledNode, DefaultEdge> exporter = new GraphMLExporter<LabelledNode, DefaultEdge>();
	        exporter.setVertexLabelProvider(np);
	        exporter.exportGraph(g, fw);
	        state.incrementFileCount();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
