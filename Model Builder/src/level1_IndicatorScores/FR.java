package level1_IndicatorScores;

import java.util.ArrayList;

import level0_TechnicalData.TechnicalData;
import model.Model;
import model.Variables;

// Fibonacci Retracement
public class FR {

	/**
	 * Primary Wrapper Method Without Modifying Technical Data
	 * @param data Technical analysis data to calculate fibonacci retracement values
	 * @param start First index in data set to calculate for
	 * @param end Last index in data set to calculate for
	 * @param period Period to calculate retracement over (set high and low)
	 * @return List of proximity to support and resistance lines
	 */
	public static ArrayList<Double> fibonacciRetracement(TechnicalData data, int start, int end, int period) {

		ArrayList<Double> list = new ArrayList<Double>();

		// Variables to store changing fibonacci support and resistance lines
		double close, delta, p100, p61_8, p50, p38_2, p23_6, p0;

		// Calculate values
		for (int i = start; i <= end; i++) {

			p100 = data.high(i);
			p0 = data.low(i);

			// Find maximum and minimum values over period
			for (int j = i-period+1; j < start; j++) {
				if (data.high(j) > p100) p100 = data.high(j);
				if (data.low(j) < p0) p0 = data.low(j);
			}

			// Calculate Fibonacci Retracement Lines
			close = data.close(i);
			delta = p100-p0;
			p61_8 = p0 + (delta * 0.618);
			p50   = p0 + (delta * 0.5);
			p38_2 = p0 + (delta * 0.382);
			p23_6 = p0 + (delta * 0.236);

			list.add(
					Model.round(
							evaluateFR(close, p100, p61_8, p50, p38_2, p23_6, p0)
							,6)
					);
		}
		return list;
	}


	/**
	 * Primary Wrapper Method Without Modifying Technical Data
	 * @param data Technical analysis data to calculate fibonacci retracement values
	 * @param start First index in data set to calculate for
	 * @param end Last index in data set to calculate for
	 * @param period Period to calculate retracement over (set high and low)
	 * @param cf Compression factor to modify period of technical data
	 * @return List of proximity to support and resistance lines
	 */
	public static ArrayList<Double> fibonacciRetracement(TechnicalData data0, int start, int end, int period, int cf) {

		TechnicalData data = TechnicalData.modifyTechnicalData(data0, cf);
		ArrayList<Double> list = new ArrayList<Double>();

		// Variables to store changing fibonacci support and resistance lines
		double close, delta, p100, p61_8, p50, p38_2, p23_6, p0;

		// Calculate values
		for (int i = start; i <= end; i++) {

			p100 = data.high(i);
			p0 = data.low(i);

			// Find maximum and minimum values over period
			for (int j = i-period+1; j < start; j++) {
				if (data.high(j) > p100) p100 = data.high(j);
				if (data.low(j) < p0) p0 = data.low(j);
			}

			// Calculate Fibonacci Retracement Lines
			close = data.close(i);
			delta = p100-p0;
			p61_8 = p0 + (delta * 0.618);
			p50   = p0 + (delta * 0.5);
			p38_2 = p0 + (delta * 0.382);
			p23_6 = p0 + (delta * 0.236);

			list.add(
					Model.round(
							evaluateFR(close, p100, p61_8, p50, p38_2, p23_6, p0)
							,6)
					);
		}
		return list;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Helper Methods
	////////////////////////////////////////////////////////////////////////////////////////////////


	/** Percentage points below next support [0] and resistance[1] level */
	public static double evaluateFR(double close, double p100, double p61_8, double p50, double p38_2, double p23_6, double p0) {

		double value;

		if (close > p61_8) {
			value = (close - p61_8) / (p100 - p61_8);
		} else if (close > p50) {
			value = (close - p50) / (p61_8 - p50);
		} else if (close > p38_2) {
			value = (close - p38_2) / (p50 - p38_2);
		} else if (close > p23_6) {
			value = (close - p23_6) / (p38_2 - p23_6);
		} else {
			value = (close - p0) / (p23_6 - p0);
		}

		return value;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	// Testing
	////////////////////////////////////////////////////////////////////////////////////////////////


	public static void main(String[] args) {

		TechnicalData data = new TechnicalData("IVV");
		int start = data.getIndexByTimeStamp("2015");
		int end = data.getIndexByTimeStamp("2016");
		int period = Variables.PIM;

		ArrayList<Double> fr = FR.fibonacciRetracement(data, start, end, period);
		for (Double v : fr) System.out.println(v);
	}
}
