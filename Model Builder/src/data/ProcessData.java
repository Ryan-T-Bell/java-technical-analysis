package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import level0_TechnicalData.TechnicalData;
import model.Read;
import model.Variables;
import model.Write;

/**
 * Class used to "clean" saved 5-minute data.txt files by
 * removing all values outside of 0930 - 1600 trading day
 * and creating artificial data for missing intra-day trading
 * values.  Set for Eastern Time Zone.
 * 
 * @author Ryan Bell
 */
public class ProcessData {



	////////////////////////////////////////////////////////////////////////////////////////////////
	// Wrapper Methods for Class that Processes All Stored Data
	////////////////////////////////////////////////////////////////////////////////////////////////


	/**
	 * Wrapper method to process all data files
	 * @param symbols All data file names (not full directory addresses)
	 * @throws IOException
	 */
	public static void processDataFiles() throws IOException {

		int updated = 0;
		String[] symbols = Variables.SYMBOLS;

		for (String symbol : symbols) {

			System.out.println("Parsing " + symbol);

			// Remove all technical data outside of trading range
			// Build missing data (if missing < 10 periods)
			if (cleanData(symbol)) {
				updated++;
				System.out.println(symbol + " data parsed");
			}
		}

		// Get rid of extra data not shared across range
		// matchAllData();

		System.out.println(updated + " File(s) updated");

		// Ensure data size is the same across all time stamps
		checkDataSizes("2009", "2016");

	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Primary Method that Removes Bad Data
	////////////////////////////////////////////////////////////////////////////////////////////////


	/**
	 * Removes all technical data with values outside of 09:30 - 16:00 time range
	 * and fills in gaps of missing data by fixed increase between known points
	 * @param symbol File name that data is being stored under
	 * @throws IOException 
	 */
	private static boolean cleanData(String symbol) throws IOException {


		// 1. Create data input stream (reader) and create output file (tempFile) //////////////////


		BufferedReader reader;												// Reader that processes old text file
		PrintWriter tempPrintWriter = null;									// Temporary PrintWriter that will replace old data.txt

		// Create temporary file to write data to
		String fileDir = Variables.ETFDATA;
		File tempFile = new File(fileDir + "Temp.txt");

		try {

			reader          = Read.getReader(fileDir + symbol + ".txt"); 	// Create reader to get data from file
			tempPrintWriter = new PrintWriter(tempFile);					// Create temporary text file

		}  catch (FileNotFoundException e) {
			System.out.println(symbol + ": file not replaced");
			return false;
		}


		// 2. Process and write all input data to temporary file ///////////////////////////////////


		writeDataToTempFile(reader, tempPrintWriter);


		// 3. Replace old file with temporary one and "cleanup" ////////////////////////////////////

		// Cleanup
		reader.close();
		tempPrintWriter.close();

		// Replace old file with temporary one
		new File(fileDir + symbol + ".txt").delete();				// Delete old file
		tempFile.renameTo(new File(fileDir + symbol + ".txt"));		// Rename temporary file (with new data) to deleted directory

		// File correctly replaced
		return true;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Helper Methods for cleanData()
	////////////////////////////////////////////////////////////////////////////////////////////////


	private static void writeDataToTempFile(BufferedReader reader, PrintWriter file) throws IOException {

		String prevLine = reader.readLine();
		boolean firstAdded = false;


		// 1. Add first line to new data file //////////////////////////////////////////////////////


		while (!firstAdded && prevLine != null) {

			// First input is within time parameters
			if (checkTime(prevLine)) {

				file.println(prevLine);			// Write first line
				firstAdded = true;				// Exit loop
			} 

			// First input is not within time parameters
			else prevLine = reader.readLine();
		}


		// 2. Write the remaining data to temporary file ///////////////////////////////////////////


		String line;
		String[] missingData;

		while ((line = reader.readLine()) != null) {			// Read next line in original file

			if (checkTime(line)) {								// Check if data is within parameters

				// Fill in missing data between previous and current line //////////////////////////

				missingData = missingData(prevLine, line);

				// Add missing data lines to file
				if (missingData != null) {
					for (String s : missingData) {
						file.println(s);
					}
				}

				// Write current line to file
				file.println(line);	

				// Set new line pointer for missing data
				prevLine = line;

			}
		}
	}


	/**
	 * Method to create artificial data to account for missing information
	 * Limits to 10 missing lines to account for early closing days (around 1300)
	 * @param prevLine All TA data for previously entered node
	 * @param currLine Next 
	 * @return String array containing all data (prevLine, currLine) where values are
	 *         changed at a fixed interval in order to transition from previous to current state
	 *         Example: Input  = (5, 9) with 3 missing nodes (fixed interval = 1)
	 *                  Output = {6, 7, 8}
	 */
	private static String[] missingData(String prevLine, String currLine) {


		int numLines = getNumLines(prevLine, currLine);
		if (numLines <= 0 || numLines > 15) return null;	// Limits to 15 missing lines to account for early closing days (around 1300)

		String[] missingData = new String[numLines];

		for (int i = 0; i < numLines; i++)
			missingData[i] = buildNewLine(prevLine, currLine, i, numLines);

		return missingData;
	}


	/** @return Date in YYYYMMDD format */
	private static String getDate(String line) {
		return line.substring(0, 8);
	}


	/**
	 * Helper method to pull time values
	 * @param line Technical data from text file
	 * @return Hour value from time stamp
	 */
	private static int getHour(String line) {
		return Integer.parseInt("" + line.charAt(8) + line.charAt(9));
	}


	/**
	 * Helper method to pull time values
	 * @param line Technical data from text file
	 * @return Minute value from time stamp
	 */
	private static int getMinute(String line) {
		return Integer.parseInt("" + line.charAt(10) + line.charAt(11));
	}


	/**
	 * Helper method to check if time is within 09:30 - 16:00 range
	 * @param line Technical data from text file
	 * @param index Current location in data string
	 * @return If time stamp is within range
	 */
	private static boolean checkTime(String line) {


		try {

			boolean inRange = false;

			int hour   = getHour(line);
			int minute = getMinute(line);

			// Check if hours in correct range
			if (hour > Variables.HourStart - 1 && hour < Variables.HourEnd + 1) inRange = true;

			// Check minutes
			if (inRange && hour ==  Variables.HourStart && minute < Variables.MinuteStart) inRange = false;
			if (inRange && hour == Variables.HourEnd && minute > Variables.MinuteEnd) inRange = false;

			return inRange;

		} 

		// Catch if null/bad input
		catch (StringIndexOutOfBoundsException e) {
			return false;
		}
	}


	/**
	 * Helper method to get the number of lines of data missing between last entry and current one
	 * @param prevLine Last data entry
	 * @param currLine Current data entry
	 * @return Number of data entries missing
	 */
	private static int getNumLines(String prevLine, String currLine) {

		int numLines = -1;

		String date = getDate(prevLine);
		int hour = getHour(prevLine);
		int minute  = getMinute(prevLine);

		String targetDate = getDate(currLine);
		int targetHour = getHour(currLine);
		int targetMinute  = getMinute(currLine);

		// Continue adding line count if 
		while(!date.equals(targetDate) || hour != targetHour || minute < targetMinute) {

			// Increment of 5 minutes
			minute = minute + 5;

			// Increment hour if necessary
			if (minute >= 60) {
				minute = 0;
				hour++;
			}

			// Increment day if necessary (sets new date)
			if (hour >= Variables.HourEnd && minute > Variables.MinuteEnd) {
				hour = 9;
				minute = 30;
				date = getDate(currLine);
			}

			numLines++;
		}

		return numLines;

	}


	/**
	 * Helper method to get open, high, low, close, or volume values from String of data
	 * 0 = Timestamp
	 * 1 = Open
	 * 2 = High
	 * 3 = Low
	 * 4 = Close
	 * 5 = Volume
	 * @param line String data in format: [YYYYMMDDhhmm],[Open],[High],[Low],[Close],[Volume]
	 * @return 
	 */
	private static String getParameter(String line, int parameter) {

		int i = 0;
		int commaCount = 0;

		// Cycle index to beginning of desired parameter 
		while (commaCount < parameter && i <= line.length()) {
			if (line.charAt(i) == ',') commaCount++;
			i++;	
		}

		// Get parameter
		StringBuilder p = new StringBuilder();

		while (i < line.length() && line.charAt(i) != ',') {
			p.append(line.charAt(i));
			i++;
		}

		return p.toString();
	}


	/**
	 * Helper method to return the average, fixed difference between the previous and current data entries.
	 * Will return difference for open (1), high (2), low (3), and close (4) in stated order
	 * @param prevLine Last data entered
	 * @param currLine Current data to be entered
	 * @param parameter Parameter to return interval for
	 * @param numLines Number of data nodes that are being created (for calculating average)
	 * @return Averages between previous and current line data
	 */
	private static double[] getIntervals(String prevLine, String currLine, int numLines) {

		double open   = (Double.parseDouble(getParameter(currLine, 1)) - Double.parseDouble(getParameter(prevLine, 1))) / (numLines+1);
		double high   = (Double.parseDouble(getParameter(currLine, 2)) - Double.parseDouble(getParameter(prevLine, 2))) / (numLines+1);
		double low    = (Double.parseDouble(getParameter(currLine, 3)) - Double.parseDouble(getParameter(prevLine, 3))) / (numLines+1);
		double close  = (Double.parseDouble(getParameter(currLine, 4)) - Double.parseDouble(getParameter(prevLine, 4))) / (numLines+1);
		double volume = (Double.parseDouble(getParameter(currLine, 5)) - Double.parseDouble(getParameter(prevLine, 5))) / (numLines+1);

		// -1.0 at front to keep technical data index consistent (1 = open, 2 = high, etc.)
		double[] intervals = {-1.0, open, high, low, close, volume};
		return intervals;
	}


	/**
	 * Helper method to build a new line of missing data 
	 * String data in format: [YYYYMMDDhhmm],[Open],[High],[Low],[Close],[Volume]
	 * @param prevLine Last data entered
	 * @param currLine Current data to be entered
	 * @param i Index from loop used to multiply with interval factor
	 * @param numLines Total number of missing data lines between previous and current
	 * @return New line of missing data between previous and 
	 */
	private static String buildNewLine(String prevLine, String currLine, int i, int numLines) {


		// 1. Time Stamp //////////////////////////////////////////////////////////////


		String date = getDate(prevLine);
		int hour = getHour(prevLine);
		int min  = getMinute(prevLine);

		for (int j = 0; j <= i; j++) {

			// Increment of 5 minutes
			min = min + 5;

			// Increment hour if necessary
			if (min >= 60) {
				min = 0;
				hour++;
			}

			// Increment day if necessary (sets new date)
			if (hour >= Variables.HourEnd && min > Variables.MinuteEnd) {
				hour = 9;
				min = 30;
				date = getDate(currLine);	// Set to following day's date
			}

		}

		// Convert single digit minute or hour to double digit (append 0 prefix)

		String minStr = "" + min;
		if (minStr.length() == 1) minStr = "0" + minStr;

		String hourStr = "" + hour;
		if (hourStr.length() == 1) hourStr = "0" + hourStr;

		String timeStamp = date + hourStr + "" + minStr;


		// 2. Calculate line values ////////////////////////////////////////////////////////////////


		double[] intervals = getIntervals(prevLine, currLine, numLines);

		double open  = Double.parseDouble(getParameter(prevLine, 1)) + (intervals[1] * (i+1));
		double high  = Double.parseDouble(getParameter(prevLine, 2)) + (intervals[2] * (i+1));
		double low   = Double.parseDouble(getParameter(prevLine, 3)) + (intervals[3] * (i+1));
		double close = Double.parseDouble(getParameter(prevLine, 4)) + (intervals[4] * (i+1));
		int volume   = Integer.parseInt(getParameter(prevLine, 5)) + ((int) intervals[5] * (i+1));

		// Round values to 5 decimal places (volume to 0)
		open   = Math.round((open * 100000.0)) / 100000.0;
		high   = Math.round((high * 100000.0)) / 100000.0;
		low    = Math.round((low * 100000.0)) / 100000.0;
		close  = Math.round((close * 100000.0)) / 100000.0;
		volume = Math.round(volume);


		// 3. Build string /////////////////////////////////////////////////////////////////////////


		return timeStamp + "," + open + "," + high + "," + low + "," + close + "," + volume;

	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Methods that Ensure All ETF Data Lists Have Same Time-Stamp Sets
	////////////////////////////////////////////////////////////////////////////////////////////////


	/** Remove all time-stamped data that is not present across all ETF Data sets */
	@SuppressWarnings("unused")
	private static void matchAllData() {


		// 1. Create List of Time Stamps in Final Data Set /////////////////////////////////////////


		ArrayList<Long> timestampList = getTimeStampList();
		System.out.println("MatchData: timestampList Complete");
		
		// 2. Remove All Time Stamped Data Not in timestampList ArrayList //////////////////////////


		matchTimeStamps(timestampList);

	}
	

	/** Return ArrayList<String> of time stamps that are in every ETF data file */
	private static ArrayList<Long> getTimeStampList() {


		// 1. Create First List and Necessary Variables ////////////////////////////////////////////


		ArrayList<Long> tsList = new ArrayList<Long>();		
		ArrayList<Long> tempList = new ArrayList<Long>();		
		TechnicalData data = new TechnicalData(Variables.SYMBOLS[0]);

		for (int i = 0; i < data.size(); i++) tsList.add(longTimeStamp(i, data));


		// 2. Cycle Through All Other TechnicalData Lists and Remove All Time Stamps Not In Common /


		for (String symbol : Variables.SYMBOLS) {

			if (!symbol.equals(Variables.SYMBOLS[0])) {

				// Build temporary ArrayList
				data = new TechnicalData(symbol);
				for (int i = 0; i < data.size(); i++) tempList.add(longTimeStamp(i, data));

				System.out.println(symbol + ": joinList");
				
				// If temporary ArrayList does not contain 
				tsList = joinList(tsList, tempList);

			}
		}

		return tsList;
	}


	/** Return list of values that are in both lists */
	private static ArrayList<Long> joinList(ArrayList<Long> list0, ArrayList<Long> list1) {

		int i0 = 0;
		int i1 = 0;

		ArrayList<Long> joinList = new ArrayList<Long>();

		// Continue until list0 or list1 are out of values to combine
		while (i0 < list0.size() && i1 < list1.size()) {
			
			// Cycle list0 and list1 indexes to equal or greater values
			while (i0 < list0.size() && i1 < list1.size() && (long) list0.get(i0) != (long) list1.get(i1)) {
				
				while (i0 < list0.size() && i1 < list1.size() && (long) list0.get(i0) < (long) list1.get(i1)) i0++;
				while (i0 < list0.size() && i1 < list1.size() && (long) list0.get(i0) > (long) list1.get(i1)) i1++;
				
			}

			
			// If list0 and list1 match, add to combined list and increment indexes
			if (i0 < list0.size() && i1 < list1.size() && (long) list0.get(i0) == (long) list1.get(i1)) {
				joinList.add(list0.get(i0));
				i0++;
				i1++;
			}
			
		}

		return joinList;
	}

	
	/** Overwrite all ETF data file to match time input time stamps */
	private static void matchTimeStamps(ArrayList<Long> tsList) {

		for (String symbol : Variables.SYMBOLS) {
		
			System.out.println(symbol + ": Match Time Stamp Begin");
			
			int index = 0;
			int dataIndex = 0;
			TechnicalData data = new TechnicalData(symbol);
			StringBuilder str = new StringBuilder();
			
			// 1. Create StringBuilder that stores all matching data ///////////////////////////////
			
			while (index < tsList.size() && dataIndex < data.size()) {
				
				// Search through TechnicalData list until tsList.get(index) matches
				while (dataIndex < data.size() && (long) longTimeStamp(dataIndex, data) != (long) tsList.get(index)) dataIndex++;
				
				// Append to file
				if (data.get(dataIndex) != null && data.get(dataIndex).toString() != null && dataIndex < data.size()) 
					str.append(data.get(dataIndex).toString() + "\r\n");
				
				// Increment tsList index
				index++;
			}
			
			
			// 2. Write all data to file ///////////////////////////////////////////////////////////
			
			
			Write.writeToFile(Variables.ETFDATA + symbol+".txt", str.toString(), true);
			
			
		}
	}

	
	/** Return Time Stamp in TechnicalData as Integer */
	private static long longTimeStamp(int index, TechnicalData data) {
		
		return Long.parseLong(data.timeStamp(index));
		
	}
	

	////////////////////////////////////////////////////////////////////////////////////////////////
	// Convert MM/DD/YYYY,hhmm to YYYYMMDDhhmm Format
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Method used to convert all date data from DDMMYYYY, hhmm to YYYYMMDDhhmm 
	 * Does not delete old file and rename, so just do it manually after.
	 * Don't know why it doesn't work like the cleanData method...
	 * @param symbol ETF File to convert data in
	 * @throws IOException If file doesn't exist will throw error
	 */
	/*
	private static void compressTimeStamps(String symbol) throws IOException {

		// Directory to edit
		String directory = System.getProperty("user.dir") + "/ETFData/";

		// Create object that reads lines from file
		BufferedReader reader = ReadWriteFile.getReader(directory + symbol + ".txt");

		// Create temporary file to write to
		File tempFile = new File(directory + "Temp" + ".txt");
		PrintWriter tempPrintWriter = new PrintWriter(tempFile);


		// Process text file to convert timestamp from MM/DD/YYYY,hhmm, format to YYYYMMDDhhmm,
		String line;

		// Read next line in original file
		while ((line = reader.readLine()) != null) {

			StringBuilder str = new StringBuilder();

			str.append(line.substring(6, 10));	// Year
			str.append(line.substring(0, 2));	// Month
			str.append(line.substring(3, 5));	// Day
			str.append(line.substring(11, 13));	// Hour
			str.append(line.substring(14,17));	// Minute
			str.append(line.substring(17));		// Remainder of line

			// Write line to file
			tempPrintWriter.println(str.toString());

		}


		// Replace old file with temporary one
		new File(directory + symbol + ".txt").delete();				// Delete old file
		tempFile.renameTo(new File(directory + symbol + ".txt"));	// Rename temporary file (with new data) to deleted directory

		// Cleanup
		reader.close();
		tempPrintWriter.close();
	}
	 */

	////////////////////////////////////////////////////////////////////////////////////////////////
	// Testing
	////////////////////////////////////////////////////////////////////////////////////////////////

	
	/** Print error message if data sets do not have same entry number */
	private static void checkDataSizes(String start, String end) {

		String[] symbols = Variables.SYMBOLS;
		int range = getTimeStampRange(symbols[0], start, end);

		for (String symbol : symbols) {
			if (getTimeStampRange(symbol, start, end) != range) {
				System.err.println(symbol + " does not match range");
		}
			System.out.println(symbol + ": " +getTimeStampRange(symbol, start, end));
		}
		
		System.out.println("Check Complete");
	}


	/** Return number of entries between start and end date */
	private static int getTimeStampRange(String symbol, String start, String end) {

		TechnicalData data = new TechnicalData(symbol);

		int startIndex = data.getIndexByTimeStamp(start);
		int endIndex = data.getIndexByTimeStamp(end);

		if (startIndex > 0 && endIndex > 0) return endIndex - startIndex;
		else return -1;

	}

	public static void main(String[] args) throws IOException {
		// processDataFiles();
		checkDataSizes("2009", "2016");
	}

}
