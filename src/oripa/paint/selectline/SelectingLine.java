package oripa.paint.selectline;

import java.awt.geom.Point2D;

import oripa.ORIPA;
import oripa.geom.OriLine;
import oripa.paint.MouseContext;
import oripa.paint.PickingLine;

public class SelectingLine extends PickingLine {

	
	
	public SelectingLine() {
		super();
	}

	@Override
	protected void initialize() {
	}

	
	private OriLine axis;
	private boolean doingFirstAction = true;
	
	/**
	 * This class keeps selecting line while {@value doSpecial} is false.
	 * When {@value doSpecial} is true, it executes mirror copy where the
	 * axis of mirror copy is the selected line.
	 * 
	 * @param doSpecial true if copy should be done.
	 * @return true if copy is done.
	 */
	@Override
	protected boolean onAct(MouseContext context, Point2D.Double currentPoint,
			boolean doSpecial) {

		boolean result = super.onAct(context, currentPoint, doSpecial);
		
		if(result == true){
				OriLine line = context.peekLine();

			if(line.selected){
            	line.selected = false;
            	context.popLine();
            	context.removeLine(line);
            }
            else {
            	line.selected = true;
            }
		}
		

		return result;
	}

	
	
	@Override
	protected void undoAction(MouseContext context) {
		// TODO Auto-generated method stub
		super.undoAction(context);
	}

	@Override
	protected void onResult(MouseContext context) {
		// TODO Auto-generated method stub

	}

}
