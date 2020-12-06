package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Read {

	
	////////////////////////////////////////////////////////////////////////////////////////////////
	// Primary Methods for Reading From File
	////////////////////////////////////////////////////////////////////////////////////////////////
	

	/**
	 * Helper method to create get contents of file
	 * @param directory Folder from /Data/ that file is stored in
	 * @param fileName Name of the .txt file in directory to read/process
	 * @return All contents of parameter file as a String
	 */
	public static String readFromFile(String directory) {

		// Create necessary variables and get file path
		String line;
		StringBuilder str = new StringBuilder();
		BufferedReader reader = getReader(directory);

		try {

			// Read all lines from file
			while ((line = reader.readLine()) != null)
				str.append(line);

			// Close reader
			reader.close(); 
		}

		catch (IOException e) {}

		return str.toString();

	}


	/**
	 * Helper method to create Buffered Reader with correct directory path
	 * @param directory Folder from /Data/ that file is stored in
	 * @param fileName Name of the .txt file in directory to read
	 * @return New reader
	 */
	public static BufferedReader getReader(String directory) {


		// Get text file path
		File file = new File(directory);

		try {

			// Create reader
			return new BufferedReader(new InputStreamReader(new FileInputStream(file)));

		} catch (FileNotFoundException e) {

			// File not found
			System.out.println("getReader: " + directory + " Not Found");
			return null;
		}
	}

	
	////////////////////////////////////////////////////////////////////////////////////////////////
	// Methods that Read CSV Text Files and Return Various Types of ArrayLists
	////////////////////////////////////////////////////////////////////////////////////////////////

	
	/** Get ArrayList<String> from comma separated file */
	public static ArrayList<String> getStringArrayList(String directory) {

		String data = readFromFile(directory);
		ArrayList<String> list = new ArrayList<String>();

		int i = 0;

		// Build ArrayList<Double> by iterating through String
		while (i < data.length()) {

			StringBuilder str = new StringBuilder();

			// Build next value (to next comma)
			while (i < data.length() && data.charAt(i) != ',') {
				str.append(data.charAt(i));
				i++;
			}

			i++;
			list.add(str.toString());
		}

		return list;
	}
	
	
	/** Get ArrayList<Integer> from comma separated file */
	public static ArrayList<Integer> getIntegerArrayList(String directory) {

		String data = readFromFile(directory);
		ArrayList<Integer> list = new ArrayList<Integer>();

		int i = 0;

		// Build ArrayList<Double> by iterating through String
		while (i < data.length()) {

			StringBuilder str = new StringBuilder();

			// Build next value (to next comma)
			while (i < data.length() && data.charAt(i) != ',') {
				str.append(data.charAt(i));
				i++;
			}

			i++;
			list.add(Integer.parseInt(str.toString()));
		}

		return list;
	}
	
	/** Get ArrayList<Double> from comma separated file */
	public static ArrayList<Double> getDoubleArrayList(String directory) {

		String data = readFromFile(directory);
		ArrayList<Double> list = new ArrayList<Double>();

		int i = 0;

		// Build ArrayList<Double> by iterating through String
		while (i < data.length()) {

			StringBuilder str = new StringBuilder();

			// Build next value (to next comma)
			while (i < data.length() && data.charAt(i) != ',') {
				str.append(data.charAt(i));
				i++;
			}

			i++;
			list.add(Double.parseDouble(str.toString()));
		}

		return list;
	}
}
