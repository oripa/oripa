package oripa.domain.cptool;

import oripa.value.OriLine;

public enum TypeForChange {
	EMPTY("Any", null),
	MOUNTAIN("M", OriLine.Type.MOUNTAIN), VALLEY("V", OriLine.Type.VALLEY),
	AUX("Aux", OriLine.Type.AUX), CUT("Cut", OriLine.Type.CUT), 
	DELETE("Del", null), FLIP("Flip", null);
	
	private String shortName;
	private OriLine.Type oriType;
	
	private TypeForChange(String shortName, OriLine.Type oriType) {
		this.shortName = shortName;
		this.oriType = oriType;
	}
	
	public OriLine.Type getOriLineType() {
		return oriType;
	}
	
	@Override
	public String toString(){
		return shortName;
	}
}