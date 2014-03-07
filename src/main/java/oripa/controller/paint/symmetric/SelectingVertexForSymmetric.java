package oripa.controller.paint.symmetric;

import java.awt.geom.Point2D.Double;
import java.util.Collection;

import javax.vecmath.Vector2d;

import oripa.ORIPA;
import oripa.controller.paint.PaintContextInterface;
import oripa.controller.paint.core.PickingVertex;
import oripa.domain.cptool.Painter;
import oripa.persistent.doc.Doc;
import oripa.value.OriLine;

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
	protected boolean onAct(PaintContextInterface context, Double currentPoint,
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
	public void onResult(PaintContextInterface context) {
		Doc document = ORIPA.doc;
		Collection<OriLine> creasePattern = document.getCreasePattern();

		document.pushCachedUndoInfo();
		
		Vector2d first = context.getVertex(0);
		Vector2d second = context.getVertex(1);
		Vector2d third = context.getVertex(2);
		
		Painter painter = new Painter();

		if (doSpecial) {
			painter.addSymmetricLineAutoWalk(
					first, second, third, first, creasePattern);
		} else {
			painter.addSymmetricLine(
					first, second, third, creasePattern);
		}

		doingFirstAction = true;
        context.clear(false);
	}

	
}
