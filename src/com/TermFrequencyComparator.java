package com;

import java.util.Comparator;
/**
 * 
 * @author Sanjay Surendranath Girija
 *
 */

/** Comparator to sort Linked List of postings list in terms of decreasing Term Frequency**/
public class TermFrequencyComparator implements Comparator<Posting>{

	@Override
	public int compare(Posting o1, Posting o2) {
		// TODO Auto-generated method stub
		Integer tf1=new Integer(o1.getTermFrequency());
		Integer tf2=new Integer(o2.getTermFrequency());
		return tf2.compareTo(tf1);
	}

}
