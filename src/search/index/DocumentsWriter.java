package search.index;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import search.document.Document;

/**
 * �����ļ�д������
 * �����ļ���ÿһ��field�������������indexInfo���򴴽�field������field��content��ÿһ��termд���ű�
 * @author locoyou
 *
 */

public class DocumentsWriter {
	public static void addDocument(IndexInfo indexInfo, Document doc, File directory) {
		//����doc������field
		for(Map.Entry<String, String> m : doc.fields.entrySet()) {
			String field = m.getKey();
			String content = m.getValue();
			//�����field�Ѿ��������б���¼��
			if(indexInfo.fieldID.containsKey(field)) {
				int fieldID = indexInfo.fieldID.get(field);
				TreeMap<String, Position> invertedListEntry = indexInfo.fieldList.get(fieldID);
				String[] terms = content.split(" ");
				//������field��ÿһ��term
				for(String term : terms) {
					//�����term����¼������ȡ��Ӧ�ļ����޸�д��
					if(invertedListEntry.containsKey(term)) {
						
					}
					//û����¼����term���´���һ�����ű��entry����ȡ���һ���ļ������½�������������
					else {
						
					}
				}
			}
			//û����¼����field�����´���
			else {
				
			}
		}
	}
}