package com;
/**
 * 
 * @author Sanjay Surendranath Girija
 *
 */

/** POJO used to store terms in the decreasing order of the size of the postings lists*/
public class Dictionary {

	private String term;
	private int docFreq;
	
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public int getDocFreq() {
		return docFreq;
	}
	public void setDocFreq(int docFreq) {
		this.docFreq = docFreq;
	}
	
	
}
