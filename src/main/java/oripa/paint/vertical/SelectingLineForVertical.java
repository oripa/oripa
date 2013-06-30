package oripa.paint.vertical;

import oripa.ORIPA;
import oripa.geom.GeomUtil;
import oripa.geom.OriLine;
import oripa.paint.Globals;
import oripa.paint.PaintContext;
import oripa.paint.PickingLine;

public class SelectingLineForVertical extends PickingLine {

	@Override
	protected void initialize() {
		setPreviousClass(SelectingVertexForVertical.class);
		setNextClass(SelectingVertexForVertical.class);

	}

	
	
	@Override
	protected void undoAction(PaintContext context) {
		context.clear(false);
	
	}

	

	@Override
	protected void onResult(PaintContext context) {
		if(context.getLineCount() != 1 || 
				context.getVertexCount() != 1){
			throw new RuntimeException();
		}
		
        OriLine vl = GeomUtil.getVerticalLine(
        		context.getVertex(0), context.getLine(0), Globals.inputLineType);

        ORIPA.doc.pushUndoInfo();
        ORIPA.doc.addLine(vl);

        context.clear(false);
	}

}
