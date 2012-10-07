package oripa.paint.copypaste;

import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.Collection;

import javax.vecmath.Vector2d;

import oripa.ORIPA;
import oripa.geom.OriLine;
import oripa.paint.PaintContext;
import oripa.paint.PickingVertex;
import oripa.paint.geometry.GeometricOperation;

public class PastingOnVertex extends PickingVertex {

	private FilledOriLineArrayList shiftedLines;

	@Override
	protected void initialize() {
	}

	
	
	@Override
	protected void undoAction(PaintContext context) {
		context.setMissionCompleted(false);
		ORIPA.doc.loadUndoInfo();
	}


	
	

	@Override
	protected boolean onAct(PaintContext context, Double currentPoint,
			boolean freeSelection) {
		if(context.pickCandidateV == null){
			return false;
		}
		
		context.pushVertex(context.pickCandidateV);
		
		return true;
	}



	@Override
	protected void onResult(PaintContext context) {

        Vector2d v = context.popVertex();
        
        if (context.getLineCount() > 0) {

        	ORIPA.doc.pushUndoInfo();

        	Vector2d origin = OriginHolder.getInstance().getOrigin(context);

        	double ox = origin.x;
            double oy = origin.y;


            shiftedLines = new FilledOriLineArrayList(context.getLineCount());
        	GeometricOperation.shiftLines(context.getLines(), shiftedLines, v.x - ox, v.y -oy);
            
//            for(int i = 0; i < context.getLineCount(); i++){
//            	ORIPA.doc.addLine(shiftedLines.get(i));
//            }
        	
        	ORIPA.doc.pasteLines(shiftedLines);
            
            context.setMissionCompleted(true);
        }
		
	}

}
