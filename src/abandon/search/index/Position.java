package abandon.search.index;

/**
 * 用于定位某个term在倒排表中第几个文件的第几行
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