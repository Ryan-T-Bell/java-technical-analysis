package level3_ThresholdAbstraction;

import java.util.Random;

import model.Variables;

/**
 * Class used to create account for N/N training and backtest.
 * Simulates taking long / short positions and exiting them.
 * 
 * @author Ryan Bell
 */
public class TrainingAccount {


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Class Variables
	////////////////////////////////////////////////////////////////////////////////////////////////


	// Threshold Entry and Exit Variables
	private String mEntry;
	private String mExit;

	// Account Balance Variables
	private double mAccountBalance;
	private int mStocksOwned;
	private double mEntryPrice;
	private int mEntryProfile;

	// Performance Variables
	private double mTotalPerformance;
	private double mHighPerformance;
	private double mMediumPerformance;
	private double mLowPerformance;

	// Win/Loss Percentage Variables
	private int mTotalWins;
	private int mHighWins;
	private int mMediumWins;
	private int mLowWins;

	private int mTotalPositions;
	private int mHighPositions;
	private int mMediumPositions;
	private int mLowPositions;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Constructors
	////////////////////////////////////////////////////////////////////////////////////////////////


	public TrainingAccount(double startingBalance) {
		mAccountBalance = startingBalance;
		mStocksOwned = 0;
		
		// Performance Variables
		mTotalPerformance = 0.0;
		mHighPerformance = 0.0;
		mMediumPerformance = 0.0;
		mLowPerformance = 0.0;

		// Win/Loss Percentage Variables
		mTotalWins = 0;
		mHighWins = 0;
		mMediumWins = 0;
		mLowWins = 0;

		mTotalPositions = 0;
		mHighPositions = 0;
		mMediumPositions = 0;
		mLowPositions = 0;
	}



	////////////////////////////////////////////////////////////////////////////////////////////////
	// Neural Network Training Methods
	////////////////////////////////////////////////////////////////////////////////////////////////


	/** Simulate long (buy stock) */
	public void simLong(double price, int profile) {

		int position = (int) (Variables.mPositionSize / price);

		mAccountBalance -= (price * position);
		mAccountBalance -= Variables.mCommission;
		mStocksOwned = position;

		mEntryPrice = price;
		mEntryProfile = profile;

		// Round account balance to 2 decimal places
		mAccountBalance = round(mAccountBalance);
	}


	/** Simulate short (sell stock) */
	public void simShort(double price, int profile) {

		double position = (int) (Variables.mPositionSize / price);

		mAccountBalance += price * position;
		mAccountBalance -= Variables.mCommission;
		mStocksOwned -= position;

		mEntryPrice = price;

		// Round account balance to 2 decimal places
		mAccountBalance = round(mAccountBalance);
	}


	/**
	 * Exit short or long positions
	 * @param price Close price being executed
	 * @param index Index of execution in TechnicalData list
	 */
	public void simExit(double price) {


		// 1. Return If No Position Held ///////////////////////////////////////////////////////////


		if (mStocksOwned == 0) return;


		// 2. Update Tracking Variables ////////////////////////////////////////////////////////////


		// Low
		if (mEntryProfile == 0) {
			mLowPerformance += getPositionDifference(price);
			if (getPositionDifference(price) > 0) mLowWins++;
			mLowPositions++;
		}

		// Medium
		else if (mEntryProfile == 1) {
			mMediumPerformance += getPositionDifference(price);
			if (getPositionDifference(price) > 0) mMediumWins++;
			mMediumPositions++;
		}

		// High
		else if (mEntryProfile == 2) {
			mHighPerformance += getPositionDifference(price);
			if (getPositionDifference(price) > 0) mHighWins++;
			mHighPositions++;
		}

		// Totals
		mTotalPerformance += getPositionDifference(price);
		if (getPositionDifference(price) > 0) mTotalWins++;
		mTotalPositions++;


		// 3. Exit position ////////////////////////////////////////////////////////////////////////


		mAccountBalance += (price * mStocksOwned) - Variables.mCommission;
		mStocksOwned = 0;

		mEntryPrice = -1;

		// Round account balance to 2 decimal places
		mAccountBalance = round(mAccountBalance);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// General Helper Method
	////////////////////////////////////////////////////////////////////////////////////////////////


	/** Get the current account value */
	public double getValue(double price) {

		return round(mAccountBalance + mStocksOwned * price);
	}


	/**
	 * Round numbers to 2 decimal places in order to calculate with full money value and save space on data storage
	 * @param value Value to round
	 * @return Rounded value
	 */
	public static double round(double value) {

		return Math.round(value*100.0) / 100.0;

	}


	/** Add all variable values together */
	public void sum(TrainingAccount account) {


		// Performance Variables
		mTotalPerformance  += account.mTotalPerformance;
		mHighPerformance   += account.mHighPerformance;
		mMediumPerformance += account.mMediumPerformance;
		mLowPerformance    += account.mLowPerformance;

		// Win/Loss Percentage Variables
		mTotalWins  += account.mTotalWins;
		mHighWins   += account.mHighWins;
		mMediumWins += account.mMediumWins;
		mLowWins    += account.mLowWins;

		mTotalPositions  += account.mTotalPositions;
		mHighPositions   += account.mHighPositions;
		mMediumPositions += account.mMediumPositions;
		mLowPositions    += account.mLowPositions;

	}


	/** Divide all performance values by parameter input (for finding average after sum) */
	public void divide(int divisor) {

		// Performance Variables
		mTotalPerformance  = mTotalPerformance / divisor;
		mHighPerformance   = mHighPerformance / divisor;
		mMediumPerformance = mMediumPerformance / divisor;
		mLowPerformance    = mLowPerformance / divisor;

		// Win/Loss Percentage Variables
		mTotalWins  = mTotalWins / divisor;
		mHighWins   = mHighWins / divisor;
		mMediumWins = mMediumWins / divisor;
		mLowWins    = mLowWins / divisor;

		mTotalPositions  = mTotalPositions / divisor;
		mHighPositions   = mHighPositions / divisor;
		mMediumPositions = mMediumPositions / divisor;
		mLowPositions    = mLowPositions / divisor;

	}

	/** Return performance of account */
	public String toString() {

		return 	mTotalPerformance + "";
		
				// + "," + mTotalWins  + "," + mTotalPositions  + ":" +
				// mHighPerformance   + "," + mHighWins   + "," + mHighPositions   + ":" +  
				// mMediumPerformance + "," + mMediumWins + "," + mMediumPositions + ":" +
				// mLowPerformance    + "," + mLowWins    + "," + mLowPositions    + "\r\n";
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Get Methods
	////////////////////////////////////////////////////////////////////////////////////////////////


	/** Return price of last entry */
	public double getEntryPrice() {
		return mEntryPrice;
	}


	/** Return true if position is already held, false otherwise */
	public boolean positionHeld() {

		if (mStocksOwned == 0) return false;
		else return true;

	}


	/** Current account balance (from price parameter) - entry account balance */
	private double getPositionDifference(double price) {

		double currentBalance = price * mStocksOwned;
		double entryBalance = mEntryPrice * mStocksOwned;
		return round(currentBalance - entryBalance - (2*Variables.mCommission));
	}


	public String getEntry() { return mEntry; }
	public String getExit() {  return mExit; }
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	// Set Methods
	////////////////////////////////////////////////////////////////////////////////////////////////


	public void setThresholds(String entry, String exit) {

		mEntry = entry;
		mExit = exit;

	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Trading Strategy Methods
	////////////////////////////////////////////////////////////////////////////////////////////////


	/** STRATEGY 0: Target Entry and Target Exit */
	public void targetTarget(boolean type, double entry, double exit, double close, double indicatorScore, int profile) {

		// Long Model
		if      (!positionHeld() && type && targetEntry(entry, indicatorScore, type)) simLong(close, profile);
		else if ( positionHeld() && type && targetExit(exit, indicatorScore, type))   simExit(close);

		// Short Model
		if      (!positionHeld() && !type && targetEntry(entry, indicatorScore, type)) simShort(close, profile);
		else if ( positionHeld() && !type && targetExit(exit, indicatorScore, type))   simExit(close);

	}


	/** STRATEGY 1: Target Entry and Fixed Bar Exit */
	public void targetFixedBar(boolean type, double entry, double exit, int periodCount, double close, double indicatorScore, int profile) {

		// Long Model
		if      (!positionHeld() && type && targetEntry(entry, indicatorScore, type)) simLong(close, profile);
		else if ( positionHeld() && type && fixedBar(exit, periodCount))              simExit(close);

		// Short Model
		if      (!positionHeld() && !type && targetEntry(entry, indicatorScore, type)) simShort(close, profile);
		else if ( positionHeld() && !type && fixedBar(exit, periodCount))              simExit(close);

	}


	/** STRATEGY 2: Target Entry and Random Exit */
	public void targetRandom(boolean type, double entry, double exit, double close, double indicatorScore, int profile) {

		// Long Model
		if      (!positionHeld() && type && targetEntry(entry, indicatorScore, type)) simLong(close, profile);
		else if ( positionHeld() && type && random(exit))                             simExit(close);

		// Short Model
		if      (!positionHeld() && !type && targetEntry(entry, indicatorScore, type)) simShort(close, profile);
		else if ( positionHeld() && !type && random(exit)) 							   simExit(close);
	
	}


	/** STRATEGY 3: Fixed Bar Entry and Target Exit */
	public void fixedBarTarget(boolean type, double entry, int periodCount, double exit, double close, double indicatorScore, int profile) {
		
		// Long Model
		if      (!positionHeld() && type && fixedBar(entry, periodCount))           simLong(close, profile);
		else if ( positionHeld() && type && targetExit(exit, indicatorScore, type)) simExit(close);

		// Short Model
		if      (!positionHeld() && !type && fixedBar(entry, periodCount))           simShort(close, profile);
		else if ( positionHeld() && !type && targetExit(exit, indicatorScore, type)) simExit(close);
	
	}
	
	
	/** STRATEGY 5: Random Entry and Target Exit */
	public void randomTarget(boolean type, double entry, double exit, double close, double indicatorScore, int profile) {
		
		// Long Model
		if      (!positionHeld() && type && random(entry)) 					        simLong(close, profile);
		else if ( positionHeld() && type && targetExit(exit, indicatorScore, type)) simExit(close);

		// Short Model
		if      (!positionHeld() && !type && random(entry)) 						 simShort(close, profile);
		else if ( positionHeld() && !type && targetExit(exit, indicatorScore, type)) simExit(close);
	
	}
	

	////////////////////////////////////////////////////////////////////////////////////////////////
	// Entry Strategy Helper Methods
	////////////////////////////////////////////////////////////////////////////////////////////////


	/**
	 * @param positionHeld If a position is held or not
	 * @param entry Entry threshold being tested
	 * @param score Indicator score at index position
	 * @param type True = Long Model, False = Short Model
	 * @return true if executing target entry, false otherwise
	 */
	private boolean targetEntry(double entry, double score, boolean type) {

		// Exit method and return false if position is already held by account
		if (positionHeld()) return false;

		// Enter Long Position if indicator score passes entry threshold
		if (type && score > entry) return true;

		// Enter Long Position if indicator score passes entry threshold
		if (!type && score < entry) return true;

		return false;
	}


	/** @return True if period count is greater than threshold */
	private boolean fixedBar(double periodThreshold, int periodCount) {

		// Enter position if period increment count passes entry threshold
		if (periodCount > periodThreshold) return true;
		else return false;
	}


	/** @return Returns true at random in accordance with percentage input (0.5 = return true 50% of the time) */
	private boolean random(double percentage) {

		// Enter position by random chance
		return (new Random().nextDouble() < percentage);

	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Exit Strategy Helper
	////////////////////////////////////////////////////////////////////////////////////////////////


	/**
	 * @param positionHeld If a position is held or not
	 * @param entry Entry threshold being tested
	 * @param score Indicator score at index position
	 * @return true if executing target exit, false otherwise
	 */
	private boolean targetExit(double exit, double score, boolean type) {

		// Exit method and return false if position is not already held by account
		if (!positionHeld()) return false;

		// Exit Long Position if indicator score passes exit threshold
		if (type && score < exit) return true;

		// Exit Short Position if indicator score passes exit threshold
		if (!type && score > exit) return true;

		return false;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Testing Method
	////////////////////////////////////////////////////////////////////////////////////////////////


	/** Print the current account value */
	public void printValue(double price) {
		System.out.println("///////////////\r\n" +
				"Balance: " + mAccountBalance + 
				"\r\nStock Owned: " + mStocksOwned + 
				" @ (" + price + " ea)" + 
				"\nValue: " + getValue(price)
				);
	}




	public static void main(String args[]) {

		// testTransactions();


	}
}
