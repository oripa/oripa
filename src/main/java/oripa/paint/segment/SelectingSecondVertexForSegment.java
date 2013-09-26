package oripa.paint.segment;

import oripa.ORIPA;
import oripa.paint.core.Globals;
import oripa.paint.core.PaintContext;
import oripa.paint.core.PickingVertex;
import oripa.value.OriLine;

public class SelectingSecondVertexForSegment extends PickingVertex{

		
	public SelectingSecondVertexForSegment(){
		super();
	}

	@Override
	protected void onResult(PaintContext context) {
		
		if(context.getVertexCount() != 2){
			throw new RuntimeException();
		}
		
		OriLine line = new OriLine(context.getVertex(0),
				context.getVertex(1), Globals.inputLineType);

		ORIPA.doc.pushUndoInfo();
        ORIPA.doc.addLine(line);

        context.clear(false);
	}

	@Override
	protected void initialize() {
		setPreviousClass(SelectingFirstVertexForSegment.class);
		setNextClass(SelectingFirstVertexForSegment.class);

//		System.out.println("SelectingSecondVertex.initialize() is called");
	}
}	
