package level1_IndicatorScores;

import java.util.ArrayList;

import level0_TechnicalData.TechnicalData;
import model.Model;
import model.Variables;

/**
 * Stochastic Momentum Index
 * 
 * @author Ryan Bell
 */
public class SMI {


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Wrapper Method for Class
	////////////////////////////////////////////////////////////////////////////////////////////////


	/**
	 * Primary wrapper method for class
	 * @param data TechnicalData set to calculate stochastic momentum index ratios for
	 * @param start First index to calculate value for
	 * @param end Last index to calculate value for
	 * @param period EMA period length (1 day = Indicators.PIM)
	 * @return List of ratio values
	 */
	public static ArrayList<Double> stochasticMomentumIndex(TechnicalData data, int start, int end, int period) {

		ArrayList<Double[]> list0 = initialRatios(data, start, end, period);	// 0 = numerator; 1 = denominator
		ArrayList<Double[]> list1 = firstEMA(list0, start, period);				// 0 = numerator; 1 = denominator
		return secondEMA(list1, period);

	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Helper Methods for Class
	////////////////////////////////////////////////////////////////////////////////////////////////


	/**
	 * Calculate (close - low) / (high - low) list w/ period*2 offset
	 * @param data Data set to calculate ratio for
	 * @param start First index in data set to calculate for
	 * @param end Last index in data set to calculate for
	 * @param maPeriod Exponential moving average period
	 * @return 2D Array of {(close-low), (high-low)}
	 */
	public static ArrayList<Double[]> initialRatios(TechnicalData data, int start, int end, int period) {


		// 1. Get initial numerator and denominator values
		boolean HLSet = false;						// Ensures variable reset

		int start0 = start - (period*2) + 1;

		double high = data.high(start0); 
		double low = data.low(start0);

		ArrayList<Double[]> list = new ArrayList<Double[]>();


		// 2. Calculate all numerator (close - low) and denominator (high - low)
		for (int i = start0; i <= end; i++) {

			for (int j = i-period+1; j <= i; j++) {

				if (HLSet) {
					if (data.high(j) > high) high = data.high(j);
					if (data.low(j) < low) low = data.low(j);
				} else {
					HLSet = true;
					high = data.high(j);
					low = data.low(j);
				}
			}

			Double[] node = {(data.close(i)-low), (high - low)};
			list.add(node);
			HLSet = false;

		}
		return list;
	}


	/** Calculate first exponential moving average of (close-low)/(high-low) */
	public static ArrayList<Double[]> firstEMA(ArrayList<Double[]> list0, int start, int period) {


		double emaTop = 0.0;
		double emaBottom = 0.0;
		double k = 2.0 / (period + 1.0);	// Smoothing Factor

		int start1 = period;
		ArrayList<Double[]> list = new ArrayList<Double[]>();

		// Calculate initial EMA sums
		for (int i = 0; i < period-1; i++) {
			emaTop += list0.get(i)[0];
			emaBottom += list0.get(i)[1];
		}

		emaTop = emaTop / period;
		emaBottom = emaBottom / period;
		
		for (int i = start1; i < list0.size(); i++) {

			// Calculate EMA values
			emaTop = ema(list0.get(i)[0], k, emaTop);
			emaBottom = ema(list0.get(i)[1], k, emaBottom);

			// Add to list
			Double[] node = {emaTop, emaBottom};
			list.add(node);
		}
		
		return list;
	}


	/** Calculate second exponential moving average of (close-low)/(high-low) */
	public static ArrayList<Double> secondEMA(ArrayList<Double[]> emaList, int period) {

		double emaTop = 0.0;
		double emaBottom = 0.0;
		double k = 2.0 / (period + 1.0);	// Smoothing Factor
		
		int start2 = period-1;
		ArrayList<Double> list = new ArrayList<Double>();

		// Calculate initial sum values
		for (int i = 0; i < period-1; i++) {
			emaTop += emaList.get(i)[0];
			emaBottom += emaList.get(i)[1];
		}

		emaTop = emaTop / period;
		emaBottom = emaBottom / period;

		for (int i = start2; i < emaList.size(); i++) {
			
			// Calculate EMA values
			emaTop = ema(emaList.get(i)[0], k, emaTop);
			emaBottom = ema(emaList.get(i)[1], k, emaBottom);
			
			list.add(Model.round(emaTop / emaBottom, 6));
		}


		return list;
	}


	/** Calculated exponential moving average of inputed data */
	public static double ema(double close, double k, double lastEMA) {
		return (close * k) + (lastEMA * (1 - k));
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Testing
	////////////////////////////////////////////////////////////////////////////////////////////////


	public static void main(String[] args) {

		// Get data
		TechnicalData data = new TechnicalData("IVV");
		int start = data.getIndexByTimeStamp("2014");
		int end = data.getIndexByTimeStamp("2015");
		int period = Variables.PIM * 21;

		ArrayList<Double> list = SMI.stochasticMomentumIndex(data, start, end, period);
		for (double value: list) System.out.println(value);
	}

}
