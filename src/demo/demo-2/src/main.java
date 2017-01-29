import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.NeighborIndex;
import org.jgrapht.ext.ExportException;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import LabelledNode.LabelledNode;
//import adoptionMethods.complexAdoption;

public class main
{
 	static int currentGraphSize = 0;
 	static int totalNoEdges = 0;
    
    public static void main(String [] args) throws ExportException, IOException
    {
    	File timestepData = new File("../../../data/timestepData.csv"); // file to record timestep data for plotting simple line graph
    	File linearEqs = new File("../../../data/linearEqs.csv"); // file to be read by python to calculate p and q. Includes S(t+1) data, and coefficients of a, b, c
    	FileWriter timestepfw = new FileWriter(timestepData.getAbsoluteFile());
    	FileWriter linearfw = new FileWriter(linearEqs.getAbsoluteFile());
    	timestepfw.write("t,Y(t),External Adopters,Internal Adopters\n");
    	linearfw.write("S(t+1),aCo,bCo,cCo\n");
    	
    	Scanner scanner = new Scanner(System.in);
        System.out.println("How many nodes?");
        int graphSize = scanner.nextInt();
        System.out.println("Set coefficient of innovation (p):");
        double p = scanner.nextDouble();
        
        int generatorType;
        double edgeProb = 0;
        System.out.println("Select graph generator type - Bernoulli (1) or Preferential Attachment (2):");
        generatorType = scanner.nextInt();
        if (generatorType == 1) {
            System.out.println("Enter edge probability:");
            edgeProb = scanner.nextDouble();
        }
         
    	//create a graph
        UndirectedGraph<LabelledNode, DefaultEdge> graph = generateGraph(graphSize, generatorType, edgeProb);
        
        int adoptionMethod;
        System.out.println("Select an adoption method - Simple (1) or Complex (2):");
        adoptionMethod = scanner.nextInt();
        adoptGraph(graph, adoptionMethod, p, graphSize, timestepfw, linearfw);
    }
        
        // method to create adopt graphs
        private static UndirectedGraph<LabelledNode, DefaultEdge> adoptGraph(UndirectedGraph<LabelledNode, DefaultEdge> g, int adoptionMethod, 
        		double p, int graphSize, FileWriter timestepfw, FileWriter linearfw) throws IOException {
        	NeighborIndex<LabelledNode, DefaultEdge> ni = new NeighborIndex(g);  
            
        	switch (adoptionMethod) {
     		case 1: g = adoptionMethods.simpleAdoption.adopt(g, p, ni, graphSize, timestepfw, linearfw);
     		//case 2: g = adoptionMethods.complexAdoption.initAdoption(graph, p);
        	}
            return g;
        }      	
    
    // method to create initial random graphs 
    private static UndirectedGraph<LabelledNode, DefaultEdge> generateGraph(int graphSize, int generatorType, double edgeProb) {
        UndirectedGraph<LabelledNode, DefaultEdge> g = new SimpleGraph<LabelledNode, DefaultEdge>(DefaultEdge.class);
        
        switch (generatorType) {
        	case 1: g = graphGenerators.bernoulli.createGraph(g, graphSize, edgeProb);
        	break;
        	case 2: g = graphGenerators.preferentialAttachment.createGraph(g, graphSize);
        	break;
        }
        return g;
    }  
}