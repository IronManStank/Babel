package index;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.ChineseAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.document.*;
import org.w3c.dom.*;

public class MakeLuceneIndexNews {
	String rootDir;
	Directory fsDirectory;
	IndexWriterConfig iwc;
	IndexWriter indexWriter;
	DocumentBuilderFactory factory;
	DocumentBuilder builder;
	
	public static void main(String[] args) {
		MakeLuceneIndexNews m = new MakeLuceneIndexNews(args[0], args[1]);
		m.makeIndex();
		/*
		//测试索引是否建立成功
		try {
			Analyzer analyzer = new ChineseAnalyzer();
			IndexReader ireader = IndexReader.open(m.fsDirectory);
			IndexSearcher searcher = new IndexSearcher(ireader);
			QueryParser parser = new QueryParser(Version.LUCENE_36, "content",analyzer);
			Query query = parser.parse("吸毒者");
			ScoreDoc[] hits  = searcher.search(query,null,10).scoreDocs;
			System.out.println(hits.length);
			System.out.println(hits[0].score+" "+searcher.doc(hits[0].doc).getField("content").stringValue());
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		*/
	}
	
	public MakeLuceneIndexNews(String rootDir, String indexDir) {
		try{
			this.rootDir = rootDir;
			fsDirectory = FSDirectory.open(new File (indexDir));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void makeIndex() {
		try {
			File root = new File(rootDir);
			iwc = new IndexWriterConfig(Version.LUCENE_36,new ChineseAnalyzer());
			indexWriter = new IndexWriter(fsDirectory,iwc);
			factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			makeFileIndex(root);
			indexWriter.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void makeFileIndex(File dir) {
		try {
			File[] files = dir.listFiles();
			for(File file:files) {
				if(file.isDirectory()) {
					makeFileIndex(file);
				}
				else {
					org.w3c.dom.Document document = builder.parse(file);
					String content = "", title = "", description = "", url = "", date = "";
					if(document.getElementsByTagName("content").item(0).getFirstChild() != null)
						content = document.getElementsByTagName("content").item(0).getFirstChild().getNodeValue();
					if(document.getElementsByTagName("title").item(0).getFirstChild() != null)
						title = document.getElementsByTagName("title").item(0).getFirstChild().getNodeValue();
					if(document.getElementsByTagName("description").item(0).getFirstChild() != null)
						description = document.getElementsByTagName("description").item(0).getFirstChild().getNodeValue();
					if(document.getElementsByTagName("url").item(0).getFirstChild() != null)
						url = document.getElementsByTagName("url").item(0).getFirstChild().getNodeValue();
					if(document.getElementsByTagName("pubTime").item(0).getFirstChild() != null)
						date = document.getElementsByTagName("pubTime").item(0).getFirstChild().getNodeValue();
					org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();
					doc.add(new Field("content", content,Field.Store.YES,Field.Index.ANALYZED));
					doc.add(new Field("title",title,Field.Store.YES,Field.Index.ANALYZED));
					doc.add(new Field("description",description,Field.Store.YES,Field.Index.ANALYZED));
					doc.add(new Field("url",url,Field.Store.YES,Field.Index.NOT_ANALYZED));
					doc.add(new Field("date",date,Field.Store.YES,Field.Index.NOT_ANALYZED));
					indexWriter.addDocument(doc);
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
