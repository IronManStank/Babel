package experiment;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.lucene.document.Document;
import org.w3c.dom.NodeList;

import search.LuceneEntryNTCIR;

public class NTCIRTest {
	HashMap<String, HashMap<String, String>> relevance;
	HashMap<String, Integer> pos;
	HashMap<String, Integer> neg;
	LuceneEntryNTCIR le;
	String taskFile;
	DocumentBuilderFactory factory;
	DocumentBuilder builder;
	
	public static void main(String[] args) {
		NTCIRTest nt = new NTCIRTest(args[0],args[1],args[2]);
		nt.test();
	}
	
	public void test() {
		try {
			org.w3c.dom.Document document = builder.parse(taskFile);
			NodeList nl = document.getElementsByTagName("TOPIC");
			for(int i = 0; i < nl.getLength(); i++) {
				String query = "", topic = "";
				int pp = 0, all = 0;
				if(document.getElementsByTagName("TITLE").item(i).getFirstChild() != null)
					query = document.getElementsByTagName("TITLE").item(i).getFirstChild().getNodeValue();
				if(document.getElementsByTagName("NUM").item(i).getFirstChild() != null)
					topic = document.getElementsByTagName("NUM").item(i).getFirstChild().getNodeValue();
				if(!query.equalsIgnoreCase("") && !topic.equalsIgnoreCase("")) {
					query = query.replaceAll(", "," ");
					ArrayList<Map.Entry<Document,Double>> results = le.search(query);
					System.out.println(pos.get(topic)+";"+neg.get(topic));
					for(Map.Entry<Document, Double> result:results) {
						String docno = result.getKey().get("DOCNO");
						String rel = relevance.get(topic).get(docno);
						if(rel != null) {
							if(rel.endsWith("1"))
								pp++;
							all++;
							System.out.println(docno + " " + relevance.get(topic).get(docno));
						}
					}
					System.out.println(pp+" "+all);
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public NTCIRTest(String relevanceFile, String taskFile, String indexDir) {
		try {
			getRelevance(relevanceFile);
			le = new LuceneEntryNTCIR(indexDir);
			this.taskFile = taskFile;
			factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void getRelevance(String relevanceFile) {
		try {
			relevance = new HashMap<String, HashMap<String, String>>();
			pos = new HashMap<String, Integer>();
			neg = new HashMap<String, Integer>();
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(relevanceFile)));
			String line;
			while((line = br.readLine()) != null) {
				String[] lines = line.split("\t"); 
				if(lines[3].equalsIgnoreCase("1")) {
					if(pos.containsKey(lines[0]))
						pos.put(lines[0], pos.get(lines[0])+1);
					else
						pos.put(lines[0], 1);
				}
				else {
					if(neg.containsKey(lines[0]))
						neg.put(lines[0], neg.get(lines[0])+1);
					else
						neg.put(lines[0], 1);
				}
				if(relevance.containsKey(lines[0])) {
					relevance.get(lines[0]).put(lines[2], lines[1]+lines[3]);
				}
				else {
					HashMap<String, String> x = new HashMap<String, String>();
					x.put(lines[2], lines[1]+lines[3]);
					relevance.put(lines[0], x);
				}
			}
			br.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}