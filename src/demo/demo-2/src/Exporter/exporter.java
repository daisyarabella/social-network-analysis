package Exporter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.ext.ExportException;
import org.jgrapht.ext.GraphMLExporter;
import org.jgrapht.graph.DefaultEdge;

import LabelledNode.LabelledNode;
import NameProvider.NameProvider;

public class exporter {
 	static int fileCount = 1; // to create iteration graphs
	
// export a graph into graphML format for use in Visone
    public static void exportGraphML(UndirectedGraph<LabelledNode, DefaultEdge> g) throws ExportException {
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