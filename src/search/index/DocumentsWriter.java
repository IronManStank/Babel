package search.index;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import search.document.Document;

/**
 * 负责将文件写入索引
 * 遍历文件的每一个field，如果不存在于indexInfo中则创建field；遍历field的content的每一个term写倒排表
 * @author locoyou
 *
 */

public class DocumentsWriter {
	public static void addDocument(IndexInfo indexInfo, Document doc, File directory) {
		//遍历doc的所有field
		for(Map.Entry<String, String> m : doc.fields.entrySet()) {
			String field = m.getKey();
			String content = m.getValue();
			//如果该field已经在索引中被记录过
			if(indexInfo.fieldID.containsKey(field)) {
				int fieldID = indexInfo.fieldID.get(field);
				TreeMap<String, Position> invertedListEntry = indexInfo.fieldList.get(fieldID);
				String[] terms = content.split(" ");
				//遍历该field的每一个term
				for(String term : terms) {
					//如果该term被记录过，读取对应文件并修改写回
					if(invertedListEntry.containsKey(term)) {
						
					}
					//没被记录过的term将新创建一个倒排表的entry，读取最后一个文件（或新建）并增加新行
					else {
						
					}
				}
			}
			//没被记录过的field将被新创建
			else {
				
			}
		}
	}
}