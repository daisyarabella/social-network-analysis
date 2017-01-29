package graphState;

public class graphState extends Object {
	
 	private int Yt;
	private int Ytadd1;
	private int intAdoptionCount;
	private int extAdoptionCount;
	private int t;
	private int fileCount;
	private int loopNumber;
	private boolean internalAdoptionHappen;

	public graphState() {
 		this.Yt = 0; // Y(t)
 	 	this.Ytadd1 = 0; // Y(t+1)
 	 	this.intAdoptionCount = 0;
 	 	this.extAdoptionCount = 0;
 	 	this.t = 0; 
 	 	this.fileCount = 1; // to create iteration graphs
 	 	this.internalAdoptionHappen = false;
 	 	this.loopNumber = 0;
	}
 	
 	public int getYt() {
 		return Yt;
 	}
	
	public void incrementYt() {
		this.Yt++;
	}
	
	public void setYtAsYtadd1() {
		this.Yt = this.Ytadd1;
	}
	
	public int getYtadd1() {
 		return Ytadd1;
 	}
 	
 	public void incrementYtadd1() {
		this.Ytadd1++;
	}
 	
 	public void incrementIntAdoptionCount() {
		this.intAdoptionCount++;
	}
 	
 	public int getIntAdoptionCount() {
 		return intAdoptionCount;
 	}
 	
 	public void incrementExtAdoptionCount() {
		this.extAdoptionCount++;
	}
 	
	public int getExtAdoptionCount() {
 		return extAdoptionCount;
 	}
 	
 	public void incrementT() {
		this.t++;
	}
 	
 	public int gett() {
 		return t;
 	} 	
 	
 	public void incrementFileCount() {
		this.fileCount++;
	}
 	
 	public int getFileCount() {
 		return fileCount;
 	} 
 
 	public int getLoopNumber() {
 		return this.loopNumber;
 	}
	
	public void incrementLoopNumber() {
		this.loopNumber++;
	}
 	
 	public void setIntAdoptionHappen(boolean bool){
		this.internalAdoptionHappen = bool;
	}
 	
 	public boolean getIntAdoptionHappen() {
 		return internalAdoptionHappen;
 	}
} 	