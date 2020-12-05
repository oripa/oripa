package oripa.domain.cptool;

public enum TypeForChange {
	EMPTY("-"),
	MOUNTAIN("M"), VALLEY("V"), AUX("Aux"), CUT("Cut"), 
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