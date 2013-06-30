package oripa.doc;

import java.util.ArrayList;
import java.util.Collection;

import oripa.geom.OriLine;

public class UndoInfo {
    private ArrayList<OriLine> lines = new ArrayList<OriLine>();

    
    

	public UndoInfo(){}
    
    public UndoInfo(Collection<OriLine> lines){
    	setLines(lines);
    }
    
    public ArrayList<OriLine> getLines() {
		return lines;
	}

    public void setLines(Collection<OriLine> lines){
    	this.lines.clear();
        for (OriLine l : lines) {
            this.lines.add(new OriLine(l));
        }

    }
}
