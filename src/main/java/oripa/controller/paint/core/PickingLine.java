package oripa.controller.paint.core;

import java.awt.geom.Point2D.Double;

import oripa.controller.paint.PaintContextInterface;
import oripa.controller.paint.geometry.NearestVertexFinder;
import oripa.value.OriLine;

/**
 * abstract class specified for picking vertex.
 * @author koji
 *
 */
public abstract class PickingLine extends AbstractActionState {
	

	public PickingLine(){
		super();
	}
	
	/**
	 * Picks the nearest line and push it into context.
	 * @return true if the action succeed, false otherwise.
	 */
	
	@Override
	protected boolean onAct(PaintContextInterface context, Double currentPoint,
			boolean doSpecial) {

		OriLine picked = NearestVertexFinder.pickLine(
				currentPoint, context.getScale());

		if(picked == null){
			System.out.println("onAct() failed");
			return false;
		}
		
		context.pushLine(picked);		
		
		
		return true;
	}
	
	
	/**
	 * delete from context the latest picked line.
	 * @return Previous state
	 */
	@Override
	protected void undoAction(PaintContextInterface context) {
		context.popLine();		
	}

	
}
