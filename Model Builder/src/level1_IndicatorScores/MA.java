package level1_IndicatorScores;

import java.util.ArrayList;

import level0_TechnicalData.TechnicalData;
import model.Model;

public class MA {


	/////////////////////////////////////////////////////////////////////////////////////////////////
	// Wrapper Method for Simple Moving Average Normalized Score Calculations
	/////////////////////////////////////////////////////////////////////////////////////////////////


	/**
	 * Calculate list of moving averages for a given stock data set
	 * @param data Data set being used
	 * @param start Index to begin calculating moving averages for
	 * @param end Last index to have moving average calculated for
	 * @param period How far to take moving average of data
	 * @param npl Normalization period length multiplier
	 * @return List of values in range of [-1,1] based on close proximity to moving average
	 */
	public static ArrayList<Double> maScores(TechnicalData data, int start, int end, int period, int npl) {

		int rangeLimit = period * npl;
		ArrayList<Double> movingAverages = new ArrayList<Double>();		
		ArrayList<Double> list = new ArrayList<Double>();

		// Calculate Moving Averages (begin at earlier index for normalization range needs)
		for (int i = start - (period * npl); i <= end; i++)
			movingAverages.add(movingAverage(data, i, period));

		// Convert Moving Averages into score values
		for (int i = start; i <= end; i++) 
			list.add(
					Model.round(
							normalize(data, i, start, rangeLimit, movingAverages)
							,6)
					);

		return list;

	}


	/////////////////////////////////////////////////////////////////////////////////////////////////
	// Wrapper for List of Moving Averages
	/////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Calculate list of moving averages for a given stock data set
	 * @param data Data set being used
	 * @param start Index to begin calculating moving averages for
	 * @param end Last index to have moving average calculated for
	 * @param period How far to take moving average of data
	 */
	public static ArrayList<Double> movingAverages(TechnicalData data, int start, int end, int period) {
		
		ArrayList<Double> list = new ArrayList<Double>();
		
		for (int i = start; i <= end; i++)
			list.add(movingAverage(data, i, period));
		
		return list;
	}
	

	/////////////////////////////////////////////////////////////////////////////////////////////////
	// Wrapper Method for Moving Average Cross Analysis
	/////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * True = ma0 < ma1
	 * False = ma0 >= ma1
	 * @param data Technical data set to calculate values for
	 * @param start First index to calculate moving average cross for
	 * @param end Last index to calculate moving average cross for
	 * @param p0 First moving average period length
	 * @param p1 Second moving average period length
	 * @return When p1 was greater than p0 (True)
	 */
	public static ArrayList<Double> maCross(TechnicalData data, int start, int end, int p0, int p1) {
		
		ArrayList<Double> ma0 = movingAverages(data, start, end, p0);
		ArrayList<Double> ma1 = movingAverages(data, start, end, p1);
		ArrayList<Double> list = new ArrayList<Double>();
		
		for (int i = 0; i < ma0.size(); i++) {
			
		if (ma0.get(i) > ma1.get(i)) list.add(0.0);
		else list.add(1.0);
		
		}
		
		return list;
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	// Normalization Helper Methods
	/////////////////////////////////////////////////////////////////////////////////////////////////


	/**
	 * Helper method to calculate moving average for given index in data set
	 * @param data Data set being used
	 * @param index Index in data set to calculate moving average for
	 * @param period Period to calculate moving average for
	 * @return Moving average value at given index
	 */
	public static double movingAverage(TechnicalData data, int index, int period) {

		double sum = 0.0;
		
		// Catch exceptions at beginning of data set when index < period
		if (index < period) {
			
			int count = 0;
			
			for (int i = 0; i <= index; i++) {
				sum += data.close(i);
				count++;
			}
			
			return sum / count;
		
		} else { 
		
			for (int i = index - period + 1; i <= index; i++) sum += data.close(i);
			return sum / period;
		}
	}


	/**
	 * Helper method
	 * @param start First index in list to get data for.  Used for calculating index offset
	 * @param dataIndex Index in data set to calculate MA score for
	 * @param rangeLimit Period * Normalization Period Length Multiplier
	 * @return Value in range of [-1,1] based on close proximity to moving average
	 */
	public static double normalize(TechnicalData data, int dataIndex, int start, int rangeLimit, ArrayList<Double> movingAverages) {

		// Create index variables for TechnicalData and MovingAverages
		int di = dataIndex;							// Technical Data Index
		int mai = dataIndex - start + rangeLimit;	// Moving Averages Index

		return rescale(
				data.close(dataIndex), 
				movingAverages.get(dataIndex-start+rangeLimit), 
				getRange(data, di, mai, rangeLimit, movingAverages)
				);

	}


	/**
	 * Convert close and moving average values to [0, 1] range
	 * @param close Close value at index
	 * @param movAvg Moving Average value at index (mean)
	 * @param range Highest and lowest normalized values previously calculated
	 * @return Converted value
	 */
	public static double rescale(double close, double movAvg, double[] range) {

		double x = close - movAvg;
		return (-1) * (x - range[1]) / (range[0] - range[1]) + 1;

	}
	
	
	/**
	 * Helper method in normalization procedure
	 * @param data Technical data set to perform calculations on
	 * @param dataIndex Index to track place in technical data list
	 * @param movingAveragesIndex Index to track moving average to technical data list
	 * @param rangeLimit 
	 * @param movingAverages List of all moving average values
	 * @return Maximum and minimum close - moving average values over given index range
	 */
	public static double[] getRange(TechnicalData data, int dataIndex, int movingAveragesIndex, int rangeLimit, ArrayList<Double> movingAverages) {

		// Create local index variables
		int di = dataIndex - rangeLimit;
		int mai = movingAveragesIndex - rangeLimit;

		// Set initial values
		double x = data.close(di) - movingAverages.get(mai);
		double max = x;
		double min = x;

		while (di <= dataIndex) {

			// Set new x value
			x = data.close(di) - movingAverages.get(mai);

			// Determine if maximum or minimum x values need updating
			if (max < x) max = x;
			if (min > x) min = x;

			// Increment variables
			di++;
			mai++;
		}

		double[] range = {max, min};
		return range;
	}


	/////////////////////////////////////////////////////////////////////////////////////////////////
	// Testing
	/////////////////////////////////////////////////////////////////////////////////////////////////


	public static void main(String[] args) {

		TechnicalData data = new TechnicalData("IVV");
		int start = data.getIndexByTimeStamp("2014");
		int end = data.getIndexByTimeStamp("2015");
		int period = 21*77;
		int npl = 3;

		ArrayList<Double> ma = maScores(data, start, end, period, npl);
		for (double value : ma) System.out.println(value);
	}
}
