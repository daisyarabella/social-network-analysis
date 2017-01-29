package demo;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.gephi.project.api.ProjectController;
import org.openide.util.Lookup;
import org.gephi.project.api.Workspace;
import org.gephi.datalab.api.AttributeColumnsController;
import org.gephi.graph.api.Column;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.NodeIterable;
import org.gephi.graph.api.Table;
import org.gephi.graph.api.UndirectedGraph;
import org.gephi.graph.api.types.IntervalStringMap;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.Container.Factory;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.exporter.preview.PDFExporter;
import org.gephi.io.generator.plugin.DynamicGraph;
import org.gephi.io.generator.plugin.RandomGraph;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewModel;
import org.gephi.preview.api.PreviewProperties;
import org.gephi.preview.api.PreviewProperty;
import org.gephi.preview.types.DependantColor;

import com.itextpdf.text.PageSize;


public class demo {
	public static void main(String[] args) {
		//Init a project - and therefore a workspace
		ProjectController pc = (ProjectController) Lookup.getDefault().lookup(ProjectController.class);
		pc.newProject();
		Workspace workspace = pc.getCurrentWorkspace();
		
		//Generate a new random graph into a container
		Container container = ((Factory) Lookup.getDefault().lookup(Container.Factory.class)).newContainer();
		RandomGraph randomGraph = new RandomGraph();
		randomGraph.setNumberOfNodes(100);
		randomGraph.setWiringProbability(0.01);
		randomGraph.generate(container.getLoader());

		//Append container to graph structure
		ImportController importController = (ImportController) Lookup.getDefault().lookup(ImportController.class);
		importController.process(container, new DefaultProcessor(), workspace);
		
		//Create graph model
		GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel();
				
		// Create a table so can record which nodes have adopted
        Table nodeTable = graphModel.getNodeTable();
        AttributeColumnsController acc = Lookup.getDefault().lookup(AttributeColumnsController.class);
        Column adoptedColumn = acc.addAttributeColumn(nodeTable, "adopted", Boolean.class);
        Column adoptedRoundColumn = acc.addAttributeColumn(nodeTable, "adopted_round", String.class);
        Column timeColumn = acc.addAttributeColumn(nodeTable, "time", IntervalStringMap.class);
        //nodeTable.addColumn("time", IntervalStringMap.class);
        
        //Get a UndirectedGraph now 
		UndirectedGraph undirectedGraph = graphModel.getUndirectedGraph();
		
		// Choose values for p and q based on study by Sultan, Farley, and Lehmann in 1990
		double p = 0.03; // Fixed coefficient of innovation
		double q = 0.38; // Fixed coefficient of imitation
		
		int p1 = 0;
		int p2 = 0;
		int q1 = 0;
		
		//double[] keys = {0.0, Double.POSITIVE_INFINITY};
		//String[] status = {"adopted by p"};
		//IntervalStringMap map = new IntervalStringMap(keys, status);
		//n.setAttribute("time", new IntervalStringMap(keys,status));
		
		// Randomly choose proportion of nodes to adopt based on p
		for (Node n : undirectedGraph.getNodes()) {
			if (Math.random() < p) { 
				n.setAttribute("adopted", true);
				n.setAttribute("adopted_round", "1: p");
				p1++;
				//n.setAttribute("time", new IntervalStringMap(keys,status));
			} else {
				n.setAttribute("adopted", false);
			}
		}
		
		//Column convertAttributeColumnToNewDynamicColumn(nodeTable, "adopted", 0.0, Double.POSITIVE_INFINITY, "adopted_dynamic");
		
		// ************at each time interval*************
		for (Node n : undirectedGraph.getNodes()) {
			// if node has adopted, getNeighbours of node
			if ((boolean) n.getAttribute("adopted")) {
				NodeIterable nodeIt = undirectedGraph.getNeighbors(n);
					// assign q% neighbours with adopted attribute
					for (Node m : nodeIt.toArray()) {
						if (Math.random() < q) {
							m.setAttribute("adopted", true); 
							m.setAttribute("adopted_round", "1: q");
							q1++;
						}
					}
				}
			// make a further p of the non-adopted nodes adopted
			else if (Math.random() < p) { 
				n.setAttribute("adopted", true); 
				n.setAttribute("adopted_round", "2: p");
				p2++;
			}
		}
		
		System.out.println("p1: "+p1);
		System.out.println("p2: "+p2);
		System.out.println("q1: "+q1);
		
		//Export full graph to Gephi format
        ExportController ec = Lookup.getDefault().lookup(ExportController.class);
        try {
            ec.exportFile(new File("demo.gexf"));
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
	}
}