package level1_IndicatorScores;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import level0_TechnicalData.TechnicalData;
import model.Model;

/**
 * Use a moving average (usually 21 periods) and an upper and lower
 * band to determine a range as to where a stock should be trading
 * 
 * BOLLINGER BAND SIGNALS
 * https://www.youtube.com/watch?v=PONc9GkzFrA
 * 
 * 1) Bullish/Bearish Expansion
 * 2) Bullish/Bearish Move Up
 * 3) Typical Variance
 * 4) Expansion Variance
 * 
 * @author Ryan Bell
 */
public class BollingerBands {


	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Wrapper Methods to Calculate Multiple Bollinger Band Values Over Entire Data Set
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////


	/**
	 * @param data Data to calculate values for
	 * @param startDate Beginning date in TechnicalData list
	 * @param endDate End date in TechnicalData list
	 * @param period Number of days to calculate bollinger bands for (Starts doing calculations at this index as well)
	 * @param weight Standard deviation weight factor (raises & lowers bands)  [usual set to 2]
	 * @return List of Bollinger Band position of close between band values
	 */
	public static ArrayList<Double> bollingerBands(TechnicalData data, int period, double weight) {

		ArrayList<Double> list = new ArrayList<Double>();

		for (int i = 0; i <= data.size(); i++) {
			list.add(
					Model.round(bollingerBand(data, i, period, weight), 6)
					);
		}

		return list;
	}

	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Helper Methods
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * Calculate standard deviation position score for close value at
	 * @param data Data set to calculate bollinger band for
	 * @param index Index in data set to calculate bollinger band for
	 * @param period Number of days to calculate bollinger bands for (Starts doing calculations at this index as well)
	 * @param weight Standard deviation weight factor (raises & lowers bands)  [usually set to 2]
	 * @return Position between upper and lower bands set by moving average
	 */
	public static double bollingerBand(TechnicalData data, int index, int period, double weight) {
		
		// Get simple moving average and standard deviation values
		double sma = MA.movingAverage(data, index, period);
		double stdDev = standardDeviation(data, index, period, sma);

		// Set close and upper band values
		double close = data.close(index);
		double upperBand = sma + (weight * stdDev);
		double lowerBand = sma - (weight * stdDev);
		
		System.out.println(data.timeStamp(index) + ": \n" + upperBand + "\n" + close + "/" + sma + "\n" + lowerBand);
		
		// Calculate position close value is in within band range
		return (close - sma)/(upperBand - sma);
	}
	
	
	/**
	 * Standard Deviation = Sum((x - m)^2)/p
	 * 			x = close values
	 * 			m = mean of close values
	 *			p = period
	 * @param data Data set to calculate value for
	 * @param mean Average of close values at index parameter of data over period
	 * @return Standard deviation of close values
	 */
	public static double standardDeviation(TechnicalData data, int index, int period, double mean) {

		double stdDev = 0.0;
		
		// Return placeholder value if period > indexed values (beginning of data set)
		if (index - period + 1 < 0) {
		
			
			
		// Calculate standard deviation
		} else {
			for (int i = index - period + 1; i <= index; i++)
				stdDev += Math.pow((data.close(i) - mean), 2);
		
			return Math.pow(stdDev / period, 0.5);
		}
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Testing
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////


	// Test calculating bollinger band data
	public static void main(String[] args) throws FileNotFoundException, IOException, NullPointerException {

		// Get data
		TechnicalData data = TechnicalData.getFile("IVV");
		bollingerBands(data, 21, 2);
	}

}
