package oripa.paint.vertical;

import oripa.Globals;
import oripa.ORIPA;
import oripa.geom.GeomUtil;
import oripa.geom.OriLine;
import oripa.paint.MouseContext;
import oripa.paint.PickingLine;

public class SelectingLineForVertical extends PickingLine {

	@Override
	protected void initialize() {
		setPreviousClass(SelectingVertexForVertical.class);
		setNextClass(SelectingVertexForVertical.class);

	}

	
	
	@Override
	protected void undoAction(MouseContext context) {
		context.clear();
	
	}

	

	@Override
	protected void onResult(MouseContext context) {
		if(context.getLineCount() != 1 || 
				context.getVertexCount() != 1){
			throw new RuntimeException();
		}
		
        OriLine vl = GeomUtil.getVerticalLine(
        		context.getVertex(0), context.getLine(0), Globals.inputLineType);

        ORIPA.doc.pushUndoInfo();
        ORIPA.doc.addLine(vl);

        context.clear();
	}

}
