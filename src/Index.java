

import java.util.HashMap;
import java.util.LinkedList;
/**
 * 
 * @author Sanjay Surendranath Girija
 *
 */

/** Stores the Index and the Dictionary*/
public class Index {
	
	/** The HashMap index stores the index. The key of the map is the term, and the value is an object of 
	 *	PostingsData.
	 *	PostingsData has {int docFrequency, 
	 *	                  LinkedList<Posting> postingsListDAAT, 
	 *	   				  LinkedList<Posting> postingsListTAAT }
	 *	In essence, each term maps to its document frequency and postings lists, sorted in two different ways */
	private static HashMap<String, PostingsData> index = new HashMap<String, PostingsData>();
	
	/**
	 * The dictionary is also stored as a linked list. It contains the terms and its document frequencies 
	 */
	private static LinkedList<Dictionary> dictionary = new LinkedList<Dictionary>();
	
	public static LinkedList<Dictionary> getDictionary() {
		return dictionary;
	}

	public static HashMap<String, PostingsData> getIndex() {
		return index;
	}


	
		

}
