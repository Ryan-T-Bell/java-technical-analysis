package level3_ThresholdAbstraction;

import java.util.ArrayList;
import level0_TechnicalData.TechnicalData;
import level1_IndicatorScores.IndicatorScores;
import level2_ProfileSelection.ProfileSelection;
import model.Model;
import model.Variables;
import model.Write;


/** 
 * Abstract Level 1: Indicator Scores and Report Performance by Profile Type
 * @author Ryan Bell 
 * */
public class ThresholdAbstraction extends Thread {


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Thread Implementation and Class Variables
	////////////////////////////////////////////////////////////////////////////////////////////////


	private int mStrategy;
	private String mStart;
	private String mEnd;

	private static final double mTargetMin       = Variables.mMinIndicatorRange;
	private static final double mTargetMax       = Variables.mMaxIndicatorRange;
	private static final double mTargetDelta     = (mTargetMax - mTargetMin) / Variables.mIterationsDelta;

	private static final double mFixedBarMin	 = Variables.mFixedBarMin;		// Fixed Bar Minimum Value
	private static final double mFixedBarMax     = Variables.mFixedBarMax;		// Fixed Bar Maximum Value
	private static final double mFixedBarDelta   = Variables.mFixedBarDelta;	// Fixed Bar Change per Iteration

	private static final double mRandomMin		 = Variables.mRandomMin;		// Lowest Random Entry/Exit Value
	private static final double mRandomMax       = Variables.mRandomMax;		// Highest Random Entry/Exit Value
	private static final double mRandomDelta     = Variables.mRandomDelta;		// Random Threshold Change per Iteration
	
	private static final int mDecimal 			 = Variables.mDecimal;			// Decimal place to round threshold values to

	/** Constructor Wrapper: Create 5 threads that  */
	public static void level_3_Training(String start, String end) {

		// Reset stored files in case files already exist
		resetThresholdAbstraction();

		// 0: Target Entry    - Target Exit
		// 1: Target Entry    - Fixed Bar Exit
		// 2: Target Entry    - Random Exit
		// 3: Fixed Bar Entry - Target Exit
		// 4: Random Entry    - Target Exit
		for (int i = 0; i < 5; i++) 
			new ThresholdAbstraction(i, start, end);

	}


	/** Constructor: Creates thread and sets method parameters*/
	public ThresholdAbstraction(int strategy, String start, String end) {
		mStrategy = strategy;
		mStart = start;
		mEnd = end;
		abstractThresholds(mStrategy, mStart, mEnd);
		// this.start();
	}


	/** Override: Code implemented after thread starts */
	public void run() {

		abstractThresholds(mStrategy, mStart, mEnd);

	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Neural Network Level 3: Threshold Abstraction
	////////////////////////////////////////////////////////////////////////////////////////////////


	/**
	 * Create sorted list of threshold evaluations by performance of all indicators based on given data 
	 * Wrapper method for indicators package that calls all technical analysis methods and saves each indicator value to .txt file
	 * @param data Technical data to complete technical analysis for
	 * @param start Start date of technical analysis calculation
	 * @param end Last date of technical analysis calculation
	 * @return All technical analysis data in list of arrays
	 */
	public static void abstractThresholds(int strategy, String start, String end) {


		for (String indicator : Variables.INDICATORS) {


			// 1. Initialize Necessary Variables ///////////////////////////////////////////////////


			String directory = Variables.LEVEL_3 + indicator + "/";

			// 2. Determine Maximum and Minimum Values for Thresholds //////////////////////////////


			double minEntry, maxEntry, deltaEntry;
			double minExit, maxExit, deltaExit;
			boolean writeEntry, writeExit;
			String entrySymbol, exitSymbol;
			
			// STRATEGY 0: Target Entry and Target Exit
			if (strategy == 0) {
				minEntry   = mTargetMin;
				maxEntry   = mTargetMax;
				deltaEntry = mTargetDelta;

				minExit    = mTargetMin;
				maxExit    = mTargetMax;
				deltaExit  = mTargetDelta;
				
				entrySymbol = "(T)";
				exitSymbol  = "(T)";
				
				writeEntry = true;
				writeExit = true;
			}

			// STRATEGY 1: Target Entry and Fixed Bar Exit
			else if (strategy == 1) {
				minEntry   = mTargetMin;
				maxEntry   = mTargetMax;
				deltaEntry = mTargetDelta;

				minExit    = mFixedBarMin;
				maxExit    = mFixedBarMax;
				deltaExit  = mFixedBarDelta;
				
				entrySymbol = "(T)";
				exitSymbol  = "(F)";
				
				writeEntry = true;
				writeExit = false;
			}

			// STRATEGY 2: Target Entry and Random Exit
			else if (strategy == 2) {
				minEntry   = mTargetMin;
				maxEntry   = mTargetMax;
				deltaEntry = mTargetDelta;

				minExit    = mRandomMin;
				maxExit    = mRandomMax;
				deltaExit  = mRandomDelta;
				
				entrySymbol = "(T)";
				exitSymbol  = "(R)";
				
				writeEntry = true;
				writeExit = false;
			}

			// STRATEGY 3: Fixed Bar Entry and Target Exit
			else if (strategy == 3) {
				minEntry   = mFixedBarMin;
				maxEntry   = mFixedBarMax;
				deltaEntry = mFixedBarDelta;

				minExit    = mTargetMin;
				maxExit    = mTargetMax;
				deltaExit  = mTargetDelta;
				
				entrySymbol = "(F)";
				exitSymbol  = "(T)";
				
				writeEntry = false;
				writeExit = true;
			}

			// STRATEGY 4: Random Entry and Target Exit
			else {
				minEntry   = mRandomMin;
				maxEntry   = mRandomMax;
				deltaEntry = mRandomDelta;

				minExit    = mTargetMin;
				maxExit    = mTargetMax;
				deltaExit  = mTargetDelta;
				
				entrySymbol = "(R)";
				exitSymbol  = "(T)";
				
				writeEntry = false;
				writeExit = true;
			}


			// 3. Execute All Entry - Exit Threshold Simulations Over Data Set and Write to File ///


			for (double entry = minEntry; entry <= maxEntry; entry = Model.round(entry + deltaEntry, mDecimal)) {
				for (double exit = minExit; exit <= maxExit && exit < entry; exit = Model.round(exit + deltaExit, mDecimal)) {

					// Print Progress
					System.out.println(indicator + " : " + entry + " " + entrySymbol + "/" + exit + " " + exitSymbol);
					
					String longModel  = abstraction(strategy, true,  indicator, entry, exit, start, end);

					// Commented out short model b/c can't invest that way /////////////////////////////////////////////////
					// String shortModel = abstraction(strategy, false, indicator, entry, exit, start, end);

					// Write Entry Results
					if (writeEntry) {
						
						String prefix = exit + entrySymbol;
						
						Write.writeToFile(directory + "Entry/Long/" + entry + ".txt",  prefix + longModel + ",\r\n", false);
						//Write.writeToFile(directory + "Entry/Short/" + entry + ".txt", prefix + shortModel + ",\r\n", false);
					}
					
					// Write Exit Results
					if (writeExit) {
						
						String prefix = entry + exitSymbol;
						
						Write.writeToFile(directory + "Exit/Long/" + exit + ".txt",  prefix + longModel + ",\r\n", false);
						//Write.writeToFile(directory + "Exit/Short/" + exit + ".txt", prefix + shortModel + ",\r\n", false);
					}
				}
			}
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Strategy Execution Method
	////////////////////////////////////////////////////////////////////////////////////////////////


	/** Entry Threshold / Exit Threshold for Long Model */
	private static String abstraction(int strategy, boolean type, String indicator, double entry, double exit, String start, String end) {


		// Store results from each symbol
		ArrayList<TrainingAccount> results = new ArrayList<TrainingAccount>();


		for (String symbol : Variables.SYMBOLS) {


			// 1. Initialize Necessary Variables ///////////////////////////////////////////////////////


			TrainingAccount account = new TrainingAccount(0.0);

			double close;				// Close variable at index
			double indicatorScore;		// Technical analysis variable at index
			int profile;				// Profile variable at index (0, 1, or 2)

			// Level 0: Data
			TechnicalData data = new TechnicalData(symbol);
			int startIndex = data.getIndexByTimeStamp(start);
			int endIndex = data.getIndexByTimeStamp(end);

			// Level 1: Technical Analysis
			ArrayList<Double> indicatorScores = IndicatorScores.getIndicatorScores(symbol, indicator);

			// Level 2: Profile Selection
			ArrayList<Integer> profiles = ProfileSelection.getProfiles(symbol);

			// Variable that tracks periods elapsed for fixed bar entry/exit
			int periodCount = 0;

			// 2. Calculate threshold abstraction values for each profile type /////////////////////////


			// Run entry/exit strategy through entire data set
			for (int i = startIndex; i <= endIndex; i++) {


				// Update variables for index
				close = data.close(i);
				indicatorScore = indicatorScores.get(i-startIndex);
				profile = profiles.get(i-startIndex);


				// 3. Execute Trading Entry / Exit Strategy ////////////////////////////////////


				if      (strategy == 0) account.targetTarget(type, entry, exit, close, indicatorScore, profile);
				else if (strategy == 1) account.targetFixedBar(type, entry, exit, periodCount, close, indicatorScore, profile);
				else if (strategy == 2) account.targetRandom(type, entry, exit, close, indicatorScore, profile);
				else if (strategy == 3)	account.fixedBarTarget(type, entry, periodCount, exit, close, indicatorScore, profile);
				else 					account.randomTarget(type, entry, exit, close, indicatorScore, profile);

				// Continuously reset period counter until position is exited, increments otherwise
				if (account.positionHeld()) periodCount = 0;
				periodCount++;

			}


			// 4. Exit any held position and add account to ArrayList<TrainingAccount> /////////////


			account.simExit(data.close(endIndex));
			results.add(account);

		}


		// 4. Combine all ThresholdAbstraction performance results for entry-exit combination


		return averageResults(results);
	}		


	////////////////////////////////////////////////////////////////////////////////////////////////
	// General Helper Methods
	////////////////////////////////////////////////////////////////////////////////////////////////


	/** @return String containing threshold entry and exit performance values */
	private static String averageResults(ArrayList<TrainingAccount> accountList) {


		// Create Final Training Account that Will Be Used to Sum Up All Results
		TrainingAccount finalAccount = new TrainingAccount(0);
		
		// Initialize Necessary Variables
		int i = 0;

		
		// Sum All Threshold Results
		for (TrainingAccount account : accountList) {

			finalAccount.sum(account);
			i++;
			
		}
		
		// Divide All Threshold Results (Taking Average)
		finalAccount.divide(i);
		
		return finalAccount.toString();
	}


	/** Reset all store Level 3: Threshold Abstraction Files */
	private static void resetThresholdAbstraction() {

		for (String indicator : Variables.INDICATORS) {

			String directory = Variables.LEVEL_3 + indicator + "/";

			for (String thresholdAbstraction : Variables.THRESHOLD_ABSTRACTIONS) {
				
				Write.writeToFile(directory + thresholdAbstraction + "/Long.txt", "", true);
				// Write.writeToFile(directory + thresholdAbstraction + "/Short.txt",   "", true);
			}

		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Main Method for Testing
	////////////////////////////////////////////////////////////////////////////////////////////////


	public static void main(String[] args) {

		level_3_Training("2011", "2014");


	}
}

