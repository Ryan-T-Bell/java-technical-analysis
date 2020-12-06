package level1_IndicatorScores;

import java.util.ArrayList;

import level0_TechnicalData.TechnicalData;

/**
 * Class used to calculate daily range values and compare them to a moving average
 * @author Ryan Bell
 */
public class Volatility {

	
	/**
	 * @param data Data to calculate values for
	 * @param startDate Beginning date in TechnicalData list
	 * @param endDate End date in TechnicalData list
	 * @param period Number of days to calculate volatility (range) for
	 * @return List of volatility values
	 */
	public static ArrayList<Double> volatility(TechnicalData data, int start, int end, int period) {

		ArrayList<Double> list = new ArrayList<Double>();

		// Variable to store high - low range in period
		double volatility;
		
		// Create initial list of volatility moving average
		double volatilitySum = initialVolatilitySum(data, start, period);
		
		for (int i = start; i <= end; i++) {
			
			// Initialize volatility variable / update it each loop
			volatility = data.high(i) - data.low(i);
			
			// Increment volatility moving average score
			volatilitySum += volatility;
			volatilitySum -= data.high(i-period) - data.low(i-period);
			
			// Add Volatility / VolatilityMA to list
			list.add(volatility / (volatilitySum/period));
		}

		return list;
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Helper Method
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////

	
	/** Calculate initial sum of volatility values before start */
	private static double initialVolatilitySum(TechnicalData data, int start, int period) {
		
		double sum = 0.0;
		
		for (int i = start - period; i >= 0 && i < start; i++)
			sum += data.high(i) - data.low(i);
		
		return sum;
	}
	
}
