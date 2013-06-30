package oripa.paint.bisector;

import java.awt.geom.Point2D.Double;

import oripa.ORIPA;
import oripa.paint.PaintContext;
import oripa.paint.PickingVertex;

public class SelectingVertexForBisector extends PickingVertex{
	
	public SelectingVertexForBisector(){
		super();
	}
	
	@Override
	protected void initialize() {
		setPreviousClass(this.getClass());
		setNextClass(SelectingLineForBisector.class);
		
//		System.out.println("SelectingFirstVertex.initialize() is called");
	}


	private boolean doingFirstAction = true;
	@Override
	protected boolean onAct(PaintContext context, Double currentPoint,
			boolean doSpecial) {
		
		if(doingFirstAction){
			ORIPA.doc.cacheUndoInfo();
			doingFirstAction = false;
		}
		
		boolean result = super.onAct(context, currentPoint, doSpecial);
		
		if(result == true){
			if(context.getVertexCount() < 3){
				result = false;
			}
		}
		
		return result;
	}

	@Override
	public void onResult(PaintContext context) {
		
	}

	
}
