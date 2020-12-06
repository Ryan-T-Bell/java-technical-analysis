package level0_TechnicalData;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import model.Read;
import model.Variables;
import level0_TechnicalData.WebInputStream;

/**
 * Class used to convert saved data (text file) to usable format.
 * Data is represented as an ArrayList of TechnicalNodes.  Technical nodes
 * store { Symbol, Time Stamp, Open, High, Low, Close, Volume }
 * 
 * @author Ryan Bell
 */
public class TechnicalData {
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	// Instance Variables
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	private String mSymbol;						// Symbol that identifies stock or ETF
	private int mSize;							// Size of list
	private ArrayList<TechnicalNode> mList;		// List of TechnicalNodes containing stock data



	////////////////////////////////////////////////////////////////////////////////////////////////
	// Constructors
	////////////////////////////////////////////////////////////////////////////////////////////////


	/**
	 * Constructor used to create TechnicalData list of daily data
	 * @param symbol Stock or ETF to invest in
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public TechnicalData(String symbol) { 

		try {

			mSymbol = symbol;
			mList = createYahooList(symbol);
			if (mList != null) mSize = mList.size();

		} catch (FileNotFoundException e) {
			System.out.println(symbol + " file not found");
		} catch (IOException e) {
			System.out.println(symbol + " IO Exception not found");
		}	
	}


	/**
	 * Constructor used when ArrayList is already constructed
	 * @param list ArrayList of Technical Nodes
	 */
	public TechnicalData(String symbol, ArrayList<TechnicalNode> list) {
		mSymbol = symbol;
		mList = list;
		
		if (mList != null) mSize = mList.size();
		
	}

	
	////////////////////////////////////////////////////////////////////////////////////////////////
	// Create List from Yahoo End of Day Data
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	private static ArrayList<TechnicalNode> createYahooList(String symbol) throws IOException {
		
		String url = Variables.YAHOO_Prefix + symbol + Variables.YAHOO_Suffix;
		String data = new WebInputStream(url).readAll();
		
		System.out.println(data);
		
		return new ArrayList<TechnicalNode>();		// Remove and replace with ArrayList containing data
	}
	

	////////////////////////////////////////////////////////////////////////////////////////////////
	// Convert list from saved text file to TechnicalData list
	////////////////////////////////////////////////////////////////////////////////////////////////

	
	/**
	 * Method used as 'constructor' to get list from file
	 * @param symbol Stock symbol
	 * @return TechnicalData object
	 */
	public static TechnicalData getFile(String symbol) throws FileNotFoundException, IOException {
		
		return new TechnicalData(symbol, getList(symbol));
		
	}

	
	/**
	 * Method called in constructor to create technical data list
	 * @param symbol Stock or ETF to invest in
	 * @return ArrayList of technical analysis data stored in TechnicalNodes
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static ArrayList<TechnicalNode> getList(String symbol) throws FileNotFoundException, IOException {

		BufferedReader reader = Read.getReader(Variables.ETFDATA+symbol+".txt");

		if (reader == null) {
			System.out.println("File not found");
			return null;
		}

		ArrayList<TechnicalNode> data = new ArrayList<TechnicalNode>();
		TechnicalNode node;

		String line;

		while ((line = reader.readLine()) != null) {		// Read line from file

			node = parseLine(line);							// Create TechnicalNode from file
			if (node != null) data.add(node);				// Add TechnicalNode to list

		}

		reader.close();

		return data;
	}


	/**
	 * Helper method to convert string of data to TechnicalNode
	 * @param symbol Stock or ETF identifier
	 * @param line Line of data being read from BufferedReader
	 * @return New TechnicalNode
	 */
	public static TechnicalNode parseLine(String line) {


		try {
			
			// 1. Add first parameters to array ////////////////////////////////////////////////////////


			String[] parameters = new String[6];

			// Time stamp in YYYYMMDDhhmm format
			parameters[0] = line.substring(0,12);


			// 2. Parse remaining string ///////////////////////////////////////////////////////////////


			int paramsAdded = 1;							// Parameters currently added 
			StringBuilder str = new StringBuilder();		// Builds TechnicalNode parameter from line

			for (int i = 13; i < line.length(); i++) { 		// Set i to 13 to skip time stamp (YYYYMMDDhhmm,)

				// Continue to build StringBuilder
				if (line.charAt(i) != ',')
					str.append(line.charAt(i));

				// Add new parameter to array
				else {
					parameters[paramsAdded] = str.toString();	// Add parameter to array
					str = new StringBuilder();					// Reset parameter being built
					paramsAdded++;								// Increment parameter tracker
				}

			}

			parameters[5] = str.toString();					// Add last parameter (volume) to array

			return new TechnicalNode(parameters);
		}

		catch (StringIndexOutOfBoundsException e) {
			return null;
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Methods to Use in Other Classes
	////////////////////////////////////////////////////////////////////////////////////////////////


	/**
	 * Used to modify technical data nodes by combining values
	 * 
	 * @param data Original TechnicalData list
	 * @param cf Combining factor for determining period to calculate candlesticks for (Period = 5 x cf)
	 * @return TechnicalData object that is same size of input parameter that is "compressed" by cf value
	 */
	public static TechnicalData modifyTechnicalData(TechnicalData data, int cf) {


		// 1. Returns initial technical data input if no modifications required ////////////////////


		if (cf <= 1) return data;


		// 2. Create variables /////////////////////////////////////////////////////////////////////


		boolean first;
		String timeStamp = data.timeStamp(0);
		double open      = data.open(0);
		double high      = data.high(0);
		double low       = data.low(0);
		double close     = data.close(0);
		int volume       = data.volume(0);
		ArrayList<TechnicalNode> list = new ArrayList<TechnicalNode>();


		// 3. Create modified data set /////////////////////////////////////////////////////////////


		for (int i = 0; i < data.size(); i++) {

			// a. Copy technical data for nodes that don't have enough trailing for cf /////////////

			if (i < cf - 1) {

				list.add(data.get(i));

			} 

			// b. Create compressed technical data nodes ///////////////////////////////////////////

			else {

				first = false;	// Reset variable

				for (int j = i - cf + 1; j <= i; j++) {

					if (first) {

						// Update values that can change in "compression range"
						if (high < data.high(j)) high = data.high(j);
						if (low > data.low(j)) low = data.low(j);
						volume = volume + data.volume(j);

					} else {

						// Set initial values
						timeStamp = data.timeStamp(j);
						open      = data.open(j);
						high      = data.high(j);
						low       = data.low(j);
						close     = data.close(j);
						volume    = data.volume(j);

						first = true;
					}
				}
				list.add(new TechnicalNode(timeStamp, open, high, low, close, volume));
			}
		}
		return new TechnicalData(data.mSymbol, list);
	}


	// Return size of TechnicalData list
	public int size() { return mSize; }


	// Print TechnicalData list
	public void print() { for (TechnicalNode node : mList) node.print(); }


	/**
	 * Get specified node by index in list
	 * @param i Index in TechnicalData list
	 * @return Specified node
	 */
	public TechnicalNode get(int index) { 
		if (index >= 0 && index < mList.size()) return mList.get(index);
		else return null;
	}


	/**
	 * Helper method used for cleaner code in indicator methods
	 * @param index Index in TechnicalNode data set
	 * @return Time stamp at provided index
	 */
	public String timeStamp(int index) {
		return this.get(index).getTimeStamp();
	}


	/**
	 * Helper method used for cleaner code in indicator methods
	 * @param index Index in TechnicalNode data set
	 * @return Open value at provided index
	 */
	public double open(int index) {
		return this.get(index).getOpen();
	}


	/**
	 * Helper method used for cleaner code in indicator methods
	 * @param index Index in TechnicalNode data set
	 * @return High value at provided index
	 */
	public double high(int index) {
		return this.get(index).getHigh();
	}


	/**
	 * Helper method used for cleaner code in indicator methods
	 * @param index Index in TechnicalNode data set
	 * @return Low value at provided index
	 */
	public double low(int index) {
		return this.get(index).getLow();
	}


	/**
	 * Helper method used for cleaner code in indicator methods
	 * @param index Index in TechnicalNode data set
	 * @return Close value at provided index
	 */
	public double close(int index) { 
		return this.get(index).getClose();
	}


	/**
	 * Helper method used for cleaner code in indicator methods
	 * @param index Index in TechnicalNode data set
	 * @return Volume at provided index
	 */
	public int volume(int index) {
		return this.get(index).getVolume();
	}


	/**
	 * Retrieve designated TechnicalNode index in data set using time stamp
	 * @param timeStamp Time in YYYYMMDDhhmm format (can be shortened)
	 * @return First index in technical data list that has designated time stamp
	 * 	Will return nodes with partial timeStamp (eg. 201512 return first node in December, 2015)
	 */
	public int getIndexByTimeStamp(String timeStamp) {

		String dataTS = get(0).getTimeStamp();	// Time stamp from TechnicalData

		// Goal: Match partial timeStamp with dataTS
		for (int i = 0; i < size(); i++) {

			// 1. Update data time stamp to next index
			dataTS = get(i).getTimeStamp();

			// 2. Determine if dataTS (all or partial) matches all of timeStamp
			for (int j = 0; j < timeStamp.length() && dataTS.charAt(j) == timeStamp.charAt(j); j++) {

				if (j >= timeStamp.length()-1) return i;	// Match found

			}

		}

		return -1;	// No match in data set 
	}


	public static void main(String[] arge) throws FileNotFoundException, IOException, NullPointerException {

		getFile("IVV").print();
		
	}
}
