package oripa.paint.copypaste;

import java.awt.geom.Point2D.Double;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Vector2d;

import oripa.ORIPA;
import oripa.doc.Doc;
import oripa.paint.PaintContextInterface;
import oripa.paint.core.PickingVertex;
import oripa.paint.creasepattern.CreasePattern;
import oripa.paint.creasepattern.Painter;
import oripa.value.OriLine;

public class PastingOnVertex extends PickingVertex {


	@Override
	protected void initialize() {
	}

	
	
	@Override
	protected void undoAction(PaintContextInterface context) {
		context.setMissionCompleted(false);
		ORIPA.doc.loadUndoInfo();
	}


	
	

	@Override
	protected boolean onAct(PaintContextInterface context, Double currentPoint,
			boolean freeSelection) {
		
		Vector2d candidate = context.getPickCandidateV();
		if(candidate == null){
			return false;
		}
		
		context.pushVertex(candidate);
		
		return true;
	}



	@Override
	protected void onResult(PaintContextInterface context) {

        Vector2d v = context.popVertex();
        
        if (context.getLineCount() > 0) {
        	Doc document = ORIPA.doc;
        	CreasePattern creasePattern = document.getCreasePattern();
        	document.pushUndoInfo();

        	Vector2d origin = OriginHolder.getInstance().getOrigin(context);

        	double ox = origin.x;
            double oy = origin.y;


            List<OriLine> shiftedLines;
        	shiftedLines = 
        			shiftLines(
        					context.getLines(), v.x - ox, v.y -oy);
        	
//            for(int i = 0; i < context.getLineCount(); i++){
//            	ORIPA.doc.addLine(shiftedLines.get(i));
//            }

        	Painter painter = new Painter();
        	painter.pasteLines(shiftedLines, creasePattern);
            
            context.setMissionCompleted(true);
        }
		
	}

	private List<OriLine> shiftLines(Collection<OriLine> lines, 
			double diffX, double diffY){

		List<OriLine> shiftedLines = new LinkedList<>();
		
		int i = 0;
		for (OriLine l : lines) {
			OriLine shifted = new OriLine();

			shifted.p0.x = l.p0.x + diffX;
			shifted.p0.y = l.p0.y + diffY;

			shifted.p1.x = l.p1.x + diffX;
			shifted.p1.y = l.p1.y + diffY;

			shifted.typeVal = l.typeVal;
		
			shiftedLines.add(shifted);
		}

		return shiftedLines;
	}

}
