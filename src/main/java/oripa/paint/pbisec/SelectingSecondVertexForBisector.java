package oripa.paint.pbisec;

import javax.vecmath.Vector2d;

import oripa.ORIPA;
import oripa.doc.Doc;
import oripa.paint.PaintContextInterface;
import oripa.paint.core.PickingVertex;
import oripa.paint.creasepattern.CreasePattern;
import oripa.paint.creasepattern.Painter;

public class SelectingSecondVertexForBisector extends PickingVertex{
	
	public SelectingSecondVertexForBisector(){
		super();
	}
	
	@Override
	public void onResult(PaintContextInterface context) {
		
		if(context.getVertexCount() != 2){
			throw new RuntimeException();
		}
		
		Vector2d p0, p1;
		p0 = context.getVertex(0);
		p1 = context.getVertex(1);
		
		Doc document = ORIPA.doc;
		CreasePattern creasePattern = document.getCreasePattern();

        document.pushUndoInfo();

        Painter painter = new Painter();
        painter.addPBisector(
        		p0, p1,
        		creasePattern, creasePattern.getPaperSize());

        context.clear(false);
	}

	@Override
	protected void initialize() {
		setPreviousClass(SelectingFirstVertexForBisector.class);
		setNextClass(SelectingFirstVertexForBisector.class);
		
//		System.out.println("SelectingFirstVertex.initialize() is called");
	}
	
}
