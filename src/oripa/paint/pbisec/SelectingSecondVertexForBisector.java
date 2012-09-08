package oripa.paint.pbisec;

import javax.vecmath.Vector2d;

import oripa.ORIPA;
import oripa.paint.PaintContext;
import oripa.paint.PickingVertex;

public class SelectingSecondVertexForBisector extends PickingVertex{
	
	public SelectingSecondVertexForBisector(){
		super();
	}
	
	@Override
	public void onResult(PaintContext context) {
		
		if(context.getVertexCount() != 2){
			throw new RuntimeException();
		}
		
        Vector2d p0, p1;
        p0 = context.getVertex(0);
        p1 = context.getVertex(1);

        ORIPA.doc.pushUndoInfo();
        ORIPA.doc.addPBisector(p0, p1);

        context.clear(false);
	}

	@Override
	protected void initialize() {
		setPreviousClass(SelectingFirstVertexForBisector.class);
		setNextClass(SelectingFirstVertexForBisector.class);
		
//		System.out.println("SelectingFirstVertex.initialize() is called");
	}
	
}
