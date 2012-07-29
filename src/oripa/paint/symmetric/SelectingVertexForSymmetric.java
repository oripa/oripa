package oripa.paint.symmetric;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D.Double;

import javax.vecmath.Vector2d;

import oripa.Doc;
import oripa.ORIPA;
import oripa.UndoInfo;
import oripa.paint.MouseContext;
import oripa.paint.PickingVertex;

public class SelectingVertexForSymmetric extends PickingVertex{
	
	public SelectingVertexForSymmetric(){
		super();
	}
	
	@Override
	protected void initialize() {
	}


	private boolean doingFirstAction = true;
	
	private boolean doSpecial = false;
	
	@Override
	protected boolean onAct(MouseContext context, Double currentPoint,
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

		this.doSpecial = doSpecial;
		
		return result;
	}

	@Override
	public void onResult(MouseContext context) {
		ORIPA.doc.pushCachedUndoInfo();
		
		Vector2d first = context.getVertex(0);
		Vector2d second = context.getVertex(1);
		Vector2d third = context.getVertex(2);
		
        if (doSpecial) {
            ORIPA.doc.addSymmetricLineAutoWalk(
            		first, second, third, 0, first);
        } else {
            ORIPA.doc.addSymmetricLine(first, second, third);
        }

        doingFirstAction = true;
        context.clear();
	}

	
}
