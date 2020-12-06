package level2_ProfileSelection;

import java.util.ArrayList;

import level0_TechnicalData.TechnicalData;
import model.Read;
import model.Variables;
import model.Write;

public class ProfileSelection extends Thread {


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Main Method for Testing
	////////////////////////////////////////////////////////////////////////////////////////////////


	public static void main(String[] args) {

		level_2_Training("2012", "2014");
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	// Wrapper Method for Class
	////////////////////////////////////////////////////////////////////////////////////////////////


	/** Wrapper methods for class to calculate all profile threshold values */
	public static void level_2_Training(String start, String end) {

		for (String symbol : Variables.SYMBOLS)
			new ProfileSelection(symbol, start, end);

	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Thread Implementation, Constructor, and Class Variables
	////////////////////////////////////////////////////////////////////////////////////////////////


	private String mSymbol;
	private String mStart;
	private String mEnd;


	/** Constructor to calculate level 2 values for neural network */
	public ProfileSelection(String symbol, String start, String end) {
		mSymbol = symbol;
		mStart = start;
		mEnd = end;
		this.start();
	}


	/** Calculate profile threshold  */
	public void run() {
		profileSelection(mSymbol, mStart, mEnd);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Execution Method for Class
	////////////////////////////////////////////////////////////////////////////////////////////////


	public static void profileSelection(String symbol, String start, String end) {


		// 1. Initialize necessary variables ///////////////////////////////////////////////////

		ArrayList<Double> sortedList = new ArrayList<Double>();					// ArrayList to store moving average score calculations
		ArrayList<Double> unsortedList = new ArrayList<Double>();				// ArrayList to store moving average scores in unsorted order

		TechnicalData data = new TechnicalData(symbol);							// Technical Analysis data of ETF symbol

		int indexStart = data.getIndexByTimeStamp(start);						// Index in TechnicalData of start
		int indexEnd = data.getIndexByTimeStamp(end);							// Index in TechnicalData of end

		double sum15 = initialMovAvgSum(data, indexStart, 15*Variables.PIM);	// Sum of 15 day moving average
		double sum200 = initialMovAvgSum(data, indexStart, 200*Variables.PIM);	// Sum of 200 day moving average

		double movingAverage;


		// 2. Calculate all moving averages and insert into list in order //////////////////////


		for (int i = indexStart; i <= indexEnd; i++) {

			// Increment/Decrement moving average sums
			sum15  += data.close(i);
			sum200 += data.close(i);

			sum15  -= data.close(i - 15*Variables.PIM);
			sum200 -= data.close(i - 200*Variables.PIM);

			// Calculate 15/200 moving average and add to lists
			movingAverage = (sum15 / 15*Variables.PIM) / (sum200 / 200*Variables.PIM);
			sortedList = sortedInsert(sortedList, movingAverage);
			unsortedList.add(movingAverage);
		}

		// 3. Identify 1/3 and 2/3 Cutoff Thresholds and Write to File /////////////////////////


		int index1 = (int) Math.round(sortedList.size() * 0.333333);
		int index2 = (int) Math.round(sortedList.size() * 0.666667);

		double profile1 = sortedList.get(index1);
		double profile2 = sortedList.get(index2);


		Write.writeToFile(Variables.LEVEL_2 + symbol + ".txt", profile1 + "," + profile2, true);

		
		// 4. Create and Store Indexed List of Profiles by Type (0=Low, 1=Medium, 3=High) //////////

		
		double value;
		ArrayList<Integer> profileList = new ArrayList<Integer>();

		for (int i = 0; i < unsortedList.size(); i++) {

			value = unsortedList.get(i);

			if (value < profile1) profileList.add(0);			// Low Profile

			else if (value < profile2) profileList.add(1);		// Medium Profile

			else profileList.add(2);							// High Profile

		}

		Write.integerArrayList(Variables.LEVEL_2 + symbol + "List.txt", profileList, true);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Helper Methods for Class
	////////////////////////////////////////////////////////////////////////////////////////////////


	/**
	 * Helper method to get initial sum of n (period) nodes before start index
	 * @param start Start index higher method will begin calculating moving averages for
	 * @param period Number of days to calculate moving average for
	 * @return Sum of preceding close values
	 */
	private static double initialMovAvgSum(TechnicalData data, int start, int period) {

		double sum = 0.0;

		// Start index will not cause issues with 
		if (start >= period) {
			for (int i = start-period; i >= 0 && i < start; i++) 
				sum += data.close(i);
		} 

		return sum;
	}


	/** 
	 * Insert moving average parameter in least - greatest order 
	 * @return List with movingAverage parameter inserted in sorted order
	 */
	private static ArrayList<Double> sortedInsert(ArrayList<Double> list, double value) {


		for (int i = 0; i < list.size(); i++) {

			// Insert value in list 
			if (value < list.get(i)) {
				list.add(i, value);
				return list;
			}
		}

		// Insert and return list if value is new greatest value or list is empty
		list.add(value);
		return list;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Get Methods for Class
	////////////////////////////////////////////////////////////////////////////////////////////////


	/** Return first (1/3) threshold value in file*/
	public static double get1(String symbol) {

		StringBuilder s = new StringBuilder();
		String file = Read.readFromFile(Variables.LEVEL_2 + symbol + ".txt");

		for (int i = 0; i < file.length() && file.charAt(i) != ','; i++) 
			s.append(file.charAt(i));

		return Double.parseDouble(s.toString());
	}


	/** Return second (2/3) threshold value in file */
	public static double get2(String symbol) {

		StringBuilder s = new StringBuilder();
		String file = Read.readFromFile(Variables.LEVEL_2 + symbol + ".txt");

		int i = 0;

		// Cycle to 
		while (i < file.length() && file.charAt(i) != ',') i++;

		i++;

		while (i < file.length()) {
			s.append(file.charAt(i));
			i++;
		}

		return Double.parseDouble(s.toString());
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Get Profiles from File
	////////////////////////////////////////////////////////////////////////////////////////////////


	/** Return profile of parameter value */
	/*public static int getProfile(String symbol, double profileScore) {

		if (profileScore < get1(symbol)) return 0;
		if (profileScore < get2(symbol)) return 1;
		return 2;
	}
	*/


	/**
	 * Convert text file to ArrayList<Integer>
	 *
	 * 0: Low Profile (Lower 1/3)
	 * 1: Medium Profile (Median 1/3)
	 * 2: High Profile (Upper 1/3)
	 * 
	 * @param symbol Symbol that is being invested in
	 * @param indicator Technical analysis indicator
	 * @return ArrayList of double values from text file
	 */
	public static ArrayList<Integer> getProfiles(String symbol) {
		
		String directory = Variables.LEVEL_2 + symbol + "List.txt";
		return Read.getIntegerArrayList(directory);

	}
}
