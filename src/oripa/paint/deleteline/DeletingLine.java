package oripa.paint.deleteline;

import oripa.ORIPA;
import oripa.paint.MouseContext;
import oripa.paint.PickingLine;

public class DeletingLine extends PickingLine {

	
	
	public DeletingLine() {
		super();
	}

	@Override
	protected void initialize() {
	}
	

	@Override
	protected void onResult(MouseContext context) {

		if(context.getLineCount() > 0){
			ORIPA.doc.pushUndoInfo();
			ORIPA.doc.removeLine(context.popLine());
		}
		
		context.clear(false);
	}

}
