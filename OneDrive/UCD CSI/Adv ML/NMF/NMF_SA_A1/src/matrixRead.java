/*
 * Reference API used from http://docs.roguewave.com/imsl/java/5.0.1/api/
 * 
 */

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class matrixRead {
	
	static int maxNo = 161462;
	
	// Number of rows and columns 
	int nRow, nCol;
	//Non zero figures
	int nonZ;
	
	public class matrixReadRow {
		
		//structure of row major matrix from file bbcnews.mtx
		public int term, doc;
		//Frequency of terms in a document
		public double freq;
		
		// vector for calculating tf. 
		public double tFreq;
		
		//vector for calculating idf
		public double inv_Freq;
		
		
		//Fix the matrix. 
		//
		public void set(int i, int j, double k){
			term = i;
			doc = j;
			freq = k;
		}
	}
	matrixReadRow[]s = new matrixReadRow[maxNo];
	
	matrixRead(){
		nRow = 0;
		nCol = 0;
		nonZ = 0;
		
		for(int i = 0; i < maxNo ; i++)
		{
		    s[i] = new matrixReadRow();
		}
	}
	
	// Reading matrix from bbcnews.mtx file
	//the file is in a sparse matrix format
	
	public void read(){
		try{
				@SuppressWarnings("rawtypes")
				Class c = Class.forName("matrixRead");
			
				// returns the ClassLoader object associated with this Class
				ClassLoader cl = c.getClassLoader();
				
				// input stream
				InputStream is = cl.getResourceAsStream("bbcnews.mtx");
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
	        
				//reading matrix terminated by line feed
				String line = br.readLine();
            
				//reading comments from file
				
				boolean comment = true;
				while (comment) {
					line = br.readLine();
					comment = line.startsWith("%");
				}
				
				// second line containing length of terms, doc and freq
	            String[] str = line.split("( )+");
	            nRow = (Integer.valueOf(str[0].trim())).intValue();
	            nCol = (Integer.valueOf(str[1].trim())).intValue();
	            nonZ = (Integer.valueOf(str[2].trim())).intValue();
	            
	            // data section begins
	            int cnt = 0;
	            while (true) {
	                line = br.readLine();
	                if (line == null){
	                	break;
	                }
	                
	                str = line.split(" ");
	                int p = (Integer.valueOf(str[0].trim())).intValue();
	                int q = (Integer.valueOf(str[1].trim())).intValue();
	                double r = (Double.valueOf(str[2].trim())).doubleValue();
	                
	                s[cnt].set(p, q, r);
	                
	                cnt++;
	            }
	            br.close();
		}
		catch(Exception e){
			System.out.println(e);
		}
	}

	//TF: term frequency
	//IDF: Inverse Document frequency
	
	public void tf_idf(){
		//
		//           
		
		//Size of document
		double [] doc_sz = new double[nCol];
		//
		for(int i = 0;i<nCol; i++){
			doc_sz[i] = 0.00;
		}
		
		//frequency of individual terms.
		int [] termfreq = new int[nRow];
		//store IDF values of each term
		double [] idf = new double[nRow];				
		//initialization of termCnt and IDF
		for(int i = 0;i<nRow; i++){
			termfreq[i] = 0;
			idf[i] = 0.00;
		}
		
		//calculating documents with specific ith term in it.
		//
		for(int i = 0; i<nCol; i++){
			//
			for(int j = 0; j<nonZ; j++){
				if(s[j].doc-1 == i){
					doc_sz[i] = doc_sz[i]+s[j].freq;
				}
			}
		}
		
		//calculating terms in specific document
		//
		
		//Calculating number of terms,
		//term frequency
		for(int i = 0; i<nRow; i++){

			for(int j = 0; j<nonZ; j++){
					if(s[j].term-1 == i){
						termfreq[i]++;
					}
			}
			
			//
			if(termfreq[i]!=0){
				idf[i] = Math.log(nCol / termfreq[i]);
			}
		}
		
		
		//calculating tf
		//tf is calculated as the number of times term occur in document.

		for(int i = 0; i<nonZ; i++){
			
			//calculating values from tf
			s[i].tFreq = 0.5+(0.5 * s[i].freq/doc_sz[s[i].doc-1]);
			
			//inversion for idf
			s[i].inv_Freq = s[i].tFreq * idf[s[i].term-1];
		}
	}
}