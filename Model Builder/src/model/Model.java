package model;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Runs model version (stored in Dropbox current model file) on live data
 * 
 * @author Ryan Bell
 */
public class Model {					

	
	
	
	
	/** Method used to get user name of computer */
	public static String getUser() {
		
		int i = 1;
		String dir = System.getProperty("user.dir");
		StringBuilder user = new StringBuilder();

		// Increases counter until reaches 'y' in user name (Ryan)
		while (i < dir.length() && dir.charAt(i+1) != 'y') i++;
		
		while (i < dir.length() && dir.charAt(i) != '/' && dir.charAt(i) != '\\') {
			user.append(dir.charAt(i));
			i++;
		}
		
		return user.toString();
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	// Important Class Variables.  Many Used in Multiple Classes
	////////////////////////////////////////////////////////////////////////////////////////////////


	


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Run Final Model
	////////////////////////////////////////////////////////////////////////////////////////////////



	////////////////////////////////////////////////////////////////////////////////////////////////
	// General Helper Methods Used Throughout Project
	////////////////////////////////////////////////////////////////////////////////////////////////


	/** Print current time to console */
	public static void printTime() {

		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		System.out.println(dateFormat.format(date));

	}


	/**
	 * Round numbers in order to calculate with full money value and save space on data storage
	 * @param value Value to round
	 * @param decimal Decimal places to round to
	 * @return Rounded value
	 */
	public static double round(double value, int decimal) {
		double x = 1.0;
		for (int i = 0; i < decimal; i++) x = x*10.0;
		return ((int) (value*x)) / x;
	}

	
	////////////////////////////////////////////////////////////////////////////////////////////////
	// Create Directories 
	////////////////////////////////////////////////////////////////////////////////////////////////


	public static void main(String[] args) {

		// System.out.println("User: " + getUser());
	}

}
