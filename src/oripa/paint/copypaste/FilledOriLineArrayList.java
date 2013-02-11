package oripa.paint.copypaste;

import java.util.ArrayList;
import java.util.Collection;

import oripa.geom.OriLine;

public class FilledOriLineArrayList extends ArrayList<OriLine>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6534311326142954566L;
	
	
	public FilledOriLineArrayList(int size) {
		super(size);
		for(int i = 0; i < size; i++){
			add(new OriLine());
		}
	}
	
	public FilledOriLineArrayList(Collection<OriLine> lines) {
		super(lines.size());
		for(OriLine line : lines){
			add(new OriLine(line));
		}
	}
	
}
