package abandon.search.index;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * 索引信息类，用于进行索引的导引，序列化之后作为索引的一部分
 * @author locoyou
 *
 */

public class IndexInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;	
	
	public int fieldNum; //记录有多少个field
	
	public HashMap<String, Integer> fieldID; //将field对应到每一个ID，每个ID即为fieldList中的下标
	
	public ArrayList<TreeMap<String, Position>> fieldList;//每一个TreeMap元素都是一个field的倒排表入口，将term和倒排表的某一行对应起来
	
	public ArrayList<Integer> fieldIndexNum;//记录每一个field的倒排表有多少个文件
	
	public ArrayList<Integer> fieldIndexLine;//记录每一个field的倒排表最后一个文件目前的行数，每个文件1000行
	
	public IndexInfo() {
		fieldNum = 0;
		fieldID = new HashMap<String, Integer>();
		fieldList = new ArrayList<TreeMap<String, Position>>();
		fieldIndexNum = new ArrayList<Integer>();
		fieldIndexLine = new ArrayList<Integer>();
	}	
	
}

