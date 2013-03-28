package CLRLM;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class Entry {
	String sourceFile = "";
	String targetFile = "";
	public static void main(String[] args) {
		Entry entry = new Entry();		
		if(args.length > 0 && args[0].equalsIgnoreCase("t")) {
			entry.sourceFile = "source.txt";
			entry.targetFile = "target.txt";
			entry.train();
		}
		Relevance relevance = new Relevance();
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String line;
			while(true) {
				line = br.readLine();
				if(line.equalsIgnoreCase("exit"))
					break;
				relevance.configure(line);
			}
			relevance.close();
		}
		catch(Exception e) {
			System.out.println(e);
			System.out.println(1);
		}
	}
	
	public void train() {
		try{
			HashMap<String, Integer> countSource = new HashMap<String, Integer>();
			HashMap<String, Integer> countTarget = new HashMap<String, Integer>();
			int totalSource = 0, totalTarget = 0;
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFile)));
			String line;
			while((line = br.readLine()) != null) {
				String terms[] = line.split(" ");
				for(String term: terms) {
					if(countSource.containsKey(term)) {
						countSource.put(term, countSource.get(term)+1);
					}
					else {
						countSource.put(term, 1);
					}
					totalSource++;
				}
			}
			br.close();
			br = new BufferedReader(new InputStreamReader(new FileInputStream(targetFile)));
			while((line = br.readLine()) != null) {
				String terms[] = line.split(" ");
				for(String term: terms) {
					if(countTarget.containsKey(term)) {
						countTarget.put(term, countTarget.get(term)+1);
					}
					else {
						countTarget.put(term, 1);
					}
					totalTarget++;
				}
			}
			br.close();
			PrintWriter bw = new PrintWriter(new OutputStreamWriter(new FileOutputStream("probSource.txt")));
			for(Map.Entry<String, Integer> m:countSource.entrySet()) {
				bw.println(m.getKey() + " " + (double)m.getValue()/(double)totalSource);
			}
			bw.close();
			bw = new PrintWriter(new OutputStreamWriter(new FileOutputStream("probTarget.txt")));
			for(Map.Entry<String, Integer> m:countTarget.entrySet()) {
				bw.println(m.getKey() + " " + (double)m.getValue()/(double)totalTarget);
			}
			bw.close();
		}
		catch(Exception e) {
			System.out.println(e);
		}
	}
}