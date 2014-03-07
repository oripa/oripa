package oripa.controller.paint.vertical;

import oripa.ORIPA;
import oripa.controller.paint.PaintContextInterface;
import oripa.controller.paint.core.PaintConfig;
import oripa.controller.paint.core.PickingLine;
import oripa.domain.cptool.Painter;
import oripa.domain.creasepattern.CreasePatternInterface;
import oripa.geom.GeomUtil;
import oripa.persistent.doc.Doc;
import oripa.value.OriLine;

public class SelectingLineForVertical extends PickingLine {

	@Override
	protected void initialize() {
		setPreviousClass(SelectingVertexForVertical.class);
		setNextClass(SelectingVertexForVertical.class);

	}

	
	
	@Override
	protected void undoAction(PaintContextInterface context) {
		context.clear(false);
	
	}

	

	@Override
	protected void onResult(PaintContextInterface context) {
		if(context.getLineCount() != 1 || 
				context.getVertexCount() != 1){
			throw new RuntimeException();
		}
		
        OriLine vl = GeomUtil.getVerticalLine(
        		context.getVertex(0), context.getLine(0), PaintConfig.inputLineType);

        Doc document = ORIPA.doc;
		CreasePatternInterface creasePattern = document.getCreasePattern();

        document.pushUndoInfo();

        Painter painter = new Painter();
        painter.addLine(vl, creasePattern);

        context.clear(false);
	}

}
