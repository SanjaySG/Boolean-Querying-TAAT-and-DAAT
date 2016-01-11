

/** Stores a single posting, consisting of docID and term frequency*/
public class Posting {
	
	private int docID;
	private int termFrequency;
	
	
	public int getDocID() {
		return docID;
	}

	public void setDocID(int docID) {
		this.docID = docID;
	}


	public int getTermFrequency() {
		return termFrequency;
	}

	public void setTermFrequency(int termFrequency) {
		this.termFrequency = termFrequency;
	}



}
