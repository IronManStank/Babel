package model;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

public class PhraseTable {
	public static void main(String[] args) {
		PhraseTable pt = new PhraseTable(args[0], args[1]);
		pt.setStopWords();
		pt.phraseTable();
	}
	
	String engFile;
	String chiFile;
	HashSet<String> engPhraseList, chiPhraseList;
	PhraseExtract phraseExtract;
	
	HashSet<String> stopWordsEng, stopWordsChi;
	
	public void setStopWords() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("data/stopwords.txt")));
			String line;
			stopWordsEng = new HashSet<String>();
			while((line = br.readLine()) != null) {
				stopWordsEng.add(line);
			}
			br.close();
			br = new BufferedReader(new InputStreamReader(new FileInputStream("data/stopwords_cn.txt")));
			stopWordsChi = new HashSet<String>();
			while((line = br.readLine()) != null) {
				stopWordsChi.add(line);
			}
			br.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public PhraseTable(String engFile, String chiFile) {
		this.engFile = engFile;
		this.chiFile = chiFile;
		phraseExtract = new PhraseExtract();
		phraseExtract.setStopWords("data/stopwords.txt");
		engPhraseList = phraseExtract.extract(engFile);
		try {
			PrintWriter bw = new PrintWriter(new OutputStreamWriter(new FileOutputStream("data/phraseEng.txt")));
			for(String s:engPhraseList) {
				bw.println(s);
			}
			bw.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		phraseExtract.setStopWords("data/stopwords_cn.txt");
		chiPhraseList = phraseExtract.extract(chiFile);
		try {
			PrintWriter bw = new PrintWriter(new OutputStreamWriter(new FileOutputStream("data/phraseChi.txt")));
			for(String s:chiPhraseList) {
				bw.println(s);
			}
			bw.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void phraseTable() {
		try {
			BufferedReader eng = new BufferedReader(new InputStreamReader(new FileInputStream(engFile)));
			BufferedReader chi = new BufferedReader(new InputStreamReader(new FileInputStream(chiFile)));
			String engLine, chiLine;
			HashMap<String, HashMap<String, Double>> phraseTable = new HashMap<String, HashMap<String, Double>>();
			HashMap<String, Double> phraseFreq = new HashMap<String, Double>(); 
			while((engLine = eng.readLine()) != null && (chiLine = chi.readLine()) != null) {
				String[] engTerms = engLine.split(" ");
				String[] chiTerms = chiLine.split(" ");
				
				ArrayList<String> engPhrases = new ArrayList<String>();
				for(int i = 0; i < engTerms.length; i++) {
					if(!stopWordsEng.contains(engTerms[i])) {
						engPhrases.add(engTerms[i]);
						for(int j = i + 1; j < i + 5 && j < engTerms.length; j++) {
							if(!stopWordsEng.contains(engTerms[j])) {
								String s;
								if(engTerms[i].compareTo(engTerms[j]) > 0)
									s = engTerms[i] + " " + engTerms[j];
								else
									s = engTerms[j] + " " + engTerms[i];
								if(engPhraseList.contains(s)) {
									engPhrases.add(s);
								}
							}
						}
					}
				}
				
				ArrayList<String> chiPhrases = new ArrayList<String>();
				for(int i = 0; i < chiTerms.length; i++) {
					if(!stopWordsChi.contains(chiTerms[i])) {
						chiPhrases.add(chiTerms[i]);
						for(int j = i + 1; j < i + 5 && j < chiTerms.length; j++) {
							if(!stopWordsChi.contains(chiTerms[j])) {
								String s;
								if(chiTerms[i].compareTo(chiTerms[j]) > 0)
									s = chiTerms[i] + " " + chiTerms[j];
								else
									s = chiTerms[j] + " " + chiTerms[i];
								if(chiPhraseList.contains(s)) {
									chiPhrases.add(s);
								}
							}
						}
					}
				}
				
				for(String chiPhrase:chiPhrases) {
					for(String engPhrase:engPhrases) {
						if(phraseTable.containsKey(engPhrase)) {
							if(phraseTable.get(engPhrase).containsKey(chiPhrase)) {
								phraseTable.get(engPhrase).put(chiPhrase, phraseTable.get(engPhrase).get(chiPhrase)+(1.0/chiPhrases.size())*(1.0/engPhrases.size()));
							}
							else {
								phraseTable.get(engPhrase).put(chiPhrase, (1.0/engPhrases.size())*(1.0/chiPhrases.size()));
							}
						}
						else {
							HashMap<String, Double> x = new HashMap<String, Double>();
							x.put(chiPhrase, (1.0/chiPhrases.size())*(1.0/engPhrases.size()));
							phraseTable.put(engPhrase, x);
						}
						
					}
				}
				
				for(String engPhrase:engPhrases) {
					if(phraseFreq.containsKey(engPhrase)) 
						phraseFreq.put(engPhrase, phraseFreq.get(engPhrase)+(1.0/engPhrases.size()));
					else 
						phraseFreq.put(engPhrase, (1.0/engPhrases.size()));
				}
				
				PrintWriter bw = new PrintWriter(new OutputStreamWriter(new FileOutputStream("data/phraseTable_en-ch.txt")));
				for(Map.Entry<String, HashMap<String, Double>> entry:phraseTable.entrySet()) {
					String engPhrase = entry.getKey();
					if(engPhrase.length() == 0) continue;
					ArrayList<Entry<String, Double>> l = new ArrayList<Entry<String, Double>>(entry.getValue().entrySet());
					System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
					Collections.sort(l, new Comparator<Map.Entry<String, Double>>(){
						public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
							if(o2.getValue() - o1.getValue() > 0) return 1;
							else return -1;
						}
					});
					for(int i = 0; i < 10 && i < l.size(); i++) {
						if(l.get(i).getKey().length() == 0) continue;
						bw.println(engPhrase+"|||"+l.get(i).getKey()+"|||"+l.get(i).getValue()/phraseFreq.get(engPhrase));
					}
					bw.flush();
				}
				bw.close();
			}
			eng.close();
			chi.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}