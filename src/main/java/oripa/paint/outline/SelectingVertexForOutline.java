package oripa.paint.outline;

import java.awt.geom.Point2D;
import java.util.Collection;

import javax.vecmath.Vector2d;

import oripa.ORIPA;
import oripa.geom.GeomUtil;
import oripa.paint.PaintContext;
import oripa.paint.PickingVertex;

public class SelectingVertexForOutline extends PickingVertex {

	
	@Override
	protected void initialize() {
		
	}
	
	
	

	@Override
	protected boolean onAct(PaintContext context, Point2D.Double currentPoint,
			boolean freeSelection) {
        context.setMissionCompleted(false);
		return super.onAct(context, currentPoint, freeSelection);
	}




	@Override
	protected void onResult(PaintContext context) {
		
		Vector2d v = context.popVertex();
		
        boolean bClose = false;
        for (Vector2d tv : context.getVertices()) {
            if (GeomUtil.Distance(v, tv) < 1) {
                bClose = true;
                break;
            }
        }

        if (bClose) {
            if (context.getVertexCount() > 2) {
            	// finish editing
            	
            	ORIPA.doc.pushUndoInfo();
                closeTmpOutline(context.getVertices());

                context.clear(false);
                context.setMissionCompleted(true);
            }
        } else {
        	// continue selecting
        	context.pushVertex(v);
        }
        

	}

	
	
    private void closeTmpOutline(Collection<Vector2d> outlineVertices) {

    	(new CloseTempOutline()).execute(outlineVertices);
    	
    }
    

    

}
