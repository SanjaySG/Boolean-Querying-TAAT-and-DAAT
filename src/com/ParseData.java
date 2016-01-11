package com;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
/**
 * 
 * @author Sanjay Surendranath Girija
 *
 */

/** Class to parse the input index file*/
public class ParseData {

	/** Linked list used to store postings*/
	private LinkedList<Posting> postingsList; 

	/** 
	 * Method to parse index file
	 * @param fileName
	 */
	public void parse(String fileName){
		BufferedReader br=null;
		File termFile= new File(fileName);
		try {
			 br= new BufferedReader(new FileReader(termFile));
			 String line;
			 while((line=br.readLine())!=null){
				 String parts1[]=line.split("\\\\c");
				 String term = parts1[0];
				 String parts2[]=parts1[1].split("\\\\m");
				 int docFreq=Integer.parseInt(parts2[0]);
				 String postingsString=parts2[1].replace("[","").replace("]", "");
				 String[] postings=postingsString.split(",");
				 
				 postingsList = new LinkedList<Posting>();
				 
				 for(int i=0;i<postings.length;i++){
					 String[] partPosting = postings[i].split("\\/"); 
					 int docId = Integer.parseInt(partPosting[0].trim());
					 int termFreq = Integer.parseInt(partPosting[1].trim());
					 
					 /** Create a Posting bbject*/
					 Posting posting = new Posting();
					 posting.setDocID(docId);
					 posting.setTermFrequency(termFreq);
					 /** Add the Posting object to the postingsList*/
					 postingsList.add(posting);
					 
				 }
				 
				 PostingsData pd =new PostingsData();
				 pd.setDocFrequency(docFreq);
				 /** Sort postings list by increasing doc ID*/
				 Collections.sort(postingsList, new DocIDComparator());
				 pd.setPostingsListDAAT(postingsList);
				 LinkedList<Posting> copyList = (LinkedList<Posting>) postingsList.clone();
				 /** Sort postings list by decreasing Term Frequency*/
				 Collections.sort(copyList, new TermFrequencyComparator());
				 pd.setPostingsListTAAT(copyList);
				 
				 /** Create a dictionary object and add term to it*/
				 Dictionary dict= new Dictionary();
				 dict.setTerm(term);
				 dict.setDocFreq(docFreq);
				 Index.getIndex().put(term, pd);
				 Index.getDictionary().add(dict);
				 
				 	 
			 }
			 /** Sort dictionary by decreasing size of postings list*/
			 Collections.sort(Index.getDictionary(), new DictionaryComparator());
			 
			 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
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
}
