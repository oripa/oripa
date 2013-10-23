package oripa.paint.deleteline;

import java.util.Collection;

import oripa.ORIPA;
import oripa.doc.Doc;
import oripa.paint.PaintContextInterface;
import oripa.paint.core.PickingLine;
import oripa.paint.creasepattern.Painter;
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
