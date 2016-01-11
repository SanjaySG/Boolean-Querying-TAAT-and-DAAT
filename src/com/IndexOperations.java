package com;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *  @author Sanjay Surendranath Girija
 */

/**
 * 
 * The class IndexOperations has methods for reading the query file and performing all the required operations, 
 * namely - getTopK(int), getPostings(String), termAtATimeQueryAnd(String[]), termAtATimeQueryOr(String[]), 
 * 			docAtATimeQueryAnd(String[]), docAtATimeQueryOR(String[])
 * 
 * It also has additional methods - readQueryFile(String) - for reading the query file. Also, calls to 
 * 														    the above methods are within the  readQueryFile() method
 * 									fileWrite(String) - for creating an instance of bufferedwriter
 * 									close() - for closing the reader and writer
 */
public class IndexOperations {

	private BufferedReader br=null;
	private BufferedWriter bw =null;

	/**
	 * This method creates an instance of the bufferedwriter used for writing to the output file. The output file
	 * is specified by the parameter String fileName.
	 * 
	 * @param fileName		Name of output file
	 */
	public void fileWrite(String fileName){
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "utf-8"));
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Displays the top k terms based on size of the postings list.
	 * A linked list 'dictionary' has the list of terms sorted in the order of the document frequency. The first
	 * k terms of the list are retrieved when the method is called.
	 * 
	 * @param k				Number of top terms to display
	 * @throws IOException
	 */
	public void getTopK(int k) throws IOException {

		/**  Static call to the dictionary list present in the Index class. */
		LinkedList<Dictionary> dict = Index.getDictionary();

		bw.write("FUNCTION: getTopK "+k);
		bw.newLine();
		StringBuilder sb= new StringBuilder();
		sb.append("Result: ");

		/** Iterates through the first k terms of the dictionary list */
		for(int i=0;i<k;i++){
			sb.append(dict.get(i).getTerm()+", ");
		}
		String result=sb.toString();
		result=result.replaceAll(", +$", "");
		bw.write(result);
		bw.newLine();

	}

	/**
	 * Reads and parses the query file to separate the query terms.
	 * Once the query terms in each line are retrieved, calls the following methods:
	 * 	getPostings(String), termAtATimeQueryAnd(String[]), termAtATimeQueryOr(String[]), 
	 * 	docAtATimeQueryAnd(String[]), docAtATimeQueryOR(String[]) with either the individual
	 * query terms or an array of query terms as the parameter.
	 * 
	 * @param queryFileName			Name of the query file
	 */
	public void readQueryFile(String queryFileName) {

		try {
			/** Creates an instance of bufferedReader for reading the query file */
			br= new BufferedReader(new FileReader(queryFileName));
			String line;
			int flag=1;
			while((line=br.readLine())!=null){
				flag=0;
				String[] query = line.split(" ");
				for(int i=0;i<query.length;i++){
					/** Calls the getPostings() method to retrieve the postings list of the 
					 *  query term */
					getPostings(query[i]);
				}

				/** Calls method to perform the Term at a Time AND operation on an array of query terms*/
				termAtATimeQueryAnd(query);

				/** Calls method to perform the Term at a Time OR operation on an array of query terms*/
				termAtATimeQueryOr(query);

				/** Calls method to perform the Document at a Time AND operation on an array of query terms*/
				docAtATimeQueryAnd(query);

				/** Calls method to perform the Document at a Time OR operation on an array of query terms*/
				docAtATimeQueryOR(query);
			}
			if(flag==1){
				bw.write("Unable to read the file containing queries or the file is empty");
				bw.newLine();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * 
	 * Retrieves the postings list of the passed query term.
	 * The postings list of the query term is retrieved in two ways 
	 * - sorted by Document Id and sorted by Term Frequency.
	 * 
	 * @param qString		Query term whose postings list is to be retrieved
	 * @throws IOException
	 */

	public void getPostings(String qString) throws IOException {

		/** Retrieves the postings data i.e. object of Posting Data class for each term
		 *  in a static way.
		 *  Here Index.getIndex() retrieves a HashMap with the terms as the key and
		 *  PostingsData as the value.
		 *  Each PostingsData object has the document frequency of a term, and the postings 
		 *  list of the term sorted in two ways - by document id and the other by term frequency.
		 */
		PostingsData postingsData =Index.getIndex().get(qString);

		if(postingsData!=null){

			/** Returns the postings list of the term sorted by Document Id*/
			LinkedList<Posting> listSortedDoc = postingsData.getPostingsListDAAT();

			/** Returns the postings list of the term sorted by Term Frequency*/
			LinkedList<Posting> listSortedFreq = postingsData.getPostingsListTAAT();

			bw.write("FUNCTION: getPostings "+qString);
			bw.newLine();

			StringBuffer sb1= new StringBuffer();
			sb1.append("Ordered by doc IDs:");

			StringBuffer sb2= new StringBuffer();
			sb2.append("Ordered by TF:");

			/** Iterates through both the postings lists simultaneously */
			for(int i=0;i<listSortedDoc.size();i++){
				sb1.append(listSortedDoc.get(i).getDocID()+", ");
				sb2.append(listSortedFreq.get(i).getDocID()+", ");
			}  
			String result1=sb1.toString();
			result1=result1.replaceAll(", +$", "");
			String result2=sb2.toString();
			result2=result2.replaceAll(", +$", "");
			bw.write(result1);
			bw.newLine();
			bw.write(result2);
			bw.newLine();

		}
		else{
			bw.write("FUNCTION: getPostings "+qString);
			bw.newLine();
			bw.write("term not found");
			bw.newLine();
		}
	}


	/**
	 * Method performs AND operation on the query terms whose postings 
	 * lists are sorted by decreasing Term Frequency. The operation is 
	 * performed Term At A Time.
	 * 
	 *  The first two postings lists are compared the result is stored 
	 *  in an intermediate list. The intermediate list is then compared 
	 *  to the next postings list and is updated. This process continues
	 *  for all the postings lists.
	 *  
	 *  The final list is then sorted.
	 * 
	 * @param query			Array of query terms
	 * @throws IOException
	 */
	public void termAtATimeQueryAnd(String[] query) throws IOException {

		StringBuffer querySb= new StringBuffer();
		querySb.append("FUNCTION: termAtATimeQueryAnd ");
		for(int z=0;z<query.length;z++){
			querySb.append(query[z]+", ");
		}
		String queryStr=querySb.toString();
		queryStr=queryStr.replaceAll(", +$", "");
		bw.write(queryStr);
		bw.newLine();

		/** Retrieving the PostingsData of the first query term
		 * 
		 * For reference, PostingsData has
		 * 				{int docFrequency,
		 *  			LinkedList<Posting> postingsListDAAT, 
		 *  			LinkedList<Posting> postingsListTAAT }
		 * 	i.e. The document frequency of a term, and two postings
		 * lists. One sorted by Document ID  - postingsListDAAT and
		 * the other sorted by Term Frequency - postingsListTAAT
		 * 
		 * */
		PostingsData postingsDataZero =Index.getIndex().get(query[0]);

		if(postingsDataZero==null){
			bw.write("term not found");
			bw.newLine();
			return;
		}

		/**Used to store the intermediate list i.e result of successive ANDs
		 * 
		 * Initially the postings list of the first query term which is sorted
		 * by decreasing term frequency is stored */
		LinkedList<Posting> intermediateListTAAT=postingsDataZero.getPostingsListTAAT();
		int countComparisons=0;
		long startTime = System.nanoTime();

		/** Iterating through the remaining postings lists*/
		for(int i=1;i<query.length;i++){
			PostingsData postingsDataI =Index.getIndex().get(query[i]);

			/** If any of the postings list is null it means that the term
			 * doesn't exist in the index. Then the result of the AND 
			 * operation is term not found.*/ 
			if(postingsDataI==null){
				bw.write("term not found");
				bw.newLine();
				return;
			}
			else{
				/** Creating a list called ANDList which stores 
				 * the result of the AND operation after the 
				 * iteration is complete				 
				 */
				LinkedList<Posting> ANDList= new LinkedList<Posting>();

				/** Retrieving the postings list of the current query term */
				LinkedList<Posting> listSortedTAAT=postingsDataI.getPostingsListTAAT();

				/** Iterating through intermediate list */
				for(int j=0;j<intermediateListTAAT.size();j++){

					/** Iterating through the current list*/
					for(int k=0;k<listSortedTAAT.size();k++){

						/** Counting thee number of comparisons */
						countComparisons++;

						/** If both lists have the same Document then it is added to the ANDList */
						if(intermediateListTAAT.get(j).getDocID()==listSortedTAAT.get(k).getDocID()){
							ANDList.add(intermediateListTAAT.get(j));
							break;
						}

					}
				}

				/** Intermediate List is updated*/
				intermediateListTAAT=ANDList;

			}

		}

		/** End time*/
		long endTime = System.nanoTime();
		double elapsedTime = ((endTime-startTime)/1000000000.0);
		if(intermediateListTAAT==null){
			bw.write("0 documents are found");
			bw.newLine();
			bw.write(countComparisons+" comparisons are made");
			bw.newLine();
			bw.write(elapsedTime+" seconds are used");
			bw.newLine();
			bw.write("nn comparisons are made with optimization");
			bw.newLine();
			bw.write("Result: ");
			bw.newLine();
		}
		else{
			bw.write(intermediateListTAAT.size()+" documents are found");
			bw.newLine();
			bw.write(countComparisons+" comparisons are made");
			bw.newLine();
			bw.write(elapsedTime+" seconds are used");
			bw.newLine();
			bw.write("nn comparisons are made with optimization");
			bw.newLine();
			StringBuffer sb = new StringBuffer();
			sb.append("Result: ");

			/** Sorting the final result of the AND operation*/
			Collections.sort(intermediateListTAAT, new DocIDComparator());
			for(int i=0;i<intermediateListTAAT.size();i++){
				sb.append(intermediateListTAAT.get(i).getDocID()+", ");
			}
			String result=sb.toString();
			result=result.replaceAll(", +$", "");
			bw.write(result);
			bw.newLine();
		}
	}

	/**
	 * 
	 * Method performs OR operation on the query terms whose postings 
	 * lists are sorted by decreasing Term Frequency. The operation is 
	 * performed Term At A Time.
	 * 
	 *  All the documents of the first postings list is added to an intermediate
	 *  list. The next postings list is then iterated through and each document in
	 *  it is checked for its presence in the intermediate list by iterating through
	 *  the intermediate list as well. All documents that are not present in the 
	 *  intermediate list are added to it.
	 *  
	 *  The final list is then sorted.
	 * 
	 * 
	 * @param query			Array of query terms
	 * @throws IOException
	 */
	public void termAtATimeQueryOr(String[] query) throws IOException {

		StringBuffer querySb= new StringBuffer();
		querySb.append("FUNCTION: termAtATimeQueryOr ");
		for(int z=0;z<query.length;z++){
			querySb.append(query[z]+", ");
		}
		String queryStr=querySb.toString();
		queryStr=queryStr.replaceAll(", +$", "");
		bw.write(queryStr);
		bw.newLine();

		/** Retrieving the PostingsData of the first query term */
		PostingsData postingsDataZero =Index.getIndex().get(query[0]);
		int size=0;

		/** Stores the postings list of the first query term that is present in the index into
		 * postingDataZero*/
		while(postingsDataZero==null && size<query.length){
			size++;
			postingsDataZero =Index.getIndex().get(query[size]);
		}

		/** If no term of the query terms is present in the index then 
		 * the result of the OR operation is empty */
		if(postingsDataZero==null){
			bw.write("term not found");
			bw.newLine();
			return;
		}

		int countComparisons=0;
		/** Start time */
		long startTime = System.nanoTime();

		/** Initializing ORList as empty. The OR of all the postings lists are stored to the ORList */
		LinkedList<Posting> ORList = new LinkedList<Posting>();

		/** Iterating through the posting lists of all queries which are sorted by decreasing Term Frequency*/
		for(int i=0;i<query.length;i++){
			PostingsData postingsDataI =Index.getIndex().get(query[i]);
			LinkedList<Posting> listSortedTAAT=postingsDataI.getPostingsListTAAT();

			/** For the first postings list, adding all the elements to the OR List */
			if(i==0){
				for(int j=0;j<listSortedTAAT.size();j++){
					ORList.add(listSortedTAAT.get(j));
				}
			}
			else{
				/** Iterating through the documents of the current postings list*/
				for(int j=0;j<listSortedTAAT.size();j++){

					int flag=0;

					/** Iterating through the OR list which stores the OR result of the previous iterations*/
					for(int k=0;k<ORList.size();k++){
						countComparisons++;

						/** If the document is already present in the OR List, then break from
						 * the loop and check for the next document*/
						if(listSortedTAAT.get(j).getDocID()==ORList.get(k).getDocID()){
							flag=1;
							break;
						}
					}
					/** If the document is not present in the OR List, then add it to the OR List*/
					if(flag==0)
						ORList.add(listSortedTAAT.get(j));
				}
			}

		}

		/** End time */
		long endTime = System.nanoTime();
		double elapsedTime = ((endTime-startTime)/1000000000.0);
		bw.write(ORList.size()+" documents are found");
		bw.newLine();
		bw.write(countComparisons+" comparisons are made");
		bw.newLine();
		bw.write(elapsedTime+" seconds are used");
		bw.newLine();
		bw.write("nn comparisons are made with optimization");
		bw.newLine();
		StringBuffer sb = new StringBuffer();
		sb.append("Result: ");

		/** Sort the OR List in terms of increasing docID */
		Collections.sort(ORList, new DocIDComparator());

		/** Iterate through the ORList and print its documents */
		Iterator<Posting> iterator= ORList.iterator();
		while(iterator.hasNext()){
			sb.append(iterator.next().getDocID()+", ");
		}
		String result=sb.toString();
		result=result.replaceAll(", +$", "");
		bw.write(result);
		bw.newLine();


	}


	/**
	 *  Method performs AND operation on the query terms whose postings 
	 * lists are sorted by increasing Document ID. The operation is 
	 * performed Document At A Time.
	 * 
	 * Pointers are created to the first term of each of the lists and 
	 * their values are compared concurrently. 
	 * If the docID in the first postings list is less than that of any other 
	 * list, then its pointer is incremented as necessary.
	 * Else, the corresponding list's pointers are updated.
	 * 
	 * If all of the lists have the same document, it is added to the ANDList
	 * 
	 * The effect of using Pointers is simulated by storing the current index
	 * of every postings list and then incrementing it as necessary.
	 * 
	 * 
	 * @param query			Array of query terms
	 * @throws IOException
	 */
	public void docAtATimeQueryAnd(String[] query) throws IOException {

		StringBuffer querySb= new StringBuffer();
		querySb.append("FUNCTION: docAtATimeQueryAnd ");
		for(int z=0;z<query.length;z++){
			querySb.append(query[z]+", ");
		}
		String queryStr=querySb.toString();
		queryStr=queryStr.replaceAll(", +$", "");
		bw.write(queryStr);
		bw.newLine();

		/** Creating an Linked List which stores the postings list of all the query terms.
		 	Each of the postings lists are sorted in increasing Document ID*/
		LinkedList<LinkedList<Posting>> DAATList = new LinkedList<LinkedList<Posting>>();

		int countComparisons=0;

		/** Start time*/
		long startTime = System.nanoTime();

		/** Creating a list ANDList to store the result of the AND operation*/
		LinkedList<Posting> ANDList= new LinkedList<Posting>();

		/** The array pointerList stores the pointers i.e. current index to each of the postings lists */
		int pointerList[] = new int[DAATList.size()];

		/** Initializing each element of the pointerList to the first posting of each of the postings list */
		for(int k=0;k<DAATList.size();k++){
			pointerList[k]=0;
		}

		/** The AND operation can have at most as many entries as in the postings list of the first term */

		/** If there is only one query term then it is made to be the AND List*/
		if(DAATList.size()==1){
			ANDList.addAll(DAATList.get(0));
		}
		else{

			/** Iterating through the first postings list using pointers (current index value) */
			while(pointerList[0]<DAATList.get(0).size()){

				/** Retrieving the document at the current index of the first postings list */
				int docFirst=DAATList.get(0).get(pointerList[0]).getDocID();

				/** Creating a flag array which marks if any of the lists have reached their last index*/
				boolean endofList[]= new boolean[DAATList.size()];
				for(int j=0;j<DAATList.size();j++){
					endofList[j]=false;
				}

				/** Flag which shows the presence of a document in a list*/
				boolean listPresent=false;

				/** Iterating through the remaning postings lists concurrently*/
				for(int j=1;j<DAATList.size();j++){

					/** Retrieving the docID of the document pointed to by the current pointer */
					int docCurrent = DAATList.get(j).get(pointerList[j]).getDocID();

					/** Creating an iterator that starts from the index stored in pointerList[] of the current list*/
					Iterator<Posting> currentIterator=DAATList.get(j).listIterator(pointerList[j]);
					if(currentIterator.hasNext()){
						currentIterator.next();
					}

					/** If the docId of the current postings list is less than that of the 
					 * current docId of the first posting list, then the pointer to the current
					 * list is incremented till the docID becomes equal to or greater than that of
					 * the first list*/
					while(currentIterator.hasNext() && docCurrent<docFirst){
						docCurrent=currentIterator.next().getDocID();
						pointerList[j]++;
						countComparisons++;
					}

					/** Set flag is End of the current postings list*/
					if(currentIterator.hasNext()==false){
						endofList[j]=true;
					}

					/** If docID of current list is equal to that of the first postings list, go to the next 
					 * postings list and check*/
					if(docCurrent==docFirst){
						listPresent=true;
						countComparisons++;
						continue;
					}

					/** If the docId of the first postings list is less than that of the 
					 * docId of the current postings list, then the pointer to the first
					 * list is incremented till the docID becomes equal to or greater than that of
					 * the current list*/

					else if(docCurrent>docFirst){
						listPresent=false;
						countComparisons++;
						while(docFirst<docCurrent){
							pointerList[0]++;

							/** While not the end of the first list, update the first docID*/
							if(DAATList.get(0).get(pointerList[0])!=null){
								docFirst=DAATList.get(0).get(pointerList[0]).getDocID();
							}
							else
								endofList[0]=true;
							countComparisons++;

							/** If the current list has reached its last document and the value of docID 
							  	is equal to that of the first list, then one more comparison is required*/
							if(endofList[j]==true && docFirst==docCurrent)
								endofList[j]=false;
						}
						break;
					}
				}

				/** If document is present in all the postings lists, then add to the ANDList */
				if(listPresent==true){
					ANDList.add(DAATList.get(0).get(pointerList[0]));
					++pointerList[0];
				}

				/** If any of the lists have reached thier final document then no more comparisons are required	*/
				boolean flagEnd=false;
				for(int l=0;l<DAATList.size();l++){
					if(endofList[l]==true){
						flagEnd=true;
						break;
					}
				}
				if(flagEnd==true)
					break;
			}
		}

		/** End time*/
		long endTime = System.nanoTime();
		double elapsedTime = ((endTime-startTime)/1000000000.0);
		/** If result of AND is empty*/
		if(ANDList.size()==0){
			bw.write("0 documents are found");
			bw.newLine();
			bw.write(countComparisons+" comparisons are made");
			bw.newLine();
			bw.write(elapsedTime+" seconds are used");
			bw.newLine();
			bw.write("Result: ");
			bw.newLine();
		}
		else{
			bw.write(ANDList.size()+" documents are found");
			bw.newLine();
			bw.write(countComparisons+" comparisons are made");
			bw.newLine();
			bw.write(elapsedTime+" seconds are used");
			bw.newLine();
			StringBuffer sb = new StringBuffer();
			sb.append("Result: ");

			/** Iterate through the ANDList and print*/
			for(int i=0;i<ANDList.size();i++){
				sb.append(ANDList.get(i).getDocID()+", ");
			}
			String result=sb.toString();
			result=result.replaceAll(", +$", "");
			bw.write(result);
			bw.newLine();

		}
	}

	/**
	 *  Method performs OR operation on the query terms whose postings 
	 * lists are sorted by increasing Document ID. The operation is 
	 * performed Document At A Time.
	 * 
	 * Pointers are created to the first term of each of the lists and 
	 * their values are compared concurrently. 
	 * The document with the lowest docID is identified and added to the 
	 * OR List. The pointer of that document is incremented.
	 * If multiple documents contain the same docID, then it gets added only 
	 * once, but all the corresponding pointers are concurrently modified   
	 * 
	 * The effect of using Pointers is simulated by storing the current index
	 * of every postings list and then incrementing it as necessary.
	 *  
	 * @param query			Array of query terms
	 * @throws IOException
	 */
	public void docAtATimeQueryOR(String[] query) throws IOException {

		StringBuffer querySb= new StringBuffer();
		querySb.append("FUNCTION: docAtATimeQueryOR ");
		for(int z=0;z<query.length;z++){
			querySb.append(query[z]+", ");
		}
		String queryStr=querySb.toString();
		queryStr=queryStr.replaceAll(", +$", "");
		bw.write(queryStr);
		bw.newLine();

		/** Creating a LinkedList which stores the postings list of all the query terms */
		LinkedList<LinkedList<Posting>> DAATList = new LinkedList<LinkedList<Posting>>();
		for(int i=0;i<query.length;i++){
			LinkedList<Posting> listSortedDAATTemp=Index.getIndex().get(query[i]).getPostingsListDAAT();
			if(listSortedDAATTemp!=null){
				DAATList.add(i, listSortedDAATTemp);
			}
		}

		int countComparisons=0;
		/** Start time*/
		long startTime = System.nanoTime();

		/** Create a linked list which stores the result of the OR operation*/
		LinkedList<Posting> ORList= new LinkedList<Posting>();


		/** The array pointerList stores the pointers i.e. current index to each of the postings lists */
		int pointerList[] = new int[DAATList.size()];

		/** Initializing each element of the pointerList to the first posting of each of the postings list */
		for(int k=0;k<DAATList.size();k++){
			pointerList[k]=0;
		}

		/** Case where there is only one query term*/
		if(DAATList.size()==1){
			ORList.addAll(DAATList.get(0));
		}
		else{
			/** Flag which marks if all list have reached their last document*/
			boolean endOfAllLists = false;
			
			/** Flag array which marks if each of the postings list have reached their last document*/
			boolean endList[] = new boolean[DAATList.size()];
			for(int j=0;j<DAATList.size();j++){
				endList[j]=false;
			}
			
			/** Stores the number of the postings list whose current index has the smallest docID*/
			int minPos=0;
			
			/** Iterate till all lists have reached their end*/
			while(!endOfAllLists){
				int minDocId=-1;
				/** Gets the docID of the current index of the postings list that has not ended*/
				for(int k=0;k<DAATList.size();k++){
					if(endList[k]==false){
						minDocId=DAATList.get(k).get(pointerList[k]).getDocID();
					}
				}
				/** If all postings lists are at their last document*/
				if(minDocId==-1)
					break;

				/** Iterate through the current pointer of each postings list and find the minimum value*/
				for(int j=0;j<DAATList.size();j++){
					if(endList[j]==false){
						if(DAATList.get(j).get(pointerList[j]).getDocID()<=minDocId){
							minPos=j;
							minDocId=DAATList.get(j).get(pointerList[j]).getDocID();
							countComparisons++;
						}
					}
				}
				
				/** Add the document having minimum docID to ORList*/
				ORList.add(DAATList.get(minPos).get(pointerList[minPos]));

				/** In case of multiple postings lists having the same minimum docID
				  	increment all of their pointers*/
				for(int j=0;j<DAATList.size();j++){
					if(endList[j]==false){
						if(DAATList.get(j).get(pointerList[j]).getDocID()==minDocId){
							countComparisons++;
							pointerList[j]++;
							if(pointerList[j]==DAATList.get(j).size()){
								endList[j]=true;
							}
						}
					}
				}

				/** Check if all lists are at their last document*/
				endOfAllLists=true;
				for(int k=0;k<endList.length;k++){
					if(endList[k]==false){
						endOfAllLists=false;
						break;
					}
				}


			}
		}
		
		/** End time*/
		long endTime = System.nanoTime();
		double elapsedTime = ((endTime-startTime)/1000000000.0);
		bw.write(ORList.size()+" documents are found");
		bw.newLine();
		bw.write(countComparisons+" comparisons are made");
		bw.newLine();
		bw.write(elapsedTime+" seconds are used");
		bw.newLine();
		StringBuffer sb = new StringBuffer();
		sb.append("Result: ");

		/** Iterate through the ORList and print */
		for(int i=0;i<ORList.size();i++){
			sb.append(ORList.get(i).getDocID()+", ");
		}
		String result=sb.toString();
		result=result.replaceAll(", +$", "");
		bw.write(result);
		bw.newLine();

	}

	/**
	 * Close the buffered writer
	 */
	public void close(){
		try {
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}