package level1_IndicatorScores;

import java.util.ArrayList;

import level0_TechnicalData.TechnicalData;
import model.Model;
import model.Variables;

public class EMA {


	/////////////////////////////////////////////////////////////////////////////////////////////////
	// Wrapper Method for Score Calculations
	/////////////////////////////////////////////////////////////////////////////////////////////////


	/**
	 * Calculate list of exponential moving averages for a given stock data set
	 * @param data Data set being used
	 * @param start Index to begin calculating moving averages for
	 * @param end Last index to have moving average calculated for
	 * @param period How far to take moving average of data
	 * @param npl Normalization period length multiplier
	 * @return List of values in range of [-1,1] based on close proximity to moving average
	 */
	public static ArrayList<Double> emaScores(TechnicalData data, int start, int end, int period, int npl) {

		// Create initial variables
		double k = 2.0 / (period + 1.0);								// Smoothing Factor
		double ema = initialMovAvgSum(data, start, period) / period; 	// Initial simple moving average sum (1 short of start)
		int rangeLimit = period * npl;									// Used to offset indexes

		ArrayList<Double> emas = new ArrayList<Double>();		
		ArrayList<Double> list = new ArrayList<Double>();

		// Calculate Moving Averages (begin at earlier index for normalization range needs)
		for (int i = start - (period * npl); i <= end; i++) {
			ema = exponentialMovingAverage(data, i, period, ema, k);
			emas.add(ema);
		}

		// Convert Moving Averages into score values
		for (int i = start; i <= end; i++) 
			list.add(
					Model.round(
							MA.normalize(data, i, start, rangeLimit, emas)
							,6)
					);

		return list;

	}


	/////////////////////////////////////////////////////////////////////////////////////////////////
	// Wrapper Method for List of EMA Values
	/////////////////////////////////////////////////////////////////////////////////////////////////


	/**
	 * Calculate list of exponential moving averages for a given stock data set
	 * Exponential Moving Average (Today) = (P * K) + (EMA * (1 - K))
	 * P = Current Price
	 * K = Smoothing Factor 2/(1+Period)
	 * EMA = Previous Exponential Moving Average 
	 * 
	 * @param data Data set being used
	 * @param start Index to begin calculating moving averages for
	 * @param end Last index to have moving average calculated for
	 * @param period How far to take moving average of data
	 * @return Exponential moving average list for given days
	 */
	public static ArrayList<Double> exponentialMovingAverages(TechnicalData data, int start, int end, int period) {


		// 1. Create initial variables /////////////////////////////////////////////////////////////


		double k = 2.0 / (period + 1.0);								// Smoothing Factor
		double ema = initialMovAvgSum(data, start, period) / period; 	// Initial simple moving average sum (1 short of start)
		ArrayList<Double> list = new ArrayList<Double>();				// List to return


		// 2. Calculate all following EMA values ///////////////////////////////////////////////////


		for (int i = start; i <= end; i++) {

			ema = exponentialMovingAverage(data, i, period, ema, k);
			list.add(ema);

		}

		return list;
	}


	/////////////////////////////////////////////////////////////////////////////////////////////////
	// Wrapper Method for Moving Average Cross Analysis
	/////////////////////////////////////////////////////////////////////////////////////////////////


	/**
	 * 1 = ma0 < ma1
	 * 0 = ma0 >= ma1
	 * @param data Technical data set to calculate values for
	 * @param start First index to calculate moving average cross for
	 * @param end Last index to calculate moving average cross for
	 * @param p0 First moving average period length
	 * @param p1 Second moving average period length
	 * @return When p1 was greater than p0 (1)
	 */
	public static ArrayList<Double> emaCross(TechnicalData data, int start, int end, int p0, int p1) {

		ArrayList<Double> ema0 = exponentialMovingAverages(data, start, end, p0);
		ArrayList<Double> ema1 = exponentialMovingAverages(data, start, end, p1);
		ArrayList<Double> list = new ArrayList<Double>();

		for (int i = 0; i < ema0.size(); i++) {

			if (ema0.get(i) > ema1.get(i)) list.add(0.0);
			else list.add(1.0);

		}

		return list;
	}


	/////////////////////////////////////////////////////////////////////////////////////////////////
	// Helper Methods
	/////////////////////////////////////////////////////////////////////////////////////////////////


	/**
	 * Single EMA calculation
	 * @param data Data set to calculate EMA for
	 * @param index Index in data set to calculate EMA for
	 * @param period Number of days to calculate 
	 * @param lastEMA The previously calculated exponential moving average value
	 * @param k Smoothing factor for EMA calculation
	 * @return EMA value for given index and data
	 */
	public static double exponentialMovingAverage(TechnicalData data, int index, int period, double lastEMA, double k) {

		if (lastEMA < 0.0) {

			return MA.movingAverage(data, index, period);

		} else {

			return (data.close(index) * k) + (lastEMA * (1 - k));

		}
	}


	/**
	 * Helper method to get initial sum of n (period) nodes before start index
	 * @param start Start index higher method will begin calculating moving averages for
	 * @param period Number of days to calculate moving average for
	 * @return Sum of preceding close values
	 */
	public static double initialMovAvgSum(TechnicalData data, int start, int period) {

		double sum = 0.0;

		// Start index will not cause issues with 
		if (start >= period) {
			for (int i = start-period; i >= 0 && i < start; i++) 
				sum += data.close(i);
		} 

		return sum;
	}


	/////////////////////////////////////////////////////////////////////////////////////////////////
	// Testing
	/////////////////////////////////////////////////////////////////////////////////////////////////

	public static void main(String[] args) {
		
		TechnicalData data = new TechnicalData("IVV");
		int start = data.getIndexByTimeStamp("2015");
		int end = data.getIndexByTimeStamp("2016");
		int period = Variables.PIM*21;
		int npl = 5;
		
		ArrayList<Double> ema = EMA.emaScores(data, start, end, period, npl);
		for (Double v : ema) System.out.println(v);
	}


}
