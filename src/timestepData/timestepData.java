package timestepData;

public class timestepData {
	private int t;
	private int Yt;
	private int Yt1;
	private int totalPAdopters;
	private int totalQAdopters;	

	public timestepData(int t, int Yt, int Yt1, int totalPAdopters, int totalQAdopters) {
		this.t = t;
		this.Yt = Yt;
		this.Yt1 = Yt1;
		this.totalPAdopters = totalPAdopters;
		this.totalQAdopters = totalQAdopters;
	}
}