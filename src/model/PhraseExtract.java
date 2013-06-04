package model;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class PhraseExtract {
	public PhraseExtract() {
		
	}
	
	HashSet<String> stopWords;
	
	public void setStopWords(String stopList) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(stopList)));
			String line;
			stopWords = new HashSet<String>();
			while((line = br.readLine()) != null) {
				stopWords.add(line);
			}
			br.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public HashSet<String> extract(String fileName) {
		HashSet<String> phrase = new HashSet<String>();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
			HashMap<String, HashMap<String, Integer>> freq = new HashMap<String, HashMap<String, Integer>>();
			HashMap<String, Integer> wordFreq = new HashMap<String, Integer>();
			String line;
			while((line = br.readLine()) != null) {
				String[] terms = line.split(" ");
				for(int i = 0; i < terms.length; i++) {
					if(stopWords.contains(terms[i])) continue;
					if(wordFreq.containsKey(terms[i])) {
						wordFreq.put(terms[i], wordFreq.get(terms[i])+1);
					}
					else {
						wordFreq.put(terms[i], 1);
					}
				}
				for(int i = 0; i < terms.length; i++) {
					for(int j = i+1; j < i+5 && j < terms.length; j++) {	
						if(!stopWords.contains(terms[i]) && !stopWords.contains(terms[j])) {	
							
							if(freq.containsKey(terms[i])) {
								if(freq.get(terms[i]).containsKey(terms[j])) {
									freq.get(terms[i]).put(terms[j], freq.get(terms[i]).get(terms[j])+1);
								}
								else {
									freq.get(terms[i]).put(terms[j], 1);
								}
							}
							else {
								HashMap<String,Integer> x = new HashMap<String, Integer>();
								x.put(terms[j], 1);
								freq.put(terms[i], x);
								
							}
							
							if(freq.containsKey(terms[j])) {
								if(freq.get(terms[j]).containsKey(terms[i])) {
									freq.get(terms[j]).put(terms[i], freq.get(terms[j]).get(terms[i])+1);
								}
								else {
									freq.get(terms[j]).put(terms[i], 1);
								}
							}
							else {
								HashMap<String,Integer> x = new HashMap<String, Integer>();
								x.put(terms[i], 1);
								freq.put(terms[j], x);
							}
						}
					}
				}
			}
			br.close();
			for(Map.Entry<String, Integer> entry:wordFreq.entrySet()) {
				String word = entry.getKey();
				int counts = entry.getValue();
				if(!freq.containsKey(word)) continue;
				if(counts < 10) continue;
				HashMap<String,Integer> list = freq.get(word);
				for(Map.Entry<String, Integer> x:list.entrySet()) {
					if((x.getValue()/(double)counts) > 0.3) {
						if(word.compareTo(x.getKey()) > 0)
							phrase.add(word + " " + x.getKey());
						else
							phrase.add(x.getKey() + " " + word);
					}
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return phrase;
	}
}