import java.util.*;
import java.io.*;

// Apache POI library for reading xlsx files 
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Main {
	public static void main ( String args []) throws IOException {
		boolean verbose = false; 
		FileInputStream fileInputStream;
		if( args.length == 3 || args.length == 4){
			try {
				fileInputStream = new FileInputStream (args[0]);
				if ( args.length == 4 && args[3].equals("-v")) verbose = true;
			}
			catch ( FileNotFoundException e){
				System.out.println(e);
				return;
			}
		}
		else {
			System.out.println("lacking/exceeding number of arguments");
			return;
		}

		final int TRAINING_COUNT = Integer.parseInt(args[1]); // size of training set 
		final int TEST_COUNT = Integer.parseInt(args[2]); // size of test set 
		final int DATASET_COUNT = 1000;
		final int NUM_OF_PREDICTIVE_ATTRIBUTES = 5; // a1, a2 ... a5
		final int PREDICTIVE_ATT_INDEX = NUM_OF_PREDICTIVE_ATTRIBUTES + 1;
		final int PREDICTIVE_ATT_VALUES = 4; // predictive attributes have values ranging from 1 to 4
		final int CLASSIFICATION_ATT_VALUES = 3; // classification attributes have values ranging from 1 to 3

		// create an XSSF Workbook object for XLSX Excel File
		XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
		XSSFSheet sheet = workbook.getSheetAt(0);

		// we iterate on rows
		Iterator<Row> rowIt = sheet.iterator();
		rowIt.next(); // discard header
		
		// lpTable keeps count of instances with certain predictive attributes and classification attributes 
		HashMap < Integer, double [][] > lpTable = new HashMap <>(); // key: predictive attribute # , value: 2d array keeping track of instance counts  
		// initialize table as many as predictive attributes 
		for (int key = 1; key < NUM_OF_PREDICTIVE_ATTRIBUTES+1; key++ ){
			double [][] value = new double [PREDICTIVE_ATT_VALUES+1][CLASSIFICATION_ATT_VALUES+1];
			lpTable.put(key, value);
		}

		// for the number of classification attribute values keep track of lp(X.a6 = 1), lp(X.a6 = 2), lp(X.a6 = 3),
		double [] classificationAttributeCount = new double [CLASSIFICATION_ATT_VALUES+1];


		// training set -----------------------------------------------------------------
		for (int i = 0; i < TRAINING_COUNT; i++) {
			Row row = rowIt.next();

			// array containing data for each row 
			int [] line = new int [PREDICTIVE_ATT_INDEX+1]; // size 7 

			// iterate on cells for the current row
			Iterator<Cell> cellIterator = row.cellIterator();

			// one based indexing of predictive attributes to avoid confusion 
			for (int j = 1; j < PREDICTIVE_ATT_INDEX+1; j++) {
				Cell cell = cellIterator.next();
				line[j] = (int) cell.getNumericCellValue();
			}

			assort (line, lpTable, classificationAttributeCount);

		}// end of training set 


		// verbose output ---------------------------------------------------------------
		if (verbose){
			System.out.println("- verbose output -\n");
			for (int i = 1; i < CLASSIFICATION_ATT_VALUES+1; i++){
				System.out.printf("%.4f	", lpClassificationAttribute(classificationAttributeCount[i], TRAINING_COUNT));
			}
			System.out.println("\n");
		}

		for (int i = 1; i < NUM_OF_PREDICTIVE_ATTRIBUTES+1; i++){
			for( int j = 1; j < CLASSIFICATION_ATT_VALUES + 1; j++){
				for (int k = 1; k< PREDICTIVE_ATT_VALUES+1; k++){
					double lp = lp(i,k,j,lpTable,classificationAttributeCount[j]);
					if (verbose) System.out.printf("%.4f	",lp);

					// store new computed value to lpTable 
					lpTable.get(i)[k][j] = lp;
				}
				if (verbose )System.out.println();
			}
			if (verbose) System.out.println();
		}


		// increment iterator to back of excel file 
		int skip = DATASET_COUNT - TRAINING_COUNT - TEST_COUNT;
		for (int i = 0; i < skip; i ++){
			rowIt.next();
		}


		// test set ---------------------------------------------------------------------
		int [][] actualPredictCountTable = new int [2][2]; 

		for (int i = 0; i < TEST_COUNT; i++){
			Row row = rowIt.next();

			// array containing data for each row 
			int [] line = new int [PREDICTIVE_ATT_INDEX+1]; // size 7 

			// iterate on cells for the current row
			Iterator<Cell> cellIterator = row.cellIterator();

			// one based indexing of predictive attributes to avoid confusion 
			for (int j = 1; j < PREDICTIVE_ATT_INDEX+1; j++) {
				Cell cell = cellIterator.next();
				line[j] = (int) cell.getNumericCellValue();
			}

			// use minimum value to label classification attribute 
			double min = Double.MAX_VALUE;
			int minLabel = -1;
			for ( int j = 1 ; j < CLASSIFICATION_ATT_VALUES+1; j++){
				double sum = 0.0;
				for ( int k = 1; k< NUM_OF_PREDICTIVE_ATTRIBUTES+1; k ++){
					sum += lpTable.get(k)[line[k]][j];
				}
				sum += lpClassificationAttribute(classificationAttributeCount[j], TRAINING_COUNT);
				if ( min > sum ){
					min = sum; 
					minLabel = j;
				}
			}

			// keep count of predictions and compare with labels 
			int classification = line[6];
			if ( minLabel == 3 && classification == 3 ){
				actualPredictCountTable[0][0] ++;
			}
			else if ( minLabel == 3 && classification != 3){
				actualPredictCountTable[0][1] ++;
			}
			else if ( minLabel != 3 && classification == 3){
				actualPredictCountTable[1][0] ++;
			}
			else{ // true negative 
				if ( minLabel == classification) actualPredictCountTable[1][1] ++;
			}
		}
		// end of test set 
		

		// non-verbose output -----------------------------------------------------------
		System.out.printf("Accuracy = %5.4f	", (float)(actualPredictCountTable[0][0]+ actualPredictCountTable[1][1]) / TEST_COUNT );
		System.out.printf("Precision = %5.4f	", (float)actualPredictCountTable[0][0] / ( actualPredictCountTable[0][0]+ actualPredictCountTable[0][1])  );
		System.out.printf("Recall = %5.4f	", (float)actualPredictCountTable[0][0] / ( actualPredictCountTable[0][0]+ actualPredictCountTable[1][0])  ) ;
		System.out.println();

		workbook.close();
		fileInputStream.close();

	}// end of main 

	// function calculates log base 2
	public static double log2(double d){
		return Math.log(d)/Math.log(2);
	}

	// function to increment count for instances that have corresponding classification attributes and predictive attributes 
	public static void assort(int[] line, HashMap<Integer,double[][]> lpTable, double [] classificationAttributeCount){
		int size = line.length; // size 7
		int classification = line[size-1];
		classificationAttributeCount[classification] += 1.0;

		// for each predictive attribute in the row 
		for ( int i = 1; i < size-1; i ++){
			lpTable.get(i)[line[i]][classification] += 1.0;
		}
	}

	public static double lpClassificationAttribute(double classificationAttributeCount, int T){
		return -1 * log2((classificationAttributeCount + 0.1) / (T + 0.3)); 
	}

	public static double lp(int predictiveAttNum, int predictiveAttVal, int classificationAttVal, HashMap < Integer, double [][] > lpTable, double classificationAttributeCount){
		return -1 * log2( (lpTable.get(predictiveAttNum)[predictiveAttVal][classificationAttVal] + 0.1) / (classificationAttributeCount + 0.4));
	}
}

