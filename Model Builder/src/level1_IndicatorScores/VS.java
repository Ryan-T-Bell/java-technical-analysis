package level1_IndicatorScores;

import java.util.ArrayList;

import level0_TechnicalData.TechnicalData;
import model.Model;

public class VS {

	/**
	 * Calculate volume strength in relation to a moving average.  This is used to
	 * determine if a recorded volume is "heavy", average, or "light"
	 * 
	 * volStrength = volume / volumeMA
	 * 
	 * @param data Data set to calculate volume strength list for
	 * @param period Period to look back for moving average comparison
	 * @param start Start index to begin calculating volume strength for
	 * @param end Last index to begin calculating volume strength for
	 * @param period Number of 5 minute periods to calculate moving average for
	 * @param periodMult Multiplication factor that determines how many periods max & min is calculated for
	 * @return List of volume to volume moving average values
	 */
	public static ArrayList<Double> volStrength(TechnicalData data, int start, int end, int period, int periodMult) {
		
		
		// 1. Create variables /////////////////////////////////////////////////////////////////////
		
		
		double volume = 0.0;		// Daily volume
		double volMA  = 0.0;		// Volume moving average 
		double nVal   = 0.0; 		// Normalization between volume and volume moving average
		double max	  = 0.0;		// Maximum value in data set for normalization
		double min    = 0.0;		// Minimum value in data set for normalization
		ArrayList<Double> list = new ArrayList<Double>();
		
		
		// 2. Calculate volume strength by comparing it to a moving average of the volume ////////// 
		
		
		for (int i = start; i <= end; i++) {
			
			// a. Reset variables
			volMA = 0.0;
			volume = data.volume(i);
			max = volume;
			min = volume;
			
			// b. Calculate volume moving average
			volMA = volumeMA(data, i, period);
			
			// c. Calculate max and min volume values over MA period x 
			for (int j = i - (period * periodMult) + 1; j <= i; j++) {
				if (data.volume(j) > max) max = data.volume(j);
				if (data.volume(j) < min) min = data.volume(j);
			}
			
			// d. Compare volume to volume moving average
			nVal = volume/volMA;
			
			// e. Add comparison to list
			list.add(Model.round(nVal,6));
			
		}
		
		return list;
	}
	
	
	/**
	 * @param data Data set to calculate volume strength list for
	 * @param index Current index top calculate volume moving average for
	 * @param period How many nodes to calculate moving average over
	 * @return Moving average of volume at given index
	 */
	public static double volumeMA(TechnicalData data, int index, int period) {
		
		double volMA = 0.0;
		
		for (int j = index - period + 1; j <= index; j++) 
			volMA += data.volume(j);
		
		return volMA / period;
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	// Testing
	////////////////////////////////////////////////////////////////////////////////////////////////


	
	public static void main(String[] args) {
		
		TechnicalData data = new TechnicalData("IVV");
		int start = data.getIndexByTimeStamp("2014");
		int end = data.size() - 1;
		int period = 21 * 84;
		
		ArrayList<Double> vs = 
		volStrength(data, start, end, period, 5);
		for (double v : vs) System.out.println(v);
	}
}
