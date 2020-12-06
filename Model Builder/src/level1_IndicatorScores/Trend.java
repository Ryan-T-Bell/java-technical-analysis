package level1_IndicatorScores;

import java.util.ArrayList;

import level0_TechnicalData.TechnicalData;
import model.Model;
import model.Variables;

/**
 * Class used to create trend lines based on price action data
 * 
 * @author Ryan Bell
 */
public class Trend {



	/**
	 * Primary wrapper method. Return slope of trend lines over entire data set that connect peaks and troughs  
	 * @param data List of close prices or moving averages of technical data
	 * @param start First index to values for
	 * @param end Last index to calculate values for
	 * @param period Moving average period
	 * @param cf Compression factor (77 = day periods)
	 * @param type True = Peaks, False = Troughs
	 * @return List of slopes from peak to peak or trough to trough x length of continuation (Slope x Length)
	 */
	public static ArrayList<Double> trendLines(TechnicalData data, int start, int end, int period, int cf, boolean type) {

		TechnicalData data2;
		
		// Modify technical data by compressing 
		if (cf > 1) 
			data2 = TechnicalData.modifyTechnicalData(data, cf);
		else 
			data2 = data;
		
		// Create lists
		ArrayList<Double> dydx = derivative(data2, start, end, type);				// First derivative estimations of technical data
		ArrayList<Integer> criticalPoints = criticalPoints(dydx, type);				// dydx indexes that are peaks or troughs
		ArrayList<Double> priceAction = priceAction(data2, start, end, period);		// Close OR moving average prices of technical data
		ArrayList<Double> trendSlopes = new ArrayList<Double>();					// List to return (Slope, Length)

		// Create variables
		int j = 0;
		double length = 0.0;
		int curr, next;
		double slope;

		// Determine and build list of slopes from peak to peak or trough to trough
		for (int i = 0; i < criticalPoints.size()-1; i++) {

			curr = criticalPoints.get(i);							// Current index in dydx to start slope calculation
			next = criticalPoints.get(i+1); 						// Index in dydx to re-calculate slope
			slope = getSlope(priceAction, curr, next);				// Slope from current index to next index in data

			// Reset and calculate length of slope line
			length = next - curr;
			
			while (j <= next && j < dydx.size()) {
				trendSlopes.add(Model.round(slope, 6) * length);
				j++;
			}
		}
		return trendSlopes;
	}
	

	////////////////////////////////////////////////////////////////////////////////////////////////
	// Helper Methods
	////////////////////////////////////////////////////////////////////////////////////////////////


	/**
	 * Approximate derivatives from each data point in TechnicalData
	 * @param data List of close prices or moving averages of technical data
	 * @param start First index to values for
	 * @param end Last index to calculate values for
	 * @return List of slopes
	 */
	public static ArrayList<Double> derivative(ArrayList<Double> data, int start, int end) {

		// Initialize list variable
		double slope;
		ArrayList<Double> dydx = new ArrayList<Double>();

		// Create derivative approximations
		for (int i = 1; i < data.size(); i++) {
			slope = (data.get(i) - data.get(i-1)) / 2.0;
			
			dydx.add(Model.round(slope,6));
			if (i < 2) dydx.add(Model.round(slope,6));		// Add extra for first value in set
		}
		return dydx;
	}


	/**
	 * Approximate derivatives from each data point in TechnicalData
	 * @param data List of close prices or moving averages of technical data
	 * @param start First index to values for
	 * @param end Last index to calculate values for
	 * @return List of slopes
	 */
	public static ArrayList<Double> derivative(TechnicalData data, int start, int end, boolean type) {

		// Initialize list variable
		ArrayList<Double> dydx = new ArrayList<Double>();

		// Create derivative approximations
		for (int i = start; i <= end; i++) {
			if (type) dydx.add(
					Model.round( 
							(data.high(i) - data.high(i-1)) / 2.0
							, 6)
					);
			else dydx.add( 
					Model.round(
							(data.low(i) - data.low(i-1)) / 2.0
							,6)
					);
		}

		return dydx;
	}

	
	/**
	 * Return all index variables in dydx parameter that has a slope that changes from positive to negative (CPs)
	 * @param dydx List of first derivative estimations over data set
	 * @param type If 
	 * @return All critical point indexes in 
	 */
	public static ArrayList<Integer> criticalPoints(ArrayList<Double> dydx, boolean type) {

		ArrayList<Integer> criticalPoints = new ArrayList<Integer>();
		criticalPoints.add(0);											// Starting point to begin evaluation

		// Determine changes form positive to negative slopes (for peak to trough calculations)
		for (int i = 1; i < dydx.size(); i++) {

			if (type) {
				if (dydx.get(i-1) > 0 && dydx.get(i) <= 0) criticalPoints.add(i);
				else if (i == dydx.size()-1) criticalPoints.add(i);
			} else {
				if (dydx.get(i-1) < 0 && dydx.get(i) >= 0) criticalPoints.add(i);
				else if (i == dydx.size()-1) criticalPoints.add(i);
			}
		}

		return criticalPoints;
	}


	/**
	 * @param data Technical data to create 
	 * @param start First index in technical data to start calculations
	 * @param end Last index in technical data to stop calculations
	 * @param period Moving average length
	 * @return Close values or moving average values over data set 
	 */
	public static ArrayList<Double> priceAction(TechnicalData data, int start, int end, int period) {

		if (period <= 1) {
			ArrayList<Double> list = new ArrayList<Double>();
			for (int i = start; i <= end; i++) list.add(Model.round(data.close(i), 6));
			return list;
		}
		else {
			return MA.movingAverages(data, start, end, period);
		}
	}


	/**
	 * @param priceAction Price values from technical data in desired index range
	 * @param curr Current index critical point
	 * @param next Next critical point
	 * @return Slope between parameter critical points
	 */
	public static double getSlope(ArrayList<Double> priceAction, int curr, int next) {
		return (priceAction.get(next) - priceAction.get(curr)) / Variables.NP;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Testing
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static void main(String[] args) {
	
		
		// for (Double v : trend) System.out.println(v);
	}
}
