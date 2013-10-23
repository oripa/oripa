package oripa.paint.core;

import java.awt.geom.Point2D.Double;

import javax.vecmath.Vector2d;

import oripa.paint.PaintContextInterface;
import oripa.paint.geometry.NearestVertexFinder;

/**
 * abstract class specified for picking vertex.
 * @author koji
 *
 */
public abstract class PickingVertex extends AbstractActionState {
	

	public PickingVertex(){
		super();
	}
	
	/**
	 * Picks the nearest vertex and push it into context.
	 * @return true if the action succeed, false otherwise.
	 */
	
	@Override
	protected boolean onAct(PaintContextInterface context, Double currentPoint,
			boolean freeSelection) {

		Vector2d picked = NearestVertexFinder.pickVertex(
				context, freeSelection);

		if(picked == null){
			return false;
		}
		
		context.pushVertex(picked);		
		
		return true;
	}
	
	
	/**
	 * delete from context the latest picked vertex.
	 * @return Previous state
	 */
	@Override
	protected void undoAction(PaintContextInterface context) {
		
		if(context.getVertexCount() > 0){
			context.popVertex();
		}
		
	}

	
}
