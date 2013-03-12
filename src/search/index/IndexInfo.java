package search.index;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * ���������࣬���ڽ��������ĵ��������л�֮����Ϊ������һ����
 * @author locoyou
 *
 */

public class IndexInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;	
	
	public int fieldNum; //��¼�ж��ٸ�field
	
	public HashMap<String, Integer> fieldID; //��field��Ӧ��ÿһ��ID��ÿ��ID��ΪfieldList�е��±�
	
	public ArrayList<TreeMap<String, Position>> fieldList;//ÿһ��TreeMapԪ�ض���һ��field�ĵ��ű���ڣ���term�͵��ű��ĳһ�ж�Ӧ����
	
	public ArrayList<Integer> fieldIndexNum;//��¼ÿһ��field�ĵ��ű��ж��ٸ��ļ�
	
	public ArrayList<Integer> fieldIndexLine;//��¼ÿһ��field�ĵ��ű����һ���ļ�Ŀǰ��������ÿ���ļ�1000��
	
	public IndexInfo() {
		fieldNum = 0;
		fieldID = new HashMap<String, Integer>();
		fieldList = new ArrayList<TreeMap<String, Position>>();
		fieldIndexNum = new ArrayList<Integer>();
		fieldIndexLine = new ArrayList<Integer>();
	}	
	
}

