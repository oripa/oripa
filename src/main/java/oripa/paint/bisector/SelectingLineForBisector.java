package oripa.paint.bisector;

import oripa.ORIPA;
import oripa.doc.Doc;
import oripa.paint.core.PaintContext;
import oripa.paint.core.PickingLine;
import oripa.paint.creasepattern.CreasePattern;
import oripa.paint.creasepattern.Painter;

public class SelectingLineForBisector extends PickingLine {

	@Override
	protected void initialize() {
		setPreviousClass(SelectingVertexForBisector.class);
		setNextClass(SelectingVertexForBisector.class);

	}

	
	
	@Override
	protected void undoAction(PaintContext context) {
		context.popVertex();
	
	}

	

	@Override
	protected void onResult(PaintContext context) {
		if(context.getLineCount() != 1 || 
				context.getVertexCount() != 3){
			throw new RuntimeException();
		}
		
		Doc document = ORIPA.doc;
		CreasePattern creasePattern = document.getCreasePattern();
		document.pushCachedUndoInfo();

		Painter painter = new Painter();
		
		painter.addBisectorLine(
        		context.getVertex(0), context.getVertex(1), context.getVertex(2), 
        		context.getLine(0),
        		creasePattern);

        context.clear(false);
	}

}
