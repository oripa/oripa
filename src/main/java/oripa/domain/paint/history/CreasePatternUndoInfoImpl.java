package oripa.domain.paint.history;

import java.util.ArrayList;
import java.util.Collection;

import oripa.util.history.UndoInfo;
import oripa.value.OriLine;

class CreasePatternUndoInfoImpl implements UndoInfo<Collection<OriLine>> {

	private ArrayList<OriLine> lines = new ArrayList<>();

	@SuppressWarnings("unused")
	private CreasePatternUndoInfoImpl(){}

    public CreasePatternUndoInfoImpl(Collection<OriLine> lines){
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
