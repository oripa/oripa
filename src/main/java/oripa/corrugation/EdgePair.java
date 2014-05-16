package oripa.corrugation;

import oripa.fold.OriEdge;

public class EdgePair {

	private OriEdge first;
	private OriEdge second;
	
	public EdgePair(OriEdge first, OriEdge second) {
		this.first = first;
		this.second = second;
	}

	public OriEdge first() {
		return this.first;
	}
	public OriEdge second() {
		return this.second;
	}
}
