package search.index;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import search.document.Document;


/**
 * IndexWriter类，负责写索引
 * @author locoyou
 *
 */

public class IndexWriter{
	
	public File directory;
	public IndexInfo indexInfo;
	String infoPath;
	
	IndexWriter(String path) {
		directory = new File(path);
		if(!directory.isDirectory()) {
			System.err.println("No such directory");
			return;
		}
		if(directory.listFiles().length > 0) {
			
			if(path.endsWith("/"))
				infoPath = path + "info";
			else
				infoPath = path + "/info";
			try {
				FileInputStream fis = new FileInputStream(infoPath);
				ObjectInputStream ois = new ObjectInputStream(fis);
				indexInfo = (IndexInfo) ois.readObject();
				ois.close();
			}
			catch(Exception e) {
				System.err.println(e);
			}
		}
		else {
			indexInfo = new IndexInfo();
		}
	}
	
	public void addDocument(Document doc) {
		DocumentsWriter.addDocument(indexInfo, doc, directory);
		
	}
	
	public void close() {
		try {
			FileOutputStream fos = new FileOutputStream(infoPath); 
			ObjectOutputStream oos = new ObjectOutputStream(fos);  
			oos.writeObject(indexInfo);
			oos.flush();
			oos.close();
		}
		catch(Exception e) {
			
		}
	}
}