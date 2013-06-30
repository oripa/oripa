package oripa;

public enum LineType {
	NONE("NONE", 0), CUT("CUT", 1), VALLEY("VALLEY", 2), RIDGE("RIDGE", 3);
	
	private String name;
	private int val;
	
	private LineType(String name, int val){
		this.name = name;
		this.val = val;
	}
	
	public String toString(){
		return name;
	}
	
	public int toInt(){
		return val;
	}
}
