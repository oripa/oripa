package oripa.doc;

import java.util.ArrayList;
import java.util.Collection;

import oripa.value.OriLine;

public class UndoInfo {
    private final ArrayList<OriLine> lines = new ArrayList<>();

    
    

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
