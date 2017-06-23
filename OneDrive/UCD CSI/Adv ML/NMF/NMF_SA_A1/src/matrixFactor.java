/*
 * COMP 41450 NON NEGATIVE MATRIX FACTORIZATION
 * SARANSH AGARWAL
 * 14203485
 * UCD School of Computer Science & Informatics
 * 
 * References: http://math.nist.gov/javanumerics/jama/doc/
 * http://docs.oracle.com/javase/tutorial/
 * 
 */



import java.util.*;
import java.io.*;

import Jama.Matrix; 
// to utilise basic linear algebra in matrix.

public class matrixFactor {
	
	//declaring object of class
	matrixRead R;
	
	readTerms T;
	
	//declaring matrices used in the corpus for computation.
	//Matrix is datatype in Jama library.
	
	Matrix a, w, h;
	//a-> data matrix
	//w-> basis vectors
	//h-> coefficient matrix
	
	
	Matrix temp;
	//temporary matrix for copy operations
	
	public static void main(String[] args) {
		//creating an object of the class
		//which can be used to access the members of the class
		
		matrixFactor mf = new matrixFactor();
		
		// reading objects from other class.
		mf.R = new matrixRead();
		
		mf.R.read();
		
		
		//apply tf-idf normalisation
		mf.R.tf_idf();
		
		
		//for the terms file.
		//reading terms.
		mf.T = new readTerms(mf.R.nRow);
		mf.T.read();
		
		//creating a matrix from the file
		mf.createMTX(mf.R);
		
		//Non negative matrix factorization.
		//Values of clusters can be varied
		
		for(int k = 2; k<=6; k++){
			System.out.println("\n\t\tFor k = "+k);
			mf.factorise(k);
			//getting the most frequent 10 terms from file.
			mf.topTerms(10);
		}
		
	}
	
	private void factorise(int k){
		initMTX(k);
		
		//multiplying W and H factors
		temp = w.times(h); //using function from Jama library.
		
		
		int cnt = 0;
		while(cnt < 100){
			//update factor h
			h_new(temp, k);
			//update factor W
			w_new(temp, k);
			
			//product of updated factors
			temp = w.times(h);
			
			cnt++;
		}
		// Factorization completed
	}

	public void createMTX(matrixRead R){
		a = new Matrix(R.nRow, R.nCol);
		
		//initialization
		for(int i = 0; i<R.nRow; i++){
			for(int j = 0; j<R.nCol; j++){
				a.set(i, j, 0.0);
			}
		}
		
		//parsing matrix
		for(int i = 0; i<R.nonZ; i++){
			a.set(R.s[i].term-1, R.s[i].doc-1, R.s[i].inv_Freq);
		}
	}
	
	
	private void initMTX(int k){
		
		//creates a matrix w with dimensions as
		//number of rows, value of k
		w = new Matrix(a.getRowDimension(), k);
		
		//
		//creates a matrix h with dimensions as
		//value of k, number of columns
		h = new Matrix(k, a.getColumnDimension());
		
		//using random numbers to fill in matrix
		Random r = new Random();
		
		//randomly initialize factor W
		
		for(int i = 0; i<w.getRowDimension(); i++){
			
			
			for(int j = 0; j<w.getColumnDimension(); j++){
				w.set(i, j, r.nextDouble()); 
				//returns a random number between 0.0 and 1.0
				
			}
		}
		
		//randomly initialize factor H
		for(int i = 0; i<h.getRowDimension(); i++){
			for(int j = 0; j<h.getColumnDimension(); j++){
				h.set(i, j, r.nextDouble());
			}
		}
	}
	
	
	//updates factor H
	private void h_new(Matrix temp, int k) {
		Matrix wt = w.transpose();
		Matrix wta = wt.times(a);
		Matrix wtwh = wt.times(temp);
		double h2;
		
		//dimensions of h are c,j.
		
		for(int j = 0; j<temp.getColumnDimension(); j++){
			for(int c = 0; c<k ;c++){
				h2 = h.get(c, j) * wta.get(c, j) / wtwh.get(c, j);
				//matrix multiplication by element
				h.set(c, j, h2);
			}
		}
	}
	
	//updates factor W
	private void w_new(Matrix temp, int k) {
		Matrix ht = h.transpose();
		Matrix aht = a.times(ht);
		Matrix whht = temp.times(ht);
		double w2;
		
		for(int i = 0; i<temp.getColumnDimension(); i++){
			for(int c = 0; c<k ;c++){
				w2 = w.get(i, c) * aht.get(i, c) / whht.get(i, c);
				w.set(i, c, w2);
			}
		}
	}

	//creates a vector of top terms in the corpus.
	private void topTerms(int num){
		
		//store terms in an array 
		double [][]m = w.getArray();
		
		double[]col = new double [w.getRowDimension()];
		
		//calculating index of term.
		
		int index = w.getRowDimension() - num;
		
		
		for(int i = 0; i<w.getColumnDimension(); i++)
		{
			for(int j = 0; j<w.getRowDimension(); j++){
				col[j] = m[j][i]; 
			}
			
			
			
			Arrays.sort(col);
			
			System.out.println("Cluster"+(i+1)+" : ");
			for(int p = 0; p<num; p++){
				for(int q = 0; q<w.getRowDimension(); q++){
					if(col[index+p] == w.get(q, i)){
						System.out.println(T.readTerms[q]);
					}
				}
			}
		}
	}
}
