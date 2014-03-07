package oripa.controller.paint.deleteline;

import java.util.Collection;

import oripa.ORIPA;
import oripa.controller.paint.PaintContextInterface;
import oripa.controller.paint.core.PickingLine;
import oripa.domain.cptool.Painter;
import oripa.persistent.doc.Doc;
import oripa.value.OriLine;

public class DeletingLine extends PickingLine {

	
	
	public DeletingLine() {
		super();
	}

	@Override
	protected void initialize() {
	}
	

	@Override
	protected void onResult(PaintContextInterface context) {

		Doc document = ORIPA.doc;
		Collection<OriLine> creasePattern = document.getCreasePattern();

		if(context.getLineCount() > 0){
			document.pushUndoInfo();

			Painter painter = new Painter();
			painter.removeLine(context.popLine(), creasePattern);
		}
		
		context.clear(false);
	}

}
