

import java.util.Comparator;
/**
 * 
 * @author Sanjay Surendranath Girija
 *
 */

/** Comparator to sort Linked List of postings list in terms of increasing document ID**/
public class DocIDComparator implements Comparator<Posting> {

	@Override
	public int compare(Posting o1, Posting o2) {
		// TODO Auto-generated method stub
		Integer docID1=new Integer(o1.getDocID());
		Integer docID2=new Integer(o2.getDocID());
		return docID1.compareTo(docID2);
	}

}
