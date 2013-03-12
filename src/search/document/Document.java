package search.document;

import java.util.TreeMap;

/**
 * �ļ��࣬����Babel�е��ļ���ÿ���ļ��������ɸ�field
 * @author locoyou
 *
 */

public class Document {
	int fieldNum;
	public TreeMap<String, String> fields;
	
	Document() {
		fields = new TreeMap<String, String>();
		fieldNum = 0;
	}
	
	Document(String fieldName, String filedCont) {
		fields = new TreeMap<String, String>();
		fields.put(fieldName, filedCont);
		fieldNum = 1;
	}
	
	public void addField(String fieldName, String fieldCont) {
		fields.put(fieldName, fieldCont);
		fieldNum++;
	}
}