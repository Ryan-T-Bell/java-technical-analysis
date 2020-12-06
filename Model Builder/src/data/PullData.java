package data;

import java.io.IOException;
import java.time.LocalDateTime;

import level0_TechnicalData.WebInputStream;
import model.Read;
import model.Variables;
import model.Write;

public class PullData extends Thread {


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Main Method for Testing and Execution of Data Collection
	////////////////////////////////////////////////////////////////////////////////////////////////


	/** Main Method for testing 
	 * @throws InterruptedException */
	public static void main (String args[]) throws IOException, InterruptedException {
		
		// 1. Erase temporary data storage files
		for (String symbol : Variables.SYMBOLS)
			Write.writeToFile(Variables.TEMP_DATA + symbol + ".txt", "", true);
		
		// 2. Call wrapper method that creates threads for each updating symbol (PullETFData)
		updateData();
		
		// 3. Call wrapper method that parses temporary data files and appends periods to permanent storage files in 5 minute periods (PareTempData)
		
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Class Variables
	////////////////////////////////////////////////////////////////////////////////////////////////


	// Boundaries before price and volume data 
	public static String mPrefixPrice = "\"},\"currency\":\"USD\",\"regularMarketPrice\":{\"raw\":";
	public static char mSufixPrice = ',';
	public static String mPrefixVolume = "\"volume\":{\"raw\":";
	public static char mSufixVolume = ',';

	// List of symbols that thread collects data for
	public String mSymbol;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Wrapper Methods for Class to Begin ETF Data Collection
	////////////////////////////////////////////////////////////////////////////////////////////////


	/** Wrapper Method for class.  Creates all threads that store period data 
	 * @throws InterruptedException */
	public static void updateData() throws InterruptedException {


		// 1. Create all thread time files (to prevent null pointer exception //////////////////////
		
		
		for (String symbol : Variables.SYMBOLS) 
			Write.writeToFile(Variables.TEMP_DATA + symbol + "Time.txt", "", true);


		// 2. Call data collection threads (write to temporary files) //////////////////////////////
		
		
		for (String symbol : Variables.SYMBOLS) new PullData(symbol);


		// 3. Restart threads if they fail /////////////////////////////////////////////////////////
		
		
		while (checkEnd()) {

			Thread.sleep(9000);			// Pause thread checking for 9 seconds

			for (String symbol : Variables.SYMBOLS) { 
				if (checkThread(symbol)) new PullData(symbol);
			}
		}

	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Thread Implementation
	////////////////////////////////////////////////////////////////////////////////////////////////	


	/** Begin data collection for all symbols in parameter input */
	public PullData(String symbol) {
		mSymbol = symbol;
		this.start();
	}


	/** Code that runs when constructor is created */
	public void run() {

		try {

			startDataThread(mSymbol);

		} catch (Exception e) {

		}
	}


	/** 
	 * Start Data Collection.  Wrapper method for writeCurrentData
	 * @throws IOException In case of Input Output Error 
	 * @throws InterruptedException */
	public static void startDataThread(String symbol) throws IOException, InterruptedException {

		// Holds method if trading day hasn't started
		while (checkStart());

		// Write timestamp, price, and volume data to temporary file
		while (checkEnd()) {
			Thread.sleep(1000);				// Pause data collection for 1 second
			writeCurrentData(symbol);		// Write current data
		}

	}


	/** Return True if thread ended, false otherwise */
	public static boolean checkThread(String symbol) {

		String timeStamp = Read.readFromFile(Variables.TEMP_DATA + symbol + ".txt");
		if (compareTime(timeStamp)) return true;

		return false;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Get Current Data Methods
	////////////////////////////////////////////////////////////////////////////////////////////////


	/** Write current time stamp, price, and volume data to temporary file */
	public static void writeCurrentData(String symbol) {


		// Write current stock data for each symbol in array String parameter
		String page = getPage(symbol);
		String output = "\r\n" + getTimeLong() + "," + getPrice(page) + "," + getVolume(page);

		// Write stock data to file
		Write.writeToFile(Variables.TEMP_DATA + symbol + ".txt", output , false);

		// Write last update to tracking file to restart thread if ended
		Write.writeToFile(Variables.TEMP_DATA + symbol + "Time.txt", getTimeLong() , true);

	}


	/**
	 * Return yahoo page html as a String
	 * @param symbol Symbol to get data for
	 * @return Yahoo html as a String
	 */
	public static String getPage(String symbol) {


		// 1. Declare return variable //////////////////////////////////////////////////////////////


		String page = "";


		// 2. Get page from web ////////////////////////////////////////////////////////////////////


		try {
			page = new WebInputStream("https://finance.yahoo.com/quote/" + symbol).readAll();
		} 

		catch (NullPointerException e) {
			page = getPage(symbol);
		}


		// 3. Attempt to parse data to ensure proper value /////////////////////////////////////////


		try {
			getPrice(page);
			getVolume(page);
		} 

		// Recall method if error
		catch (NumberFormatException e) {
			page = getPage(symbol);
		}


		// 4. Return page as String ////////////////////////////////////////////////////////////////


		return page;
	}


	/** Parse web page data and return current price value as a double */
	public static double getPrice(String page) {

		StringBuilder data = new StringBuilder();

		// Set index to beginning of price data
		int i = page.indexOf(mPrefixPrice) + mPrefixPrice.length();

		// Build string
		while (i < page.length() && page.charAt(i) != mSufixPrice) {

			data.append(page.charAt(i));
			i++;
		}

		return Double.parseDouble(data.toString());
	}


	/** Parse Yahoo page data and return current volume value as an int */
	public static int getVolume(String page) {

		StringBuilder data = new StringBuilder();

		// Set index to beginning of volume data
		int i = page.indexOf(mPrefixVolume) + mPrefixVolume.length();

		// Find beginning index of Yahoo volume and build as String
		while (i < page.length() && page.charAt(i) != mSufixVolume) {

			data.append(page.charAt(i));
			i++;

		}

		return Integer.parseInt(data.toString());
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Time Method
	////////////////////////////////////////////////////////////////////////////////////////////////


	/** Return current time as YYYYMMDDhhmm format */
	public static String getTime() {
		String time = LocalDateTime.now().toString();

		return 
				time.substring(0,4)   +		// YYYY 
				time.substring(5,7)   +		// MM
				time.substring(8,10)  +		// DD
				time.substring(11,13) +		// hh
				time.substring(14,16);		// mm
	}


	/** Return current time as YYYYMMDDhhmm format */
	public static String getTimeLong() {

		String time = LocalDateTime.now().toString();

		return 
				time.substring(0,4)   +		// YYYY 
				time.substring(5,7)   +		// MM
				time.substring(8,10)  +		// DD
				time.substring(11,13) +		// hh
				time.substring(14,16) +		// mm
				time.substring(17,19);		// ss
	}


	/** @return True if current time is more than 10 seconds after input (last write).  False otherwise */
	public static boolean compareTime(String timeStamp) {

		String currentTime = getTimeLong();

		int time = convertToSeconds(timeStamp);
		int current = convertToSeconds(currentTime);

		if (current - time >= 10) return true;
		else return false;

	}


	/** Convert hours, minutes, and seconds to seconds time */
	public static int convertToSeconds(String timeStamp) {

		int second = second(timeStamp);
		int minute = minute(timeStamp);
		int hour = hour(timeStamp);

		return hour * 3600 + minute * 60 + second;
	}


	/** @return minute value from input time parameter YYYYMMDDhhmmss format */
	public static int second(String time) {
		return Integer.parseInt("" + time.charAt(12) + time.charAt(13));
	}


	/** @return minute value from input time parameter YYYYMMDDhhmm format */
	public static int minute(String time) {
		return Integer.parseInt("" + time.charAt(10) + time.charAt(11));
	}


	/** @return hour value from input time parameter YYYYMMDDhhmm format */
	public static int hour(String time) {
		return Integer.parseInt("" + time.charAt(8) + time.charAt(9));
	}





	/** Return false if beginning of day (starts data collection) */
	public static boolean checkStart() {

		String time = getTime();

		int hour = hour(time);
		int minute = minute(time);

		if (
				( (hour == Variables.HourStart && minute == Variables.MinuteStart) || hour > Variables.HourStart ) && 
				( hour < Variables.HourEnd )

				) return false;

		else return true;
	}


	/** Return false if end of day (ends data collection) */
	public static boolean checkEnd() { 

		String time = getTime();

		int hour = hour(time);
		int minute = minute(time);

		if (hour == Variables.HourEnd && minute > Variables.MinuteEnd || hour > Variables.HourEnd) return false;
		else return true;
	}

}	
