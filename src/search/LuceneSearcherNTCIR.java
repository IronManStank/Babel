package search;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.ChineseAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class LuceneSearcherNTCIR {
	HashMap<String, TreeMap<Double, String>> reldict;
	HashMap<String, ArrayList<String>> dict;
	HashMap<Document, Double> documents;
	Directory directory;
	String indexDir;
	
	public LuceneSearcherNTCIR(HashMap<String, ArrayList<String>> dict, HashMap<String, TreeMap<Double, String>> reldict, String indexDir) {
		this.dict = dict;
		this.reldict = reldict;
		this.indexDir = indexDir;
		try{
			directory = FSDirectory.open(new File(indexDir));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public String transformSolrMetacharactor(String input){
	    StringBuffer sb = new StringBuffer();
	    String regex = "[+\\-&|!(){}\\[\\]^\"~*?:(\\)]";
	    Pattern pattern = Pattern.compile(regex);
	    Matcher matcher = pattern.matcher(input);
	    while(matcher.find()){
	        matcher.appendReplacement(sb, "\\\\"+matcher.group());
	    }
	    matcher.appendTail(sb);
	    return sb.toString();
	}
	
	public ArrayList<Map.Entry<Document,Double>> search(String query) {
		//query = transformSolrMetacharactor(query);
		//完成query的翻译、lucene的查询，并对相关文档进行排序
		String[] queryTerms = query.split(" ");
		System.out.println("search:"+query+" "+queryTerms.length);
		//使用query中term的目标语言对应前k个term组成目标语言query
		//DONE 使用dict。实验证明，使用gerneal.dict的效果很差，词典本身不带概率，很多无效翻译
		//DONE 合并重复target term，计算权重
		HashMap<String, Double> termsWeight = new HashMap<String, Double>();
		String targetQuery = "";
		for(String term:queryTerms) {
			
			if(dict.containsKey(term)) {
				ArrayList<String> x = dict.get(term);
				if(x.size() <= 2) {
					for(String s:x) {
						if(termsWeight.containsKey(s)) {
							termsWeight.put(s, termsWeight.get(s)+1.0);
						}
						else {
							termsWeight.put(s, 1.0);
						}
					}
				}
			}
			if(reldict.containsKey(term)) {
				TreeMap<Double, String> x = reldict.get(term);
				int num = 0;
				for(Map.Entry<Double, String> targetTerm:x.entrySet()) {
					if(num > 2) break;
					//if(targetTerm.getValue().length() < 2) continue;
					if(termsWeight.containsKey(targetTerm.getValue())) {
						termsWeight.put(targetTerm.getValue(),termsWeight.get(targetTerm.getValue())+targetTerm.getKey());
					}
					else {
						termsWeight.put(targetTerm.getValue(),targetTerm.getKey());
					}
					num++;
					//targetQuery += transformSolrMetacharactor(targetTerm.getValue())+"^"+(targetTerm.getKey()*100)+" ";
				}
				
			}
		}
		for(Map.Entry<String, Double> queryTermWeight:termsWeight.entrySet()) {
			targetQuery += transformSolrMetacharactor(queryTermWeight.getKey())+"^"+queryTermWeight.getValue()+" ";
		}
		documents = new HashMap<Document, Double>();
		
		try {
			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
			IndexReader ireader = IndexReader.open(directory);
			IndexSearcher searcher = new IndexSearcher(ireader);
			//QueryParser parser = new QueryParser(Version.LUCENE_36, "TEXT",analyzer);
			String fields[] = new String[]{"HEADLINE","TEXT"};
			Map<String, Float> weight = new HashMap<String, Float>();
			weight.put("HEADLINE", 10.0f);
			weight.put("TEXT", 1.0f);
			MultiFieldQueryParser parser = new MultiFieldQueryParser(Version.LUCENE_36, fields, analyzer, weight);
			Query queryTerm = null;
			System.out.println(targetQuery);
			queryTerm = parser.parse(targetQuery);
			if(queryTerm != null) {
				ScoreDoc[] hits  = searcher.search(queryTerm,null,3000).scoreDocs;
				for(int i = 0 ; i < hits.length; i++){
					Document hitDoc = searcher.doc(hits[i].doc);
					documents.put(hitDoc, (double)hits[i].score);
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
