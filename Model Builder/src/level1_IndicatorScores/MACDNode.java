package level1_IndicatorScores;

public class MACDNode {

	private static double mMACD;
	private static double mSignal;
	private static double mHistogram;
	
	public MACDNode(double macd, double signal, double histogram) {
		mMACD = macd;
		mSignal = signal;
		mHistogram = histogram;
	}
	
	public double macd()      { return mMACD; }
	public double signal()    { return mSignal; }
	public double histogram() { return mHistogram; }
	
}
