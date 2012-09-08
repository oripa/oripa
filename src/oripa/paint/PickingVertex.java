package oripa.paint;

import java.awt.geom.Point2D.Double;

import javax.vecmath.Vector2d;

import oripa.paint.geometry.GeometricOperation;

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
	protected boolean onAct(PaintContext context, Double currentPoint,
			boolean freeSelection) {

		Vector2d picked = GeometricOperation.pickVertex(
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
	protected void undoAction(PaintContext context) {
		
		if(context.getVertexCount() > 0){
			context.popVertex();
		}
		
	}

	
}
