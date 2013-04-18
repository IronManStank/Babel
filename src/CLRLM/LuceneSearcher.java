package CLRLM;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class LuceneSearcher {
	HashMap<String, HashMap<String, Double>> dict;
	HashMap<Document, Double> documents;
	Directory directory;
	String indexDir = "data/index/";
	
	public LuceneSearcher(HashMap<String, HashMap<String, Double>> dict) {
		this.dict = dict;
		try{
			directory = FSDirectory.open(new File(indexDir));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<Map.Entry<Document,Double>> search(String query) {
		//TODO 完成query的翻译、lucene的查询，并对相关文档进行排序
		String[] queryTerms = query.split(" ");
		System.out.println("search:"+query+" "+queryTerms.length);
		//将所有query中term的目标语言对应term存入targetTerms中并计算概率
		HashMap<String, Double> targetTerms = new HashMap<String, Double>();
		for(String term:queryTerms) {
			if(dict.containsKey(term)) {
				HashMap<String, Double> x = dict.get(term);
				for(Map.Entry<String, Double> targetTerm:x.entrySet()) {
					if(targetTerms.containsKey(targetTerm.getKey())) {
						targetTerms.put(targetTerm.getKey(), targetTerm.getValue() + targetTerms.get(targetTerm.getKey()));
					}
					else {
						targetTerms.put(targetTerm.getKey(), targetTerm.getValue());
					}
				}
				
			}
		}
		documents = new HashMap<Document, Double>();
		
		//遍历所有可能的target term，分别检索出相关文档并合并
		try {
			Analyzer analyzer = new EnglishAnalyzer(Version.LUCENE_36);
			IndexReader ireader = IndexReader.open(directory);
			IndexSearcher searcher = new IndexSearcher(ireader);
			QueryParser parser = new QueryParser(Version.LUCENE_36, "content",analyzer);
			Query queryTerm = null;
			for(Map.Entry<String, Double> targetTerm:targetTerms.entrySet()) {
				queryTerm = parser.parse(targetTerm.getKey());
				if(queryTerm != null) {
					ScoreDoc[] hits  = searcher.search(queryTerm,null,50).scoreDocs;
					for(int i = 0 ; i < hits.length; i++){
						Document hitDoc = searcher.doc(hits[i].doc);
						if(documents.containsKey(hitDoc)) {
							documents.put(hitDoc, hits[i].score*targetTerm.getValue() + documents.get(hitDoc));
						}
						else {
							documents.put(hitDoc, hits[i].score*targetTerm.getValue());
						}
					}
				}
			}
			searcher.close();
			ireader.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		//对相关文档排序
		ArrayList<Map.Entry<Document,Double>> l = new ArrayList<Map.Entry<Document,Double>>(documents.entrySet());   
        Collections.sort(l, new Comparator<Map.Entry<Document,Double>>() {   
            public int compare(Map.Entry<Document,Double> o1, Map.Entry<Document,Double> o2) {   
                if(o2.getValue() - o1.getValue() > 0)
                	return 1;
                else
                	return -1;
            }   
        }); 
        return l;
	}
}
