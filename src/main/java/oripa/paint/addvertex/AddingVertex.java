package oripa.paint.addvertex;

import java.awt.geom.Point2D;

import oripa.ORIPA;
import oripa.doc.Doc;
import oripa.paint.core.PaintContext;
import oripa.paint.core.PickingVertex;
import oripa.paint.creasepattern.CreasePattern;
import oripa.paint.creasepattern.Painter;
import oripa.paint.geometry.GeometricOperation;
import oripa.value.OriLine;

public class AddingVertex extends PickingVertex {

	@Override
	protected void initialize() {

	}

	
	
	@Override
	protected boolean onAct(PaintContext context, Point2D.Double currentPoint,
			boolean freeSelection) {
		
		boolean result = super.onAct(context, currentPoint, true);
		
		if(result == true){
			OriLine line = GeometricOperation.pickLine(
					context);

			if(line != null){
				context.pushLine(line);
			}
			else {
				result = false;
			}
		}
		
		return result;
	}



	@Override
	protected void onResult(PaintContext context) {

		if(context.getVertexCount() > 0){
			
			Doc document = ORIPA.doc;
			document.pushUndoInfo();
			CreasePattern creasePattern = document.getCreasePattern();

			Painter painter = new Painter();
			
			if (!painter.addVertexOnLine(
					context.popLine(), context.popVertex(),
					creasePattern, creasePattern.getPaperSize())) {
				ORIPA.doc.loadUndoInfo();
			}

		}
		
		context.clear(false);
	}

}
