package CLRLM;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.document.Document;

public class LuceneEntry {
	HashMap<String, HashMap<String, Double>> dict;
	LuceneSearcher luceneSearcher;
	
	public LuceneEntry() {
		try {
			dict = new HashMap<String, HashMap<String, Double>>();
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("data/dict.txt")));
			String line;
			while((line = br.readLine()) != null) {
				String[] terms = line.split("\\|\\|\\|");
				if(dict.containsKey(terms[0])) {
					dict.get(terms[0]).put(terms[1], Double.valueOf(terms[2]));
				}
				else {
					HashMap<String, Double> x = new HashMap<String, Double>();
					x.put(terms[1], Double.valueOf(terms[2]));
					dict.put(terms[0], x);
				}
			}
			br.close();
			luceneSearcher = new LuceneSearcher(dict);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<Map.Entry<Document,Double>> search(String query) {
		return luceneSearcher.search(query);
	}
	
	public static void main(String[] args) {
		LuceneEntry le = new LuceneEntry();
		ArrayList<Map.Entry<Document,Double>> results = le.search("德国 经济");
		int count = 0;
		for(Map.Entry<Document,Double> result:results) {
			if(count > 50) break;
			count++;
			System.out.println(result.getValue() + " " + result.getKey().get("content"));
		}
	}
}