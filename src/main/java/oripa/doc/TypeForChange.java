package oripa.doc;

public enum TypeForChange{
	EMPTY("-"),
	RIDGE("M"), VALLEY("V"), AUX("Aux"), CUT("Cut"), 
	DELETE("Del"), FLIP("Flip");
	
	private String shortName;
	
	private TypeForChange(String shortName) {
		this.shortName = shortName;
	}
	
	@Override
	public String toString(){
		return shortName;
	}
}