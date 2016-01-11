

import java.util.Comparator;

/**
 * 
 * @author Sanjay Surendranath Girija
 *
 */

/** Comparator to sort dictionary in terms of decreasing size of postings list*/
public class DictionaryComparator implements Comparator<Dictionary> {

	@Override
	public int compare(Dictionary o1, Dictionary o2) {
		// TODO Auto-generated method stub
		Integer df1=new Integer(o1.getDocFreq());
		Integer df2=new Integer(o2.getDocFreq());
		return df2.compareTo(df1);
	}
	
}
