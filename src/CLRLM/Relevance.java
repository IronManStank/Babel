package CLRLM;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Relevance {
	BufferedReader sourceR;
	BufferedReader targetR;
	BufferedReader probSourceR;
	BufferedReader probTargetR;
	ArrayList<String[]> sourceText, targetText, documents;
	HashMap<String, Double> probSource, probTarget;
	TreeMap<Double, String[]> KLDocument; 
	
	public Relevance() {
		try{
			sourceR = new BufferedReader(new InputStreamReader(new FileInputStream("source.txt")));
			targetR = new BufferedReader(new InputStreamReader(new FileInputStream("target.txt")));
			probSourceR = new BufferedReader(new InputStreamReader(new FileInputStream("probSource.txt")));
			probTargetR = new BufferedReader(new InputStreamReader(new FileInputStream("probTarget.txt")));
			sourceText = new ArrayList<String[]>();
			targetText = new ArrayList<String[]>();
			documents = new ArrayList<String[]>();
			probSource = new HashMap<String, Double>();
			probTarget = new HashMap<String, Double>();
			String line;
			while((line = sourceR.readLine()) != null) {
				sourceText.add(line.split(" "));
			}
			sourceR.close();
			System.out.println("sourceR closed");
			while((line = targetR.readLine()) != null) {
				targetText.add(line.split(" "));
			}
			targetR.close();
			System.out.println("targetR closed");
			while((line = probSourceR.readLine()) != null) {
				probSource.put(line.split(" ")[0], Double.valueOf(line.split(" ")[1]));
			}
			probSourceR.close();
			System.out.println("probSourceR closed");
			while((line = probTargetR.readLine()) != null) {
				probTarget.put(line.split(" ")[0], Double.valueOf(line.split(" ")[1]));
			}
			probTargetR.close();
			System.out.println("probTargetR closed");
			documents = targetText;			
		}
		catch(Exception e) {
			System.out.println(e);
			System.out.println(2);
		}
	}
	
	public void configure(String query) {
		String[] queryTerms = query.split(" ");
		HashMap<String, Double> pwR = new HashMap<String, Double>();
		ArrayList<Double> pqM = new ArrayList<Double>();
		double totalProb = 0;
		/*
		 * 先遍历Ms，获取query在Ms上的概率，即P(Q|Ms)
		 */
		for(int i = 0; i < sourceText.size(); i++) {
			double p = 1;
			for(int j = 0; j < queryTerms.length; j++) {
				p *= probS(queryTerms[j], sourceText.get(i));
			}
			pqM.add(p);
			totalProb += p;
		}
		
		/*
		 * 遍历w，计算P(w|Q)=P(w|Mt)*P(Ms|Q),其中计算P(Ms|Q)时使用贝叶斯公式并忽略P(Ms)项
		 */
		for(Map.Entry<String, Double> targetTerm:probTarget.entrySet()) {
			String w = targetTerm.getKey();
			double pwRm = 0;
			for(int i = 0; i < targetText.size(); i++) {
				String[] Mt = targetText.get(i);
				double p = probT(w, Mt);
				p = p * pqM.get(i);
				p = p /totalProb;
				pwRm += p;
			}
			pwR.put(w, pwRm);
		}
		
		/*
		 * 遍历文档，根据KL散度计算每篇文档与query的相关性
		 */
		KLDocument = new TreeMap<Double, String[]>();
		for(int i = 0; i < documents.size(); i++) {
			String[] document = documents.get(i);
			double p = 0;
			for(Map.Entry<String, Double> targetTerm:probTarget.entrySet()) {
				String w = targetTerm.getKey();
				double pwD = probT(w, document);
				p += pwR.get(w)*Math.log(pwR.get(w)/pwD);
			}
			KLDocument.put(p, document);
		}
		
		/*
		 * 输出前10个相关文档
		 */
		int num = 0;
		for(Map.Entry<Double, String[]> document:KLDocument.entrySet()) {
			String s = "";
			String[] terms = document.getValue();
			for(int i = 0; i < terms.length; i++) {
				s += terms[i] + " ";
			}
			System.out.println(s);
			System.out.println(document.getKey());
			num++;
			if(num > 10) break;
		}
		
	}
	
	private double probS(String t, String[] M) {
		int c = 0;
		for(int i = 0; i < M.length; i++) {
			if(t.equalsIgnoreCase(M[i]))
				c++;
		}
		return 0.8*c/M.length+0.2*probSource.get(t);
	}
	
	private double probT(String t, String[] M) {
		int c = 0;
		for(int i = 0; i < M.length; i++) {
			if(t.equalsIgnoreCase(M[i]))
				c++;
		}
		return 0.8*c/M.length+0.2*probTarget.get(t);
	}
	
	public void close() {
		try{
			sourceR.close();
			targetR.close();
			probSourceR.close();
			probTargetR.close();
		}
		catch(Exception e) {
			System.out.println(e);
		}
	}
}