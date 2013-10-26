package oripa.paint.triangle;

import java.awt.geom.Point2D.Double;
import java.util.Collection;

import oripa.ORIPA;
import oripa.doc.Doc;
import oripa.paint.PaintContextInterface;
import oripa.paint.core.PickingVertex;
import oripa.paint.cptool.Painter;
import oripa.value.OriLine;

public class SelectingVertexForTriangleSplit extends PickingVertex{
	
	public SelectingVertexForTriangleSplit(){
		super();
	}
	
	@Override
	protected void initialize() {
	}


	private boolean doingFirstAction = true;
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
		
		return result;
	}

	@Override
	public void onResult(PaintContextInterface context) {
		Doc document = ORIPA.doc;
		Collection<OriLine> creasePattern = document.getCreasePattern();
		
		document.pushCachedUndoInfo();

		Painter painter = new Painter();
		painter.addTriangleDivideLines(
				context.getVertex(0), context.getVertex(1), context.getVertex(2),
				creasePattern);

        doingFirstAction = true;
        context.clear(false);
	}

	
}
