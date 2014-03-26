package oripa.domain.paint.core;

import java.awt.geom.Point2D.Double;

import javax.vecmath.Vector2d;

import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.geometry.NearestItemFinder;

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

		Vector2d picked = NearestItemFinder.pickVertex(
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
