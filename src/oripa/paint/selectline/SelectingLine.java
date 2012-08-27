package oripa.paint.selectline;

import java.awt.geom.Point2D;

import oripa.ORIPA;
import oripa.geom.OriLine;
import oripa.paint.MouseContext;
import oripa.paint.PickingLine;

public class SelectingLine extends PickingLine {

	
	
	public SelectingLine() {
		super();
	}

	@Override
	protected void initialize() {
	}

	
	
	
	@Override
	protected void undoAction(MouseContext context) {
		// TODO Auto-generated method stub
		super.undoAction(context);
	}

	@Override
	protected void onResult(MouseContext context) {
		
		ORIPA.doc.pushUndoInfo();
		
		
		OriLine line = context.peekLine();

		if(line.selected){
	    	line.selected = false;
	    	context.popLine();
	    	context.removeLine(line);
	    }
	    else {
	    	line.selected = true;
	    }

	}

}
