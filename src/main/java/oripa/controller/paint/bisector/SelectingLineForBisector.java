package oripa.controller.paint.bisector;

import java.util.List;

import javax.vecmath.Vector2d;

import oripa.ORIPA;
import oripa.controller.paint.PaintContextInterface;
import oripa.controller.paint.core.PickingLine;
import oripa.domain.cptool.Painter;
import oripa.domain.creasepattern.CreasePatternInterface;
import oripa.persistent.doc.Doc;

public class SelectingLineForBisector extends PickingLine {

	@Override
	protected void initialize() {
		setPreviousClass(SelectingVertexForBisector.class);
		setNextClass(SelectingVertexForBisector.class);

	}

	
	
	@Override
	protected void undoAction(PaintContextInterface context) {
		context.popVertex();
	
	}

	

	@Override
	protected void onResult(PaintContextInterface context) {
		if(context.getLineCount() != 1 || 
				context.getVertexCount() != 3){
			throw new RuntimeException();
		}
		
		Doc document = ORIPA.doc;
		CreasePatternInterface creasePattern = document.getCreasePattern();
		document.pushCachedUndoInfo();

		Painter painter = new Painter();
		
		List<Vector2d> vertices = context.getPickedVertices();
		painter.addBisectorLine(
        		context.getVertex(0), context.getVertex(1), context.getVertex(2), 
        		context.getLine(0),
        		creasePattern);

        context.clear(false);
	}

}
