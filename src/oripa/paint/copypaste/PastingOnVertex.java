package oripa.paint.copypaste;

import java.util.Collection;

import javax.vecmath.Vector2d;

import oripa.ORIPA;
import oripa.geom.OriLine;
import oripa.paint.GeometricalOperation;
import oripa.paint.MouseContext;
import oripa.paint.PickingVertex;

public class PastingOnVertex extends PickingVertex {

	@Override
	protected void initialize() {
	}

	
	
	@Override
	protected void undoAction(MouseContext context) {
		ORIPA.doc.loadUndoInfo();
	}



	@Override
	protected void onResult(MouseContext context) {

        Vector2d v = context.popVertex();
        
        if (context.getLineCount() > 0) {

        	ORIPA.doc.pushUndoInfo();

            double ox = context.getLine(0).p0.x;
            double oy = context.getLine(0).p0.y;

            Collection<OriLine> shiftedLines = 
            		GeometricalOperation.shiftLines(context.getLines(), v.x - ox, v.y -oy);
            
            for(OriLine line : shiftedLines){
            	ORIPA.doc.addLine(line);
            }
        }
		
	}

}
