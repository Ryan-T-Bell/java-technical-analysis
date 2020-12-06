package level1_IndicatorScores;

import java.util.ArrayList;

import level0_TechnicalData.TechnicalData;
import level0_TechnicalData.TechnicalNode;

/**
 * This class is used to analyze periods of stock data and provide indicators
 * that suggest to buy or sell an investment.
 * 
 * Definitions
 * Body: Difference between the open and close prices
 * 		- Body = |Close - Open|
 * 		- Bullish: Close > Open
 * 		- Bearish: Open > Close
 * Upper Shadow: Distance between high and top of candlestick body
 * Lower Shadow: Distance between low and bottom of candlestick body
 * 
 * @author Ryan Bell
 */
public class Candlestick {


	/////////////////////////////////////////////////////////////////////////////////////
	// Class Variables
	/////////////////////////////////////////////////////////////////////////////////////

	public static final int NUMBER = 19;
	
	/////////////////////////////////////////////////////////////////////////////////////
	// Wrapper Method
	/////////////////////////////////////////////////////////////////////////////////////


	/**
	 * Primary wrapper method for class (conducts candlestick analysis on entered data)  
	 * @param data Data being tested
	 * @param start Beginning index (must have cf + 2 prefix)
	 * @param end Last index to calculate candlestick pattern for
	 * @param cf Compression factor for determining period to calculate candlesticks for (5 minute P => (5xcf) minute P)
	 * @param cprw Candlestick pattern recognition weight (2 standard)
	 */
	public static ArrayList<Double[]> candlestickAnalysis(TechnicalData tdata, int start, int end, int cf, double cprw) {

		
		int index;
		ArrayList<Double[]> list = new ArrayList<Double[]>();
		TechnicalData data = TechnicalData.modifyTechnicalData(tdata, cf);


		for (int i = start; i <= end; i++) {

			if (cf <= 1) index = i;
			else index = modI(i, cf);
			
			list.add(getCandlestickScore(data, index, cprw));
		
		}


		return list;
	}


	/////////////////////////////////////////////////////////////////////////////////////
	// Primary Helper Methods
	/////////////////////////////////////////////////////////////////////////////////////


	/**
	 * Used to "stagger" indexes parameter in getCandlestickScore method.  This method is necessary to 
	 * prevent a "moving average effect".
	 * Example (cf = 3): 0, 1, 2, 3, 4, 5, 6 => 0, 0, 0, 3, 3, 3
	 * @param index Current index in technical data being added
	 * @param cf Combining factor for determining period to calculate candlesticks for (Period = 5 x cf)
	 * @return Index to calculate candlestick score for
	 */
	private static int modI(int index, int cf) {
		return index - index % cf;
	}


	/**
	 * Generate score for given index in technical data
	 * @param data Regular or modified (combined nodes) TechnicalData
	 * @param index Current index creating candlestick analysis score for
	 * @param dW Weight that alters candlestick pattern recognition (2 is standard)
	 * @return Score of candlestick values
	 */
	private static Double[] getCandlestickScore(TechnicalData data, int index, double dW) {

		// Set initial scores values (to ensure null values aren't returned
		Double[] scores = {
				0.0,0.0,0.0,0.0,0.0,
				0.0,0.0,0.0,0.0,0.0,
				0.0,0.0,0.0,0.0,0.0,
				0.0,0.0,0.0,0.0 };

		// Single candlestick patterns
		if (hammer(data, index)) 						scores[0] = 1.0;
		if (hangingMan(data, index)) 					scores[1] = 1.0;

		// Double bullish patterns
		if (bullishEngulfing(data, index, dW)) 			scores[2] = 1.0;
		if (strongBullishEngulfing(data, index, dW)) 	scores[3] = 1.0;
		if (bullishPiercing(data, index)) 				scores[4] = 1.0;
		if (bullishHarami(data, index, dW)) 			scores[5] = 1.0;
		if (bullishHaramiCross(data, index, dW)) 		scores[6] = 1.0;

		// Double bearish patterns
		if (bearishEngulfing(data, index, dW)) 			scores[7] = 1.0;
		if (strongBearishEngulfing(data, index, dW)) 	scores[8] = 1.0;
		if (darkCloud(data, index)) 					scores[9] = 1.0;
		if (bearishHarami(data, index, dW)) 			scores[10] = 1.0;
		if (bearishHaramiCross(data, index, dW)) 		scores[11] = 1.0;

		// Triple candlestick patterns
		if (morningStar(data, index)) 					scores[12] = 1.0;
		if (eveningStar(data, index)) 					scores[13] = 1.0;

		// Indecision patterns
		if (doji(data, index, dW)) 						scores[14] = indInc(scores);
		if (harami(data, index, dW)) 					scores[15] = indInc(scores);
		if (haramiCross(data, index, dW)) 				scores[16] = indInc(scores);

		// Body range width
		scores[17] = data.get(index).body();
		
		// Position between high and low
		scores[18] = (data.close(index) - data.low(index)) / 
					 (data.high(index) - data.low(index));
		
		return scores;
	}


	/**
	 * Indecisive increment (will move towards 0 based on sum of all other values)
	 * @param score Current score (to determine sign)
	 * @return Positive or negative 0.5 value 
	 */
	private static double indInc(Double[] scores) {

		double sum = 0.0;
		for (int i = 0; i < 14; i++) sum += scores[i];

		if (sum > 0) return -0.5;
		else if (sum > 0) return 0.5;
		else return 0;
	}


	/////////////////////////////////////////////////////////////////////////////////////
	// Secondary Helper Methods: Bullish indicators
	/////////////////////////////////////////////////////////////////////////////////////


	/**
	 * Sum of open and close used in candlestick pattern recognition
	 * @param n TechnicalNode to calculate open and close sum for
	 * @return Open + close price
	 */
	private static double sum(TechnicalNode n) { 
		return n.getOpen() + n.getClose();
	}


	/**
	 * Body: Bullish
	 * Upper Shadow: Small or non-existent
	 * Lower Shadow: 2xs (or more) the body
	 * @param node Technical analysis data
	 * @return If it is a hammer
	 */
	private static boolean hammer(TechnicalData data, int index) {

		TechnicalNode n = data.get(index);

		if (n.bullish() && (2*n.body()) <= n.lowShadow() && n.upShadow() < (n.lowShadow()/4) ) return true;
		else return false;

	}


	/**
	 * First candlestick is bearish and body is "engulfed" by the 
	 * bullish, second candlestick body
	 * @param list Two candlesticks
	 * * @param dW Doji weight (Usually 0.03 - 0.05)
	 * @return If pattern is bullish engulfing
	 */
	private static boolean bullishEngulfing(TechnicalData data, int index, double dW) {
		TechnicalNode n0 = data.get(index-1);
		TechnicalNode n1 = data.get(index);

		if (!doji(data, index, dW) && n0.bearish() && n1.bullish() && n0.getOpen() < n1.getClose() && n0.getClose() > n1.getOpen() &&
				!strongBullishEngulfing(data, index, dW)) return true;

		else return false;
	}


	/**
	 * First candlestick is bearish and entire stick is "engulfed" by the 
	 * bullish, second candlestick body
	 * @param list Two candlesticks
	 * @param dW Doji weight (usually 0.03 - 0.05)
	 * @return If pattern is bullish engulfing
	 */
	private static boolean strongBullishEngulfing(TechnicalData data, int index, double dW) {
		TechnicalNode n0 = data.get(index-1);
		TechnicalNode n1 = data.get(index);

		if (!doji(data, index, dW) && n0.bearish() && n1.bullish() && n0.getOpen() < n1.getClose() && n0.getClose() > n1.getOpen()
				&& n0.getHigh() < n1.getClose() && n0.getLow() > n1.getOpen()) return true;
		else return false;
	}


	/**
	 * First, bearish candlestick opens above and ends within the body of
	 * the second candlestick which is bullish
	 * @param list Two candlesticks
	 * @return If pattern is a dark cloud
	 */
	private static boolean bullishPiercing(TechnicalData data, int index) {
		TechnicalNode n0 = data.get(index-1);
		TechnicalNode n1 = data.get(index);

		if (n0.bearish() && n1.bullish() && n0.getOpen() > n1.getClose() && n0.getClose() > n1.getOpen() && 
				n0.getClose() < n1.getClose() && (n0.getOpen() + n0.getClose())/2 <= n1.getClose()) return true;

		else return false;
	}


	/**
	 * First, bearish candlestick's body engulfs second bullish candlestick's body
	 * @param list Two candlesticks
	 * @param dW Doji weight (usually 0.03 - 0.05)
	 * @return If pattern is bullish harami
	 */
	private static boolean bullishHarami(TechnicalData data, int index, double dW) {
		TechnicalNode n0 = data.get(index-1);
		TechnicalNode n1 = data.get(index);

		if (n0.bearish() && n1.bullish() && !doji(data, index, dW) && n0.getOpen() > n1.getClose() && 
				n0.getClose() < n1.getOpen() && n0.body() > n1.body()*2) return true;
		else return false;
	}


	/**
	 * First, bearish candlestick's body engulfs second doji candlestick's body
	 * @param list Two candlesticks
	 * @param dW Doji weight (usually 0.03 - 0.05)
	 * @return If pattern is bullish harami cross
	 */
	private static boolean bullishHaramiCross(TechnicalData data, int index, double dW) {
		TechnicalNode n0 = data.get(index-1);
		TechnicalNode n1 = data.get(index);

		if (n0.bearish() && n1.bullish() && doji(data, index, dW) && n0.getOpen() > n1.getClose() && 
				n0.getClose() < n1.getOpen() && n0.body() > n1.body()*2) return true;
		else return false;
	}


	/**
	 * First: Bearish
	 * Second: Body is about below surrounding candlestick's bodies and body is 1/2 (or less) the size of surrounding
	 * Third: Bullish and penetrates around 1/2 into first candlestick's body
	 * @param list Three candlesticks
	 * @param dW Doji weight (usually 0.03 - 0.05)
	 * @param d Divides open+close sum in order to determine fraction of 
	 * allowed penetration by adjacent candlesticks
	 * @return If pattern is morning star
	 */
	private static boolean morningStar(TechnicalData data, int index) {
		TechnicalNode n0 = data.get(index-2);
		TechnicalNode n1 = data.get(index-1);
		TechnicalNode n2 = data.get(index);

		if (n0.bearish() && n2.bullish() && 															// Ensure correct bear/bull candlesticks
				sum(n0)/2 >= n1.getOpen() && sum(n0)/2 >= n1.getClose()	&& n0.body() >= n1.body()*2 &&	// Check n0->n1
				sum(n2)/2 >= n1.getOpen() && sum(n2)/2 >= n1.getClose() && n2.body() >= n1.body()*2 && 	// Check n2->n1
				sum(n0)*2/3 >= n2.getOpen() 															// Check n0->n2
				) return true;
		else return false;
	}


	/////////////////////////////////////////////////////////////////////////////////////
	// Secondary Helper Methods: Bearish indicators
	/////////////////////////////////////////////////////////////////////////////////////


	/**
	 * Body: Bearish
	 * Upper Shadow: Small or non-existent
	 * Lower Shadow: 2xs (or more) the body
	 * @param node Technical analysis data
	 * @return If it is a hanging man
	 */
	private static boolean hangingMan(TechnicalData data, int index) {
		TechnicalNode n = data.get(index);

		if (n.bearish() && (2*n.body()) <= n.lowShadow() && n.upShadow() < (n.lowShadow()/4) ) return true;
		else return false;
	}


	/**
	 * First candlestick is bullish and body is "engulfed" by the 
	 * bearish, second candlestick body
	 * @param list Two candlesticks
	 * * @param dW Doji weight (Usually 0.03 - 0.05)
	 * @return If pattern is bearish engulfing
	 */
	private static boolean bearishEngulfing(TechnicalData data, int index, double dW) {
		TechnicalNode n0 = data.get(index-1);
		TechnicalNode n1 = data.get(index);

		if (!doji(data, index, dW) && n0.bullish() && n1.bearish() && n0.getClose() < n1.getOpen() && n0.getOpen() > n1.getClose() &&
				!strongBearishEngulfing(data, index, dW)) {
			return true;	
		}


		else return false;
	}


	/**
	 * First candlestick is bullish and entire stick is "engulfed" by the 
	 * bearish, second candlestick body
	 * @param list Two candlesticks
	 * @param dW Doji weight (Usually 0.03 - 0.05)
	 * @return If pattern is strongly bearish engulfing
	 */
	private static boolean strongBearishEngulfing(TechnicalData data, int index, double dW) {
		TechnicalNode n0 = data.get(index-1);
		TechnicalNode n1 = data.get(index);

		if (!doji(data, index, dW) && n0.bullish() && n1.bearish() && n0.getClose() < n1.getOpen() && n0.getOpen() > n1.getClose()
				&& n0.getHigh() < n1.getOpen() && n0.getLow() > n1.getClose()) return true;
		else return false;
	}


	/**
	 * First, bullish candlestick opens below and ends within the body of
	 * the second candlestick which is bearish
	 * @param list Two candlesticks
	 * @return If pattern is a dark cloud
	 */
	private static boolean darkCloud(TechnicalData data, int index) {
		TechnicalNode n0 = data.get(index-1);
		TechnicalNode n1 = data.get(index);

		if (n0.bullish() && n1.bearish() && n0.getClose() > n1.getClose() && n0.getClose() < n1.getOpen() && 
				n0.getOpen() < n1.getClose() && (n0.getOpen() + n0.getClose())/2 >= n1.getClose()) return true;

		else return false;
	}


	/**
	 * First, bullish candlestick's body engulfs second bearish candlestick's body
	 * @param list Two candlesticks
	 * @param dW Doji weight (usually 0.03 - 0.05)
	 * @return If pattern is bearish harami
	 */
	private static boolean bearishHarami(TechnicalData data, int index, double dW) {
		TechnicalNode n0 = data.get(index-1);
		TechnicalNode n1 = data.get(index);

		if (n0.bullish() && n1.bearish() && !doji(data, index, dW) && n0.getOpen() > n1.getClose() && 
				n0.getClose() < n1.getOpen() && n0.body() > n1.body()*2) return true;
		else return false;
	}


	/**
	 * First, bearish candlestick's body engulfs second doji candlestick's body
	 * @param list Two candlesticks
	 * @param dW Doji weight (usually 0.03 - 0.05)
	 * @return If pattern is bearish harami cross
	 */
	private static boolean bearishHaramiCross(TechnicalData data, int index, double dW) {
		TechnicalNode n0 = data.get(index-1);
		TechnicalNode n1 = data.get(index);

		if (n0.bullish() && n1.bearish() && doji(data, index, dW) && n0.getOpen() > n1.getClose() && 
				n0.getClose() < n1.getOpen() && n0.body() > n1.body()*2) return true;
		else return false;
	}


	/**
	 * First: Bullish
	 * Second: Body is about above surrounding candlestick's bodies and body is 1/2 (or less) the size of surrounding
	 * Third: Bearish and penetrates around 1/2 into first candlestick's body
	 * @param list Three candlesticks
	 * @param dW Doji weight (usually 0.03 - 0.05)
	 * @param d Divides open+close sum in order to determine fraction of 
	 * allowed penetration by adjacent candlesticks
	 * @return If pattern is morning star
	 */
	private static boolean eveningStar(TechnicalData data, int index) {
		TechnicalNode n0 = data.get(index-2);
		TechnicalNode n1 = data.get(index-1);
		TechnicalNode n2 = data.get(index);

		if (n0.bullish() && n2.bearish() && 														// Ensure correct bear/bull candlesticks
				sum(n0)/2 <= n1.getOpen() && sum(n0)/2 <= n1.getClose()	&& n0.body() >= n1.body()*2 &&	// Check n0->n1
				sum(n2)/2 <= n1.getOpen() && sum(n2)/2 <= n1.getClose() && n2.body() >= n1.body()*2 && 	// Check n2->n1
				sum(n0)*2/3 >= n2.getOpen() 															// Check n0->n2
				) return true;
		else return false;
	}


	/////////////////////////////////////////////////////////////////////////////////////
	// Helper Methods: Indecisive or trend change indicators
	/////////////////////////////////////////////////////////////////////////////////////


	/**
	 * Candlestick with small body (usually 3 to 5% body/height)
	 * @param n Technical analysis data
	 * @param weight body/height percentage to be a doji
	 * @return If candlestick is a doji
	 */
	private static boolean doji(TechnicalData data, int index, double weight) {
		TechnicalNode n = data.get(index);
		return n.body() <= ((n.getHigh() - n.getLow()) * weight) || n.body() <= n.getOpen() * 0.0003;
	}


	/**
	 * First, bearish candlestick's body engulfs second bullish candlestick's body
	 * @param list Two candlesticks
	 * @param dW Doji weight (usually 0.03 - 0.05)
	 * @return If pattern is bullish harami
	 */
	private static boolean harami(TechnicalData data, int index, double dW) {
		TechnicalNode n0 = data.get(index-1);
		TechnicalNode n1 = data.get(index);

		if (!doji(data, index, dW) && n0.getOpen() > n1.getClose() && 
				n0.getClose() < n1.getOpen() && n0.body() > n1.body()*2) return true;
		else return false;
	}


	/**
	 * First, bearish candlestick's body engulfs second doji candlestick's body
	 * @param list Two candlesticks
	 * @param dW Doji weight (usually 0.03 - 0.05)
	 * @return If pattern is bullish harami cross
	 */
	private static boolean haramiCross(TechnicalData data, int index, double dW) {
		TechnicalNode n0 = data.get(index-1);
		TechnicalNode n1 = data.get(index);

		if (doji(data, index, dW) && n0.getOpen() > n1.getClose() && 
				n0.getClose() < n1.getOpen() && n0.body() > n1.body()*2) return true;
		else return false;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Testing
	////////////////////////////////////////////////////////////////////////////////////////////////


	public static void main (String[] args) {

		
	}
}