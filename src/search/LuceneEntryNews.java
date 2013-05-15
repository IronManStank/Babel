package search;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.lucene.document.Document;

public class LuceneEntryNews {
	HashMap<String, TreeMap<Double, String>> reldict;
	LuceneSearcherNews luceneSearcher;
	
	public boolean isTag(String str) {
		if(str.equals("'") || str.equals(".") || str.equals("-") || str.equals("!") || str.equals("\"") || str.equals("?") || str.equals("<") || str.equals(">") || str.equals("_")) {
			return true;
		}
		return false;
	}
	
	public LuceneEntryNews() {
		try {
			reldict = new HashMap<String, TreeMap<Double, String>>();
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("data/en-ch.txt")));
			System.out.println("dict");
			String line;
			while((line = br.readLine()) != null) {
				String[] terms = line.split("\\|\\|\\|");
				if(isTag(terms[0]) || isTag(terms[1]))
					continue;
				if(reldict.containsKey(terms[0])) {
					reldict.get(terms[0]).put(Double.valueOf(terms[2]), terms[1]);
				}
				else {
					TreeMap<Double, String> x = new TreeMap<Double, String>(new Comparator<Double>() {
						public int compare(Double d1, Double d2) {
							if(d2 - d1 > 0) return 1;
							else return -1;
						}
					});
					x.put(Double.valueOf(terms[2]), terms[1]);
					reldict.put(terms[0], x);
				}
			}
			br.close();
			System.out.println("/dict");
			luceneSearcher = new LuceneSearcherNews(reldict);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<Map.Entry<Document,Double>> search(String query) {
		query = query.toLowerCase();
		return luceneSearcher.search(query);
	}
	
	public static void main(String[] args) {
		LuceneEntryNews le = new LuceneEntryNews();
		ArrayList<Map.Entry<Document,Double>> results = le.search("America");
		int count = 0;
		for(Map.Entry<Document,Double> result:results) {
			if(count > 50) break;
			count++;
			System.out.println(result.getValue() + " " + result.getKey().get("content"));
		}
	}
}