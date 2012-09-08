package oripa.paint.copypaste;

import java.util.Collection;

import javax.vecmath.Vector2d;

import oripa.ORIPA;
import oripa.geom.OriLine;
import oripa.paint.PaintContext;
import oripa.paint.PickingVertex;
import oripa.paint.geometry.GeometricOperation;

public class PastingOnVertex extends PickingVertex {

	@Override
	protected void initialize() {
	}

	
	
	@Override
	protected void undoAction(PaintContext context) {
		ORIPA.doc.loadUndoInfo();
	}



	@Override
	protected void onResult(PaintContext context) {

        Vector2d v = context.popVertex();
        
        if (context.getLineCount() > 0) {

        	ORIPA.doc.pushUndoInfo();

        	Vector2d origin = OriginHolder.getInstance().getOrigin(context);

        	double ox = origin.x;
            double oy = origin.y;

            Collection<OriLine> shiftedLines = 
            		GeometricOperation.shiftLines(context.getLines(), v.x - ox, v.y -oy);
            
            for(OriLine line : shiftedLines){
            	ORIPA.doc.addLine(line);
            }
        }
		
	}

}
