/*
 * Reference API : http://pages.cs.wisc.edu/~cs368-2/JavaTutorial/jdk1.2/api/
 */

import java.io.*;


public class readTerms {
	String [] readTerms; //creates an array to store terms
	
	int cnt; 
	
	
	//method invoked to count the number of terms in file
	public readTerms(int length){
		cnt = length; 
		// stores number of terms in a file. 4058
		readTerms = new String[length];
	}
	
	public void read(){
		
		//suppressing errors occuring due to undeclared datatypes.
		@SuppressWarnings("rawtypes")
		Class c;
		try {
			c = Class.forName("readTerms");
			
			ClassLoader cl = c.getClassLoader();
			// get reference to the object
			
			InputStream is = cl.getResourceAsStream("bbcnews.terms"); // returns input stream of file
			
			//creating a character input stream
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			
			//reading text file terminated by line feed
			readTerms[0] = br.readLine();
			for(int i = 1; i<cnt; i++){
				readTerms[i] = br.readLine();
			}
			
		} catch (Exception e) {
			e.printStackTrace(); //security exception
		}
	}
}
