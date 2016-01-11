

import java.util.LinkedList;

/**
 * 
 * @author Sanjay Surendranath Girija
 *
 */
public class PostingsData {
	
	/** Document frequency of a term*/
	private int docFrequency;
	
	/** Postings list of a term sorted in increasing order of Document IDs*/
	private LinkedList<Posting> postingsListDAAT = new LinkedList<Posting>();
	

	/** Postings list of a term sorted in decreasing order of Term Frequencies*/
	private LinkedList<Posting> postingsListTAAT = new LinkedList<Posting>();
	
	
	public int getDocFrequency() {
		return docFrequency;
	}
	public void setDocFrequency(int docFrequency) {
		this.docFrequency = docFrequency;
	}
	public LinkedList<Posting> getPostingsListDAAT() {
		return postingsListDAAT;
	}
	public void setPostingsListDAAT(LinkedList<Posting> postingsListDAAT) {
		this.postingsListDAAT = postingsListDAAT;
	}
	public LinkedList<Posting> getPostingsListTAAT() {
		return postingsListTAAT;
	}
	public void setPostingsListTAAT(LinkedList<Posting> postingsListTAAT) {
		this.postingsListTAAT = postingsListTAAT;
	}
	
	
}
