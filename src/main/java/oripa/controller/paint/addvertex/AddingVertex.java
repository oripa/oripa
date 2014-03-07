package oripa.controller.paint.addvertex;

import java.awt.geom.Point2D;

import oripa.ORIPA;
import oripa.controller.paint.PaintContextInterface;
import oripa.controller.paint.core.PickingVertex;
import oripa.controller.paint.geometry.NearestVertexFinder;
import oripa.domain.cptool.Painter;
import oripa.domain.creasepattern.CreasePatternInterface;
import oripa.persistent.doc.Doc;
import oripa.value.OriLine;

public class AddingVertex extends PickingVertex {

	@Override
	protected void initialize() {

	}

	
	
	@Override
	protected boolean onAct(PaintContextInterface context, Point2D.Double currentPoint,
			boolean freeSelection) {
		
		boolean result = super.onAct(context, currentPoint, true);
		
		if(result == true){
			OriLine line = NearestVertexFinder.pickLine(
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
	protected void onResult(PaintContextInterface context) {

		if(context.getVertexCount() > 0){
			
			Doc document = ORIPA.doc;
			document.pushUndoInfo();
			CreasePatternInterface creasePattern = document.getCreasePattern();

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
