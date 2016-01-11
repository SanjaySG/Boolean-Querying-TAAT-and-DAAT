import java.util.Comparator;
/**
 * 
 * @author Sanjay Surendranath Girija
 *
 */

/** Comparator to sort queries in terms of increasing Document Frequency**/
public class QueryComparator implements Comparator<String>{

	@Override
	public int compare(String o1, String o2) {
		// TODO Auto-generated method stub
		Integer freq1=Index.getIndex().get(o1).getDocFrequency();
		Integer freq2=Index.getIndex().get(o2).getDocFrequency();
		return freq1.compareTo(freq2);
	}

}
