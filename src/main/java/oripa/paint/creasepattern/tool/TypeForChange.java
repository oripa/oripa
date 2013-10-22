package oripa.paint.creasepattern.tool;

// TODO move to view layer, or integrate with LineTypes

public enum TypeForChange {
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