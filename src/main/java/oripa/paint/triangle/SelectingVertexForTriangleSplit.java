package oripa.paint.triangle;

import java.awt.geom.Point2D.Double;

import oripa.ORIPA;
import oripa.paint.PaintContext;
import oripa.paint.PickingVertex;

public class SelectingVertexForTriangleSplit extends PickingVertex{
	
	public SelectingVertexForTriangleSplit(){
		super();
	}
	
	@Override
	protected void initialize() {
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
		ORIPA.doc.pushCachedUndoInfo();
		
        ORIPA.doc.addTriangleDivideLines(context.getVertex(0),
        		context.getVertex(1), context.getVertex(2));

        doingFirstAction = true;
        context.clear(false);
	}

	
}
