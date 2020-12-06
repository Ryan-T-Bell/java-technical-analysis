package level1_IndicatorScores;

import java.util.ArrayList;

import level0_TechnicalData.TechnicalData;
import model.Model;

/**
 * 1. MACD Signal Crossover
 * 2. MACD Centerline Crossover (12 EMA cross 26 EMA)
 * 3. Divergence with price action (Most reliable)
 * 
 * @author Ryan Bell
 */
public class MACD {
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	// Calculate MACD Values for Entire Data Set
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * Used to calculate the moving average convergence divergence line
	 * @param data Technical analysis data for stock or ETF
	 * @param start Beginning index in TechnicalData to begin calculations
	 * @param end Last index in TechnicalData to calculate MACD for
	 * @param ema12 12 Day exponential moving average list
	 * @param ema26 26 Day exponential moving average list
	 * @return ArrayList for indexes [start,end] of MACD values packaged as nodes
	 */
	public static ArrayList<MACDNode> macd(TechnicalData data, int start, int end, ArrayList<Double> ema12, ArrayList<Double> ema26) {

		
		// 1. Create all necessary variables ///////////////////////////////////////////////////////
		
		
		double MACD;									// 12 Day EMA - 26 Day EMA
		double signal = 0.0;  							// Exponential Moving Average of MACD
		double histogram;								// Difference between MACD value and signal
		ArrayList<MACDNode> list = new ArrayList<MACDNode>();

		
		// 2. Calculate signal initial value ///////////////////////////////////////////////////////
		
		
		signal = getInitialSignal(data, start, ema12, ema26);
		
		
		// 3. Calculate MACD and matching signal (MACD 9 day EMA) values ///////////////////////////
		
		
		for (int i = start; i <= end; i++) {
			
			MACD      = ema12.get(i - start) - ema26.get(i - start);
			signal    = MACD * 0.2 + signal * (0.8);
			histogram = MACD - signal;
			
			list.add(new MACDNode(MACD, signal, histogram));
			
		}
		
		return list;
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	// MACD Signal Methods (Used to Make MACD Findings Useful to Neural Network)
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * @param data Technical analysis data for stock or ETF
	 * @param start Beginning index in TechnicalData to begin calculations
	 * @param end Last index in TechnicalData to calculate MACD for
	 * @param ema12 12 Day exponential moving average list
	 * @param ema26 26 Day exponential moving average list
	 * @return When MACD Diverges from 
	 */
	public static ArrayList<Double> macdDivergence(TechnicalData data, int start, int end, ArrayList<Double> ema12, ArrayList<Double> ema26) {
		
		MACDNode node;
		ArrayList<Double> list = new ArrayList<Double>();
		ArrayList<MACDNode> macd = macd(data, start, end, ema12, ema26);
		
		for (int i = start; i <= end; i++) {
			
			node = macd.get(i - start);
			list.add(data.close(i) - node.macd());
			
		}
		
		return list;
	}
	
	
	/**
	 * @param data Technical analysis data for stock or ETF
	 * @param start Beginning index in TechnicalData to begin calculations
	 * @param end Last index in TechnicalData to calculate MACD for
	 * @param ema12 12 Day exponential moving average list
	 * @param ema26 26 Day exponential moving average list
	 * @return List of all histogram (MACD - Signal) values over [start, end] indexes
	 */
	public static ArrayList<Double> macdHistogram(TechnicalData data, int start, int end, ArrayList<Double> ema12, ArrayList<Double> ema26) {
		
		MACDNode node;
		ArrayList<Double> list = new ArrayList<Double>();
		ArrayList<MACDNode> macd = macd(data, start, end, ema12, ema26);
		
		for (int i = start; i <= end; i++) {
			
			node = macd.get(i - start);
			list.add(Model.round(node.histogram(), 6));
			
		}
		
		return list;
	}
	
 
	////////////////////////////////////////////////////////////////////////////////////////////////
	// Testing
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * Helper method used to calculate signal value (used to create histogram)
	 * @param data Technical analysis data for stock or ETF
	 * @param start Beginning index in TechnicalData to begin calculations
	 * @return Signal value (average of ema12 - ema26)
	 */
	public static double getInitialSignal(TechnicalData data, int start, ArrayList<Double> ema12, ArrayList<Double> ema26) {
		
		double signal = 0.0;
		
		for (int i = 0; i < 9; i++)
			signal += ema12.get(i) - ema26.get(i);
		
		return signal/9;
		
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	// Testing
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	public static void main (String[] args) {

		TechnicalData data = new TechnicalData("IVV");
		int start = data.getIndexByTimeStamp("2014");
		int end = data.getIndexByTimeStamp("2015");
		
		ArrayList<Double> ema12 = EMA.exponentialMovingAverages(data, start, end, 12*77);
		ArrayList<Double> ema26 = EMA.exponentialMovingAverages(data, start, end, 26*77);
		
		macd(data, start, end, ema12, ema26);
	}
}
