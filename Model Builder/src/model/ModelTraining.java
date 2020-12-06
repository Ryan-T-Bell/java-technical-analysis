package model;

import java.util.ArrayList;

import level1_IndicatorScores.IndicatorScores;
import level2_ProfileSelection.ProfileSelection;
import level3_ThresholdAbstraction.ThresholdAbstraction;

/**
 * Create neural network model given time stamp.
 * 1. Create all training levels.
 * 2. Store model versions that perform the best.
 * 
 * @author Ryan Bell
 */
public class ModelTraining {

	
	public static void main(String[] args) {
		
		new ModelTraining("2011", "2014");
	
	}
	

	////////////////////////////////////////////////////////////////////////////////////////////////
	// Constructor for Neural Network Training
	////////////////////////////////////////////////////////////////////////////////////////////////


	public ModelTraining(String start, String end) {

		// Create training directories if necessary
		createDirectories();
		
		IndicatorScores.level_1_Training(start, end);		// Technical Analysis
		
		System.out.println("Level 1 Training Complete");
		Model.printTime();
		
		ProfileSelection.level_2_Training(start, end);		// Profile Selection (High, Medium, and Low Identifying)
		
		System.out.println("Level 2 Training Complete");
		Model.printTime();
		
		ThresholdAbstraction.level_3_Training(start, end);	// Create thresholds for Technical Analysis indicators and simulate buying and selling

		System.out.println("Level 3 Training Complete");
		Model.printTime();
		
		
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Create Directories 
	////////////////////////////////////////////////////////////////////////////////////////////////


	/** Create all directories needed for model */
	public static void createDirectories() {

		// Technical Analysis Folders
		for (String symbol : Variables.SYMBOLS) {
			
			// Level 1: Technical Analysis
			Write.createFolder(Variables.LEVEL_1 + symbol);
			
			// Level 2: Profile Selection
			Write.createFolder(Variables.LEVEL_2);
		}
		
		// Level 3: Threshold Abstraction
		for (String indicator : Variables.INDICATORS) {
			for (String thresholdAbstraction : Variables.THRESHOLD_ABSTRACTIONS) {
				Write.createFolder(Variables.LEVEL_3 + indicator + "/" + thresholdAbstraction);
			}
		}
		
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	// Helper Methods
	////////////////////////////////////////////////////////////////////////////////////////////////


	/** 
	 * Divide symbols list into arrays that will be processed in simultaneously running threads
	 * @return Parsed symbols list
	 */
	public static ArrayList<String[]> parseSymbolsList() {


		// 1. Determine how many threads will run at one time (based on best performance) //////////


		int threadCount = Variables.mWorkingThreads;


		// 2. Create all other necessary variables /////////////////////////////////////////////////


		int i = 0;														// How many symbol names have been added to each String array
		String[] threadGroup = new String[threadCount];  				// Array of ETF symbols that will be run at one time
		ArrayList<String[]> symbolsList = new ArrayList<String[]>();	// ArrayList of thread groups that will be returned


		// 3. Parse symbols list ///////////////////////////////////////////////////////////////////


		for (String symbol : Variables.SYMBOLS) {

			// 3a. Build symbol list that will be grouped together /////////////////////////////////

			if (i < threadCount) {

				threadGroup[i] = symbol;
				i++;

			}

			// 3b. Thread group built: Add to ArrayList and reset variables ////////////////////////

			else {

				i = 0;
				symbolsList.add(threadGroup);
				threadGroup = new String[threadCount];

			}
		}

		// Add last group to list if last group not fully built
		if (i != 0) symbolsList.add(threadGroup);


		// 4. Return built list ////////////////////////////////////////////////////////////////////


		return symbolsList;

	}

	
	/** 
	 * Divide symbols list into arrays that will be processed in simultaneously running threads
	 * @param threads Number of threads to parse list into
	 * @return Parsed symbols list
	 */
	public static ArrayList<String[]> parseSymbolsList(int threads) {


		// 1. Determine how many threads will run at one time (based on best performance) //////////


		int threadCount = threads;


		// 2. Create all other necessary variables /////////////////////////////////////////////////


		int i = 0;														// How many symbol names have been added to each String array
		String[] threadGroup = new String[threadCount];  				// Array of ETF symbols that will be run at one time
		ArrayList<String[]> symbolsList = new ArrayList<String[]>();	// ArrayList of thread groups that will be returned


		// 3. Parse symbols list ///////////////////////////////////////////////////////////////////


		for (String symbol : Variables.SYMBOLS) {

			// 3a. Build symbol list that will be grouped together /////////////////////////////////

			if (i < threadCount) {

				threadGroup[i] = symbol;
				i++;

			}

			// 3b. Thread group built: Add to ArrayList and reset variables ////////////////////////

			else {

				i = 0;
				symbolsList.add(threadGroup);
				threadGroup = new String[threadCount];

			}
		}

		// Add last group to list if last group not fully built
		if (i != 0) symbolsList.add(threadGroup);


		// 4. Return built list ////////////////////////////////////////////////////////////////////


		return symbolsList;

	}

}
