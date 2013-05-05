package abandon.search.document;

import java.util.TreeMap;

/**
 * 文件类，定义Babel中的文件，每个文件包含若干个field
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