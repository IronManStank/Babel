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

public class LuceneEntryNTCIR {
	HashMap<String, TreeMap<Double, String>> reldict;
	HashMap<String, ArrayList<String>> dict;
	LuceneSearcherNTCIR luceneSearcher;
	
	public boolean isTag(String str) {
		if(str.equals("'") || str.equals(".") || str.equals("-") || str.equals("!") || str.equals("\"") || 
			str.equals("?")|| str.equals("<") || str.equals(">") || str.equals("_") || str.equals(":")) {
			return true;
		}
		return false;
	}
	
	public LuceneEntryNTCIR(String indexDir) {
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
			
			dict = new HashMap<String, ArrayList<String>>();
			br = new BufferedReader(new InputStreamReader(new FileInputStream("data/general.dict")));
			while((line = br.readLine()) != null) {
				line = line.substring(1,line.length()-1);
				String key = line.split(",")[0].split(":")[1];
				String value = line.split(",")[1].split(":")[1];
				key = key.substring(1,key.length()-1);
				value = value.substring(1, value.length()-1);
				if(dict.containsKey(key)) {
					dict.get(key).add(value);
				}
				else {
					ArrayList<String> x = new ArrayList<String>();
					x.add(value);
					dict.put(key, x);
				}
			}
			System.out.println("/dict");
			luceneSearcher = new LuceneSearcherNTCIR(dict, reldict, indexDir);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<Map.Entry<Document,Double>> search(String query) {
		query = query.toLowerCase();
		return luceneSearcher.search(query);
	}
}