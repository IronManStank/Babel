package search.index;

/**
 * ���ڶ�λĳ��term�ڵ��ű��еڼ����ļ��ĵڼ���
 * @author locoyou
 *
 */
public class Position {
	public int fileID;
	public int line;
	public Position(int fileID, int line) {
		this.fileID = fileID;
		this.line = line;
	}
}