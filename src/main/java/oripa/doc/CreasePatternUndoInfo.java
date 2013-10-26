package oripa.doc;

import java.util.ArrayList;
import java.util.Collection;

import oripa.util.history.UndoInfo;
import oripa.value.OriLine;

public class CreasePatternUndoInfo implements UndoInfo<Collection<OriLine>> {

	private ArrayList<OriLine> lines = new ArrayList<>();
	public CreasePatternUndoInfo(){}
    
    public CreasePatternUndoInfo(Collection<OriLine> lines){
    	setInfo(lines);
    }

    @Override
    public java.util.Collection<OriLine> getInfo() {
    	return lines;
    }
    
    @Override
    public void setInfo(Collection<OriLine> lines) {
		this.lines = new ArrayList<>(lines.size());
	    for (OriLine l : lines) {
	    	this.lines.add(new OriLine(l));
	    }
	
	}
}
