package level0_TechnicalData;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Pattern;

public final class WebInputStream {
    
    private Scanner scanner;
    
    /** Create an input stream from a filename or web page name. */
    public WebInputStream(String s) {
        try {
        	
            // First try to read file from local file system
        	
            File file = new File(s);
            
            if (file.exists()) {
                scanner = new Scanner(file, "UTF-8");
                scanner.useLocale(Locale.US);
                return;
            }
            
            URL url = getClass().getResource(s);    // Try for files included in jar
            if (url == null) { url = new URL(s); }  // or URL from web

            URLConnection site = url.openConnection();

            java.io.InputStream is = site.getInputStream();
            scanner = new Scanner(new BufferedInputStream(is), "UTF-8");
            scanner.useLocale(Locale.US);
        }
        catch (IOException ioe) {
        	// System.out.println("Could not open " + s);
            new WebInputStream(s);
        }
    }
    

    /** @return If file exists */
    public boolean exists()  {
        return scanner != null;
    }
    
    
    /** @return Whether or not input stream has another line */
    public boolean hasNextLine() {
        return scanner.hasNextLine();
    }


    /** Read and return the remainder of the input as a string */
    public String readAll() {
        
    	if (!scanner.hasNextLine()) return "";

        String result = scanner.useDelimiter(Pattern.compile("\\A")).next();
        scanner.useDelimiter(Pattern.compile("\\p{javaWhitespace}+"));
        
        return result;
    }

}
