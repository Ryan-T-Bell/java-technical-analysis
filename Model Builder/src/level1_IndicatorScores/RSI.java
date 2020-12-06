package level1_IndicatorScores;

import java.util.ArrayList;

import level0_TechnicalData.TechnicalData;
import model.Model;

/**
 * Class used to measure market trends and when a stock is over-bought and over-sold
 * 
 * 100 - 100/(1 + RS)
 * RS = [Average of x days' up closes] / [Average of x days' down closes]
 * @author Ryan Bell
 */
public class RSI {

	
	/**
	 * @param data Data set to get RSI values for
	 * @param period Number of days to calculate RSI for (9, 14, 25)
	 * @return List of RSI values for data set
	 */
	public static ArrayList<Double> relativeStrengthIndex(TechnicalData data, int start, int end, int period) {

		double rs;				// Relative strength
		double rsiValue;		// Relative strength index value
		double delta;			// Difference between last close and current one
		double up   = 0.0;		// Used to track average of days where close > open
		double down = 0.0;      // Used to track average of days where close < open		
	
		
		ArrayList<Double> list = new ArrayList<Double>();

		for (int i = start; i <= end; i++) {
			
			for (int j = i - period + 1; j <= i; j++) {

				delta = data.close(j) - data.close(j-1);
				
				if (delta > 0) up += delta;
				else down -= delta;
			}

			rs = up / down;
			rsiValue = (100.0 - (100.0 / (1 + rs))) / 100.0;
			
			list.add(Model.round(rsiValue,6));

			// Reset variables
			up = 0.0;
			down = 0.0;
		}
		
		
		return list;
	}


	/**
	 * Alterate RSI calculations
	 * @param data Data set to get RSI values for
	 * @param period Number of days to calculate RSI for (9, 14, 25)
	 * @param cf Compression factor (used to 
	 * @return List of RSI values for data set
	 */
	public static ArrayList<Double> relativeStrengthIndex(TechnicalData data, int start, int end, int period, int cf) {

		double rs;				// Relative strength
		double rsiValue;		// Relative strength index value
		double delta;			// Difference between last close and current one
		double up   = 0.0;		// Used to track average of days where close > open
		double down = 0.0;      // Used to track average of days where close < open		
	
		TechnicalData data2 = TechnicalData.modifyTechnicalData(data, cf);
		ArrayList<Double> list = new ArrayList<Double>();

		for (int i = start; i <= end; i++) {
			
			for (int j = i - period + 1; j <= i; j++) {

				delta = data2.close(j) - data2.close(j-1);
				
				if (delta > 0) up += delta;
				else down -= delta;
			}

			rs = up / down;
			rsiValue = (100.0 - (100.0 / (1 + rs)))/100.0;
			
			list.add(Model.round(rsiValue,6));

			// Reset variables
			up = 0.0;
			down = 0.0;
		}
		
		return list;
	}
	


	/**
	 * Alterate RSI calculations.  Stabilize values by taking RSI moving average
	 * @param data Data set to get RSI values for
	 * @param period Number of days to calculate RSI for (9, 14, 25)
	 * @param cf Compression factor (used to alter TechnicalData period)
	 * @param rsiMAPeriod Calculate the moving average of RSI values
	 * @return List of RSI values for data set
	 */
	public static ArrayList<Double> rsiMA(TechnicalData data, int start, int end, int period, int cf, int rsiMAPeriod) {

		double rsiMA;
		ArrayList<Double> rsi = relativeStrengthIndex(data, start-rsiMAPeriod, end, rsiMAPeriod, cf);
		ArrayList<Double> list = new ArrayList<Double>();
		
		// Calculate moving averages of rsi values
		for (int i = rsiMAPeriod; i < rsi.size(); i++) {
			
			rsiMA = 0.0;
			
			for (int j = i - rsiMAPeriod+1; j <= i; j++) rsiMA += rsi.get(j);
			
			list.add(Model.round(rsiMA/rsiMAPeriod,6));
		}

		return list;
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	// Testing
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	public static void main(String[] args) {
		TechnicalData data = new TechnicalData("IVV");
		int start = data.getIndexByTimeStamp("2014");
		int end = data.size() - 1;
		int period = 14;
		
		rsiMA(data, start, end, period, 77, 25);
		//for (double val : rsi) System.out.println(val);
		
	}
}
