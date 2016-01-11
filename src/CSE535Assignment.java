import java.io.IOException;

/**
 * 
 * @author Sanjay Surendranath Girija
 *
 */


public class CSE535Assignment {

	/**
	 * Main function 
	 *  
	 * @param inputFile
	 * @param outputFile
	 * @param k
	 * @param queryFile
	 * @throws IOException
	 */
	public static void main(String args[]) throws IOException {
				
		String inputFile= args[0];
		String outputFile=args[1];
		int k= Integer.parseInt(args[2]);
		String queryFile=args[3];
		
		
		
		/** Parse data from the input file*/
		ParseData parser = new ParseData();
		parser.parse(inputFile);
		
		IndexOperations oper = new IndexOperations();
		
		/** Create and write to output file*/
		oper.fileWrite(outputFile);
		
		/** Get top k terms*/
		oper.getTopK(k);
		
		/** Parse the query file and perform all the AND and OR operations*/
		oper.readQueryFile(queryFile);
		oper.close();
	}

}
