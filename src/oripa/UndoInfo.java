package oripa;

import java.util.ArrayList;
import java.util.Collection;

import oripa.geom.OriLine;

public class UndoInfo {
    private ArrayList<OriLine> lines = new ArrayList<OriLine>();

    
    
    public ArrayList<OriLine> getLines() {
		return lines;
	}

	public UndoInfo(){}
    
    public UndoInfo(Collection<OriLine> lines){
    	setLines(lines);
    }
    
    public void setLines(Collection<OriLine> lines){
        for (OriLine l : lines) {
            this.lines.add(new OriLine(l));
        }

    }
}
