package LabelledNode;

import org.jgrapht.ext.EdgeNameProvider;
import org.jgrapht.ext.VertexNameProvider;

public class LabelledNode extends Object {
	private String label;
	private boolean adopted;
	
	public LabelledNode(String label, boolean adopted) {
		this.label = label;
		this.adopted = adopted;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setAdoptedStatus(boolean adopted) {
		this.adopted = adopted;
	}
	
	public boolean getAdoptedStatus() {
		return adopted;
	}
	
	public String getVertexName() {
		return label + "adopted";
	}
}

public class NameProvider extends Object implements VertexNameProvider {
	public String getVertexName(Object node) {
		return ((LabelledNode) node).getLabel() + " Adopted: "+ ((LabelledNode) node).getAdoptedStatus();
	}
}