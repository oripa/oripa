package oripa.paint.addvertex;

import java.awt.geom.Point2D;

import oripa.ORIPA;
import oripa.geom.OriLine;
import oripa.paint.PaintContext;
import oripa.paint.PickingVertex;
import oripa.paint.geometry.GeometricOperation;

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
			
//            Object[] line_vertex = new Object[2];
//            if (pickPointOnLine(context.mousePoint, line_vertex)) {
//                ORIPA.doc.pushUndoInfo();
//                if (!ORIPA.doc.addVertexOnLine((OriLine) line_vertex[0], (Vector2d) line_vertex[1])) {
//                    ORIPA.doc.loadUndoInfo();
//                }
//            }
          ORIPA.doc.pushUndoInfo();
          if (!ORIPA.doc.addVertexOnLine(context.popLine(), context.popVertex())) {
              ORIPA.doc.loadUndoInfo();
          }

		}
		
		context.clear(false);
	}

}
