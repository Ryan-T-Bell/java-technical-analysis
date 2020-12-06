package model;

import java.util.ArrayList;

/**
 * Class used to store model testing/optimization data
 * 		
 * @author Ryan Bell
 */
public class TrainingData {


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Set Methods
	////////////////////////////////////////////////////////////////////////////////////////////////


	/**
	 * Primary method used to store ModelData Information
	 * @param symbol Stock symbol to invest in
	 * @param indicator Technical analysis indicator
	 * @param startIndex First index calculation is done for
	 * @param endIndex Last index log extends to
	 * @param list ArrayList of { performance, buyThreshold, sellThreshold, win%, #trades, #trades/year, avgProfit, 
	 * 							  avgLoss, avgProfit%, avgLoss%, maxProfitTrade, maxLossTrade }
	 */
	public static void setModelData(String symbol, String indicator, int startIndex, int endIndex, String modelData) {
		
		// Write to desktop data file
		Write.writeToFile(Variables.TRAINING+indicator+"/"+symbol+".txt", modelData, true);

	}


	/**
	 * Convert ArrayList<Double[]> to comma separated value list
	 * @param performance { performance, buyThreshold, sellThreshold, win%, 
	 * 						#trades, #trades/year, avgProfit,  avgLoss, avgProfit%,
	 * 						avgLoss%, maxProfitTrade, maxLossTrade }
	 * @return List converted to String
	 */
	public static String modelDataToString(ArrayList<Double[]> performance) {

		int arrayCounter = 0;
		StringBuilder array = new StringBuilder();
		StringBuilder list = new StringBuilder();

		for (int i = 0; i < performance.size(); i++) {

			// Reset variables
			arrayCounter = 0;
			array = new StringBuilder();

			// Convert Double[] to List
			for (Double value : performance.get(i)) {
				if (arrayCounter < 10) {
					array.append(value + ",");
					arrayCounter++;
				}
				else {
					array.append(value + ";\n");
					arrayCounter++;
				}
			}

			// Add node string to list
			list.append(array.toString());
		}

		return list.toString();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Sorting Data
	////////////////////////////////////////////////////////////////////////////////////////////////


	/**
	 * Methods that takes ArrayList<Double[]> as parameter and converts to a sorted String
	 * @param performance ArrayList of returnPercentage, buyThreshold, sellThreshold
	 * @return ArrayList<Double[]> converted to String
	 */
	public static String sortByReturnPercentage(ArrayList<Double[]> performance) {
		return modelDataToString(removeDuplicates(quickSort(performance)));
	}


	/**
	 * Remove all duplicate performance values
	 * @param performance Sorted list from quick sort method
	 * @return Sorted list without duplicates
	 */
	public static ArrayList<Double[]> removeDuplicates(ArrayList<Double[]> performance) {

		ArrayList<Double[]> list = new ArrayList<Double[]>();

		// Add First Value to List
		double lastValue = performance.get(0)[0];
		list.add(performance.get(0));

		// Step Through List
		for (int i = 0; i < performance.size(); i++) {
			if (lastValue != performance.get(i)[0]) {	// If performance is not duplicate of last added value
				list.add(performance.get(i));			// Add non-duplicate to list
				lastValue = performance.get(i)[0];		// Update comparative "last added value"
			}
		}

		return list;
	}


	/**
	 * Implementation of reverse Quick Sort (Largest, ... , Smallest) Return Percentage
	 * 1. Choose pivot point
	 * 2. Create left (larger) and right (smaller) arrays and sort parent into them
	 * 3. Recursively call on child (left & right) arrays if necessary
	 * 4. Combine left and right arrays
	 * @param performance ArrayList of returnPercentage, buyThreshold, sellThreshold
	 * @return ArrayList<Double[]> sorted
	 */
	public static ArrayList<Double[]> quickSort(ArrayList<Double[]> parentArray) {

		// 1. Choose pivot point
		double pivot = parentArray.get(0)[0];

		// 2. Create left (larger) and right (smaller) arrays and sort parent into them
		ArrayList<Double[]> leftArray = new ArrayList<Double[]>();
		ArrayList<Double[]> rightArray = new ArrayList<Double[]>();

		// Sort into left (smaller than pivot) and right (bigger than pivot) arrays
		for (int i = 1; i < parentArray.size(); i++) {

			if (pivot < parentArray.get(i)[0]) leftArray.add(parentArray.get(i));
			else rightArray.add(parentArray.get(i));

		}

		// Add pivot to left array
		leftArray.add(parentArray.get(0));

		// 3. Recursive call on each array
		if (leftArray.size() > 1) leftArray = quickSort(leftArray);
		if (rightArray.size() > 1) rightArray = quickSort(rightArray);

		// 4. Combine left and right arrays and return combined list
		for (int i = 0; i < rightArray.size(); i++) leftArray.add(rightArray.get(i));
		return leftArray;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Testing
	////////////////////////////////////////////////////////////////////////////////////////////////


	public static void main(String[] args) {

		
	}
}
