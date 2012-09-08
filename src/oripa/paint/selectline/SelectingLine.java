package oripa.paint.selectline;

import java.awt.geom.Point2D;

import oripa.ORIPA;
import oripa.geom.OriLine;
import oripa.paint.PaintContext;
import oripa.paint.PickingLine;

public class SelectingLine extends PickingLine {

	
	
	public SelectingLine() {
		super();
	}

	@Override
	protected void initialize() {
	}

	
	
	
	@Override
	protected void undoAction(PaintContext context) {
		// TODO Auto-generated method stub
		super.undoAction(context);
	}

	@Override
	protected void onResult(PaintContext context) {
		
		ORIPA.doc.pushUndoInfo();
		
		
		OriLine line = context.peekLine();

		// toggle selection
		if(line.selected){
	    	line.selected = false;
	    	context.popLine();
	    	// line should be already stored.
	    	context.removeLine(line);
	    }
	    else {
	    	line.selected = true;
	    }

	}

}
