package level1_IndicatorScores;

import java.util.ArrayList;

import level0_TechnicalData.TechnicalData;
import model.Model;
import model.Variables;

/**
 * Class used to predict how changes in volume and price action affect future prices
 * Compare OBV with MAs
 * 	1) Down-trend of OBV in up price action (Sell)
 * 	2) Up-trend of OBV in down price action (Buy)
 * 	
 * 
 * 	In up swing:
 * 		Volume should be heavier as the price moves higher
 * 		Should decrease as price drops
 * 
 * Confirm double and triple tops with lighter volume at each top
 * 
 * @author Ryan Bell
 */
public class OBV {

	
	////////////////////////////////////////////////////////////////////////////////////////////////
	// Primary Wrapper Method for Class
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * Calculate and re-scale 
	 * @param data Technical data set to create analysis for
	 * @param start First index to create obvMA score for
	 * @param end Last index to create obv score for
	 * @param period Period to calculate on balance volume moving average over
	 * @param perMult Period multiplier 
	 * @return List of volume moving average scores (volume * delta - volMA * delta)
	 */
	public static ArrayList<Double> obvScores(TechnicalData data, int start, int end, int period, int perMult) {

		ArrayList<Double> obv = obv(data, start-(period*perMult), end);
		return normalize(obv, start, period*perMult);
		
	}
	
	
	/**
	 * Calculate and re-scale 
	 * @param data Technical data set to create analysis for
	 * @param start First index to create obvMA score for
	 * @param end Last index to create obvMA score for
	 * @param period Period to calculate on balance volume moving average over
	 * @param perMult Period multiplier 
	 * @return List of volume moving average scores (volume * delta - volMA * delta)
	 */
	public static ArrayList<Double> obvMAScores(TechnicalData data, int start, int end, int period, int perMult) {

		ArrayList<Double> obvMA = obvMA(data, start-(period*perMult), end, period);
		return normalize(obvMA, start, period*perMult);
		
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	// Primary Indicator Calculation Methods
	////////////////////////////////////////////////////////////////////////////////////////////////
	

	/**
	 * Alternative to standard on balance volume calculation.  Method keeps a running sum of 
	 * (volume x price change).  OBV will go up on periods that close higher than the previous,
	 * and it will go down on prices that close lower.  Multiplying by price delta 
	 *
	 * @param data Technical data that is being analyzed
	 * @param start Index in TechnicalData to begin calculating OBV for
	 * @param end Last index in TechnicalData to calculate OBV for
	 * @return ArrayList of on balance volume values
	 */
	public static ArrayList<Double> obv(TechnicalData data, int start, int end) {

		double delta = 0.0;								// Price change (today's close - yesterday's)
		double obv = 0.0;								// Store current on balance volume value
		ArrayList<Double> list = new ArrayList<Double>();	

		for (int i = start; i <= end; i++) {

			// Calculate values
			delta = data.close(i) - data.close(i-1);
			obv = (data.volume(i) * delta);

			// Add to list
			list.add(obv);					

		}

		return list;
	}

	
	/**
	 * Calculate a moving average for on balance volume
	 * 
	 * @param data TechnicalData for stock.  List must be period + 1 beyond required length
	 * @param start Index in TechnicalData to begin calculating OBV for
	 * @param end Last index in TechnicalData to calculate OBV for
	 * @param period Period to take OBV MA for
	 * @param d Determine if modify OBV by price change for weighting (T = Yes, F = No)
	 * @return List of OBV values in order latest -> newest
	 */
	public static ArrayList<Double> obvMA(TechnicalData data, int start, int end, int period) {

		double delta = 1.0; 	// Differences in close prices
		double obvMA = 0.0;		// Running sum of closes for moving average

		ArrayList<Double> list = new ArrayList<Double>();

		for (int i = start; i <= end; i++) {

			obvMA = 0.0;	// Reset variable

			for (int j = i - period + 1; j <= i; j++) {

				// If statement added to make delta = 1 for first value in data set
				if (j != 0) delta = data.close(j) - data.close(j-1);
				else delta = 1;

				obvMA += data.volume(j) * delta;
			}
			list.add(obvMA/period);
		}
		return list;
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	// Normalization Wrapper Methods and Helper Methods
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * @param obv On balance volume or on balance volume moving average list
	 * @param start Index in TechnicalData data set to start analysis (used in range offset)
	 * @param rangePeriod Index offset to calculate 
	 * @return List of obv values that were converted to [0,1] range
	 */
	public static ArrayList<Double> normalize(ArrayList<Double> obv, int start, int rangePeriod) {
		
		ArrayList<Double> list = new ArrayList<Double>();
		ArrayList<Double[]> ranges = getRanges(obv, start, rangePeriod);
		
		for (int i = 0; i < ranges.size(); i++) {
			list.add(
					Model.round(
							rescale(obv.get(i+rangePeriod), ranges.get(i))
					,6)
					);
		}
		
		return list;
	}
	
	
	/**
	 * Convert close and moving average values to [0, 1] range
	 * @param close Close value at index
	 * @param movAvg Moving Average value at index (mean)
	 * @param range Highest and lowest normalized values previously calculated
	 * @return Converted value to [0,1] range
	 */
	public static double rescale(double x, Double[] range) {

		return (-1) * (x - range[1]) / (range[0] - range[1]) + 1;

	}
	
	
	/**
	 * Helper method that creates a list of max and min ranges for the obv score inputs
	 * @param obv On balance volume 
	 * @param start
	 * @param rangePeriod
	 * @return
	 */
	public static ArrayList<Double[]> getRanges(ArrayList<Double> obv, int start, int rangePeriod) {
		
		double max, min;
		ArrayList<Double[]> ranges = new ArrayList<Double[]>();
		
		for (int i = rangePeriod; i < obv.size(); i++) {
			
			// Reset variables
			max = obv.get(i);
			min = obv.get(i);
			
			for (int j = i - rangePeriod + 1; j <= i; j++) {
				if (obv.get(j) > max) max = obv.get(j);
				if (obv.get(j) < min) min = obv.get(j);
			}
			
			Double[] range = {max, min};
			ranges.add(range);
		}
		return ranges;
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	// Testing
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	public static void main(String[] args) {

		TechnicalData data = new TechnicalData("IVV");
		int start = data.getIndexByTimeStamp("2014");
		int end = data.size() - 1;
		int period = Variables.PIM*21;
		
		
		//ArrayList<Double> obv = 
		obvScores(data, start, end, period, 10);
		//for (double val : obv) System.out.println(val);
	}
}
