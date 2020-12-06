package level0_TechnicalData;

/**
 * Node that stores info for 1 time period of technical analysis data and pulls the current
 * technical analysis data for a given investment (using its symbol)
 * 
 * @author Ryan Bell
 */
public class TechnicalNode {


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Class Variables
	////////////////////////////////////////////////////////////////////////////////////////////////


	private String mTimeStamp;	// Date and time of record
	private double mOpen;	    // Price investment opens at for beginning of the time period
	private double mHigh;		// Highest price of the investment for the day/Time period
	private double mLow;		// Lowest price of the investment for the day/Time period
	private double mClose;		// Price investment closes at for end of the time period
	private int mVolume;		// Total amount of trades made during period


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Node Constructor
	////////////////////////////////////////////////////////////////////////////////////////////////


	/** General constructor used to set all values */
	public TechnicalNode(String timeStamp, double open, double high, double low, double close, int volume) {
		mTimeStamp = timeStamp;
		mOpen      = open;
		mHigh      = high;
		mLow       = low;
		mClose     = close;
		mVolume    = volume;
	}
	

	/** Empty constructor for Pull ETF Data class */
	public TechnicalNode() { }
	
	public TechnicalNode(String[] parameters) {
		
		// DELETE PRINT LINE BELOW
		//for (int i = 0; i < 6; i++) System.out.println("Param(" + parameters[0] + ", " + i + ")" + parameters[i]);
		
		mTimeStamp = parameters[0];
		mOpen      = Double.parseDouble(parameters[1]);
		mHigh      = Double.parseDouble(parameters[2]);
		mLow       = Double.parseDouble(parameters[3]);
		mClose     = Double.parseDouble(parameters[4]);
		mVolume    = Integer.parseInt(parameters[5]);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Get and Set Methods
	////////////////////////////////////////////////////////////////////////////////////////////////


	// Get Methods
	public String getTimeStamp() { return mTimeStamp; }
	public double getOpen()      { return mOpen;      }
	public double getClose()     { return mClose;     }
	public double getHigh()      { return mHigh;      }
	public double getLow()       { return mLow;       }
	public int getVolume()    	 { return mVolume;    }
	
	public String getDate()      { return mTimeStamp.substring(0, 8);   }
	public String getTime()      { return mTimeStamp.substring(8, 12);  }
	
	public int getYear()         { return Integer.parseInt(mTimeStamp.substring(0,4));    }
	public int getMonth()        { return Integer.parseInt(mTimeStamp.substring(4, 6));   }
	public int getDay()          { return Integer.parseInt(mTimeStamp.substring(6, 8));   }
	public int getHour()         { return Integer.parseInt(mTimeStamp.substring(8, 10));  }
	public int getMinute()       { return Integer.parseInt(mTimeStamp.substring(10, 12)); }
	
	// Set Methods
	public void setTimeStamp(String timeStamp) { mTimeStamp = timeStamp; }
	public void setOpen(double open) 	       { mOpen = open;    		 }
	public void setHigh(double high) 	 	   { mHigh = high;     		 }
	public void setLow(double low) 		 	   { mLow = low;      		 }
	public void setClose(double close)         { mClose = close;   		 }
	public void setVolume(int volume) 	  	   { mVolume = volume;		 }


	//////////////////////////////////////////////////////////////////////////////
	// TechnicalNode Helper Methods
	//////////////////////////////////////////////////////////////////////////////

	
	/** Return TechnicalNode as String (as format stored in ETFData text files) */
	public String toString() {
		
		return 
				mTimeStamp + "," +
				mOpen + "," +
				mHigh + "," +
				mLow + "," +
				mClose + "," +
				mVolume;
		
	}

	// Change format of time stamp to MM/DD/YYYY (hh:mm)
	// THIS METHOD IS NOT USED AS OF 05 NOV 2016
	/*private static String formatTimeStamp(String timeStamp) {

		StringBuilder str = new StringBuilder();
		
		str.append(timeStamp.substring(0,10));
		str.append(" (");
		str.append(timeStamp.substring(11));
		str.append(")");

		return str.toString();
	}
	*/
	
	// Convert time stamp to YYYYMMDDhhmm format
	/*
	public String compressTimeStamp() { 
		String timeStamp = getTimeStamp();
		return "" + 
				timeStamp.substring(6, 10) + timeStamp.substring(0, 2) + timeStamp.substring(3, 5) + 
				timeStamp.substring(12, 14) + timeStamp.substring(15, 17); 
	}
	*/
	// Print all values in node
	public void print() {
		if (this != null) 
			System.out.println(
							mTimeStamp + ", " +
							mOpen      + ", " +
							mHigh      + ", " +
							mLow       + ", " +
							mClose     + ", " +
							mVolume);
	}



	//////////////////////////////////////////////////////////////////////////////
	// Level1-IndicatorScores: Candlestick Methods
	//////////////////////////////////////////////////////////////////////////////


	public boolean bullish()     { return mClose > mOpen; }
	public boolean bearish()     { return mClose < mOpen; }
	public double  body()        { return Math.abs(mClose - mOpen); }
	public double  upShadow()    { return mHigh - Math.max(mClose, mOpen); }
	public double  lowShadow()   { return Math.min(mClose,  mOpen) - mLow; }
	public double  height()      { return mHigh - mLow; }
	public double  bodyPercent() { return body() / height(); }


	//////////////////////////////////////////////////////////////////////////////
	// Testing
	//////////////////////////////////////////////////////////////////////////////

	
	public static void main (String[] args) {
		TechnicalNode node = new TechnicalNode("200810192930", 209.72, 209.75, 209.64, 209.66, 13152);
		//System.out.println(node);
		System.out.println("200810192930");
		System.out.println("Year: " + node.getYear());
		System.out.println("Month: " + node.getMonth());
		System.out.println("Day: " + node.getDay());
		System.out.println("Hour: " + node.getHour());
		System.out.println("Minute: " + node.getMinute());
		System.out.println("Date: " + node.getDate());
		System.out.println("Time: " + node.getTime());
	}
}
