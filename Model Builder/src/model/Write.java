package model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;


/**
 * Class used to read and write data from text files.
 * Store ETF Data, neural network level training data, and model version data.
 * 
 * @author Ryan Bell
 */
public class Write {


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Create File Directory
	////////////////////////////////////////////////////////////////////////////////////////////////


	/** Create directory folders for parameter */
	public static void createFolder(String directory) {

		(new File(directory)).mkdirs();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Primary Methods for Writing Data to a Text File
	////////////////////////////////////////////////////////////////////////////////////////////////


	/**
	 * Write String to text file
	 * @param directory File directory from Data/ folder that contains file
	 * @param indicatorName Name of indicator (e.g. rsi0)
	 * @param data String to write to data file
	 * @param erase True = Erase File, False = Append Data w/o Erasing
	 * @param desktop True = write to Desktop/Data/, False = write to Model Builder/Data/
	 */
	public static void writeToFile(String directory, String data, boolean erase) {

		try {
			
			// Get file pointer.  Erase if parameter requires

			File file = eraseFile(directory, erase);

			// Write to File

			try{

				BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true));
				bufferedWriter.write(data);
				bufferedWriter.close();

			} catch(IOException e) {}

			// if (erase) System.out.println("Overwrite: " + directory);
			// else System.out.println("Append: " + directory);
		
		} catch (NullPointerException e) {
			createFolder(directory);
		}
	}
	

	/**
	 * Erase the text file in TechnicalAnalysis data file to allow new calculations (or create blank one)
	 * @param directory String in "/Folder/SubFolder/fileName.txt"
	 * @param erase True = Erase File, False = Just Return Pointer
	 * @return Pointer to file
	 */
	public static File eraseFile(String directory, boolean erase) {

		// Create directory and new file
		File file = new File(directory);

		if (erase || file == null) {

			PrintWriter writer = null;

			try {
				writer = new PrintWriter(file);
			} catch (FileNotFoundException e) { }

			writer.print("");
			writer.close();

		}

		return file;

	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	// Methods that Write ArrayLists as CSV Text Files
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * Convert ArrayList<Integer> to text file
	 * @param directory File directory from Data/ folder that contains file
	 * @param indicatorName Name of indicator (e.g. rsi0)
	 * @param data ArrayList<Integer> data set to convert
	 * @param erase True = Erase File, False = Append Data w/o Erasing
	 * @param desktop True = write to Desktop/Data/, False = write to Model Builder/Data/
	 */
	public static void integerArrayList(String directory, ArrayList<Integer> data, boolean erase) {

		// Get file pointer.  Erase if parameter requires

		File file = eraseFile(directory, erase);

		// Write to File

		try{

			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true));

			// Convert ArrayList<Double> to comma separated file
			for (Integer value : data) bufferedWriter.write("" + value + ",\r\n");
			bufferedWriter.close();

		} catch(IOException e) {}

		// if (erase) System.out.println("Overwrite: " + directory);
		// else System.out.println("Append: " + directory);
	}
	
	
	/**
	 * Convert ArrayList<Double> to text file
	 * @param directory File directory from Data/ folder that contains file
	 * @param indicatorName Name of indicator (e.g. rsi0)
	 * @param data ArrayList<Double> data set to convert
	 * @param erase True = Erase File, False = Append Data w/o Erasing
	 * @param desktop True = write to Desktop/Data/, False = write to Model Builder/Data/
	 */
	public static void doubleArrayList(String directory, ArrayList<Double> data, boolean erase) {

		// Get file pointer.  Erase if parameter requires

		File file = eraseFile(directory, erase);

		// Write to File

		try{

			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true));

			// Convert ArrayList<Double> to comma separated file
			for (Double value : data) bufferedWriter.write("" + value + ",\r\n");
			bufferedWriter.close();

		} catch(IOException e) {}

		// if (erase) System.out.println("Overwrite: " + directory);
		// else System.out.println("Append: " + directory);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Write 2D ArrayList to Multiple Files (Array)
	////////////////////////////////////////////////////////////////////////////////////////////////


	/**
	 * Specialized method to process candlestick weights 
	 * Convert ArrayList<Double[]> to text file
	 * @param directory Symbol of ETF to invest in (determines directory to store in)
	 * @param fileName Name of indicator (e.g. rsi0)
	 * @param data ArrayList<Double[]> data set to convert
	 * @param erase True = Erase File, False = Append Data w/o Erasing
	 * @param desktop True = write to Desktop/Data/, False = write to Model Builder/Data/
	 */
	public static void double2DimArrayList(String directory, ArrayList<Double[]> data, boolean erase) {

		// Convert Multidimensional array to single ArrayList<Double> and write to file
		for (int i = 0; i < data.get(0).length; i++) {

			ArrayList<Double> list = new ArrayList<Double>();			// Create list to write to file

			for (int j = 0; j < data.size(); j++)						// Add all [i] values to list
				list.add(data.get(j)[i]);

			// Create String without ".txt" for proper version
			StringBuilder str = new StringBuilder();
			for (int k = 0; k < directory.length() && directory.charAt(k) != '.'; k++)
				str.append(directory.charAt(k));

			// Write list to file
			doubleArrayList(str.toString()+"v"+i+".txt", list, erase);
		}

	}


	////////////////////////////////////////////////////////////////////////////////////////////////	
	// Testing
	////////////////////////////////////////////////////////////////////////////////////////////////


	public static void main(String[] args) {

		
	}
}
