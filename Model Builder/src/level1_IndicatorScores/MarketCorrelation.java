package level1_IndicatorScores;

import java.util.ArrayList;

import level0_TechnicalData.TechnicalData;
import model.Model;

/**
 * Class used to compare two different markets (ETFs)
 * 
 * @author Ryan Bell
 */
public class MarketCorrelation {


	/**
	 * Compare two market price differences
	 * @param data0 TechnicalData set analyzing
	 * @param symbol1 Market to compare data set to
	 * @param start Index in TechnicalData (data0) to start calculations for
	 * @param end Index in TechnicalData (data0) to stop calculations for
	 * @return Compared values between two markets
	 */
	public static ArrayList<Double> compareMarketPrices(TechnicalData data0, String symbol1, int start, int end) {

		TechnicalData data1 = new TechnicalData(symbol1);
		int index0 = start;
		int index1;
		
		// Offset for start->other data set
		try {
			index1 = data1.getIndexByTimeStamp(data0.get(start).getTimeStamp());	
		} catch (NullPointerException e) {
			index1 = -1;
		}	
		
		double value0 = 1.0;
		double value1 = 1.0;

		ArrayList<Double> list = new ArrayList<Double>();

		// Determine if both ETFs are within valid range
		boolean rangeValid;
		if (index1 < 0) rangeValid = false; 
		else rangeValid = true;

		// Compare markets
		while (index0 <= end) {

			if (rangeValid) {
				value0 = value0 * (data0.close(index0) / data0.close(index0-1));
				value1 = value1 * (data1.close(index1) / data1.close(index1-1));
				index0++; index1++;

				list.add(Model.round(value0-value1, 6));
			}
			else {
				list.add(0.0); index0++;
			}
		}

		return list;
	}


	/**
	 * Compare two market volume differences
	 * @param data0 TechnicalData set analyzing
	 * @param symbol1 Market to compare data set to
	 * @param start Index in TechnicalData (data0) to start calculations for
	 * @param end Index in TechnicalData (data0) to stop calculations for
	 * @return Compared values between two markets
	 */
	public static ArrayList<Double> compareMarketVolumes(TechnicalData data0, String symbol1, int start, int end) {

		TechnicalData data1 = new TechnicalData(symbol1);

		int index0 = start;
		int index1;
		
		try {
			index1 = data1.getIndexByTimeStamp(data0.get(start).getTimeStamp());	// Offset for start->other data set
		} catch (NullPointerException e) {
			index1 = -1;
		}	
		
		double value0 = 1.0;
		double value1 = 1.0;

		ArrayList<Double> list = new ArrayList<Double>();

		// Determine if both ETFs are within valid range
		boolean rangeValid;
		if (index1 < 0) rangeValid = false; 
		else rangeValid = true;

		// Compare markets
		while (index0 <= end) {

			if (rangeValid) {
				value0 = value0 * ((0.0+data0.volume(index0)) / data0.volume(index0-1));
				value1 = value1 * ((0.0+data1.volume(index1)) / data1.volume(index1-1));
				index0++; index1++;

				list.add(Model.round(value0-value1 ,6));
			}
			else {
				list.add(0.0); index0++;
			}
		}

		return list;
	}


	/** Determine if both ETFs are within valid range */
	public static boolean checkRange(TechnicalData data0, TechnicalData data1, int index0, int index1) {
		if (data0.timeStamp(index0).equals(data1.timeStamp(index1))) return true;
		return false;
	}


	public static void main(String[] args) {

		TechnicalData data = new TechnicalData("IVV");
		int start = data.getIndexByTimeStamp("2014");
		int end = data.getIndexByTimeStamp("2015");
		ArrayList<Double> list = compareMarketVolumes(data, "SH", start, end);
		for (double value : list) System.out.println(value);
	}
}
