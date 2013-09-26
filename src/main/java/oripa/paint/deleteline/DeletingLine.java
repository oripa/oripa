package oripa.paint.deleteline;

import oripa.ORIPA;
import oripa.paint.core.PaintContext;
import oripa.paint.core.PickingLine;

public class DeletingLine extends PickingLine {

	
	
	public DeletingLine() {
		super();
	}

	@Override
	protected void initialize() {
	}
	

	@Override
	protected void onResult(PaintContext context) {

		if(context.getLineCount() > 0){
			ORIPA.doc.pushUndoInfo();
			ORIPA.doc.removeLine(context.popLine());
		}
		
		context.clear(false);
	}

}
