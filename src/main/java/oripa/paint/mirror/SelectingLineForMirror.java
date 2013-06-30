package oripa.paint.mirror;

import java.awt.geom.Point2D;

import oripa.ORIPA;
import oripa.geom.OriLine;
import oripa.paint.PaintContext;
import oripa.paint.PickingLine;

public class SelectingLineForMirror extends PickingLine {

	
	
	public SelectingLineForMirror() {
		super();
	}

	@Override
	protected void initialize() {
	}

	
	private OriLine axis;
	private boolean doingFirstAction = true;
	
	/**
	 * This class keeps selecting line while {@code doSpecial} is false.
	 * When {@value doSpecial} is true, it executes mirror copy where the
	 * axis of mirror copy is the selected line.
	 * 
	 * @param doSpecial true if copy should be done.
	 * @return true if copy is done.
	 */
	@Override
	protected boolean onAct(PaintContext context, Point2D.Double currentPoint,
			boolean doSpecial) {
		if(doingFirstAction){
			doingFirstAction = false;
			ORIPA.doc.cacheUndoInfo();
			
		}

		boolean result = super.onAct(context, currentPoint, doSpecial);
		
		if(result == true){
			if(doSpecial){
				axis = context.popLine();
				result = true;
            } 
			else {
				OriLine line = context.peekLine();

				if(line.selected){
                	line.selected = false;
                	context.popLine();
                	context.removeLine(line);
                }
                else {
                	line.selected = true;
                }

                result = false;
            }
		}
		

		return result;
	}

	
	
	@Override
	protected void undoAction(PaintContext context) {
		// TODO Auto-generated method stub
		super.undoAction(context);
	}

	@Override
	protected void onResult(PaintContext context) {
		// TODO Auto-generated method stub
        ORIPA.doc.pushCachedUndoInfo();

		ORIPA.doc.mirrorCopyBy(axis, context.getLines());

        doingFirstAction = true;
        context.clear(true);
	}

}
