package oripa.paint.bisector;

import oripa.ORIPA;
import oripa.paint.PaintContext;
import oripa.paint.PickingLine;

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
		
        ORIPA.doc.pushCachedUndoInfo();

        ORIPA.doc.addBisectorLine(
        		context.getVertex(0), context.getVertex(1), context.getVertex(2), 
        		context.getLine(0));

        context.clear(false);
	}

}
