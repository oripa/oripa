package oripa.paint;

import java.awt.geom.Point2D.Double;

import oripa.geom.OriLine;

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
	protected boolean onAct(MouseContext context, Double currentPoint,
			boolean doSpecial) {

		OriLine picked = GeometricalOperation.pickLine(
				currentPoint, context.scale);

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
	protected void undoAction(MouseContext context) {
		
		context.popLine();		
	}

	
}
