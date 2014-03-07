package oripa.controller.paint.selectline;

import oripa.ORIPA;
import oripa.controller.paint.PaintContextInterface;
import oripa.controller.paint.core.PickingLine;
import oripa.value.OriLine;

public class SelectingLine extends PickingLine {

	
	
	public SelectingLine() {
		super();
	}

	@Override
	protected void initialize() {
	}

	
	
	
	@Override
	protected void undoAction(PaintContextInterface context) {
		// TODO Auto-generated method stub
		super.undoAction(context);
	}

	@Override
	protected void onResult(PaintContextInterface context) {
		
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
