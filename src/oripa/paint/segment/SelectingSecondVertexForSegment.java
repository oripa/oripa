package oripa.paint.segment;

import javax.management.RuntimeErrorException;

import oripa.Globals;
import oripa.ORIPA;
import oripa.geom.OriLine;
import oripa.paint.MouseContext;
import oripa.paint.PickingVertex;

public class SelectingSecondVertexForSegment extends PickingVertex{

		
	public SelectingSecondVertexForSegment(){
		super();
	}

	@Override
	protected void onResult(MouseContext context) {
		
		if(context.getVertexCount() != 2){
			throw new RuntimeException();
		}
		
		OriLine line = new OriLine(context.getVertex(0),
				context.getVertex(1), Globals.inputLineType);

		ORIPA.doc.pushUndoInfo();
        ORIPA.doc.addLine(line);

        context.clear();
	}

	@Override
	protected void initialize() {
		setPreviousClass(SelectingFirstVertexForSegment.class);
		setNextClass(SelectingFirstVertexForSegment.class);

		System.out.println("SelectingSecondVertex.initialize() is called");
	}
}	
