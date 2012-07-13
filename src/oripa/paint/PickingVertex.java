package oripa.paint;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

import javax.vecmath.Vector2d;

import oripa.paint.ActionState;

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
	 * @return Next state if vertex is found, else itself.
	 */
	
	@Override
	protected boolean onAct(MouseContext context, Double currentPoint,
			boolean freeSelection) {

		Vector2d picked = GeometricalOperation.pickVertex(
				context, currentPoint, freeSelection);

		if(picked == null){
			System.out.println("onAct() failed");
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
	protected void undoAction(MouseContext context) {
		
		if(context.getVertexCount() > 0){
			context.popVertex();
		}
		
	}

	
}
