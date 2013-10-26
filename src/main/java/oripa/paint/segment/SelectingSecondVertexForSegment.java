package oripa.paint.segment;

import oripa.ORIPA;
import oripa.doc.Doc;
import oripa.paint.CreasePatternInterface;
import oripa.paint.PaintContextInterface;
import oripa.paint.core.PaintConfig;
import oripa.paint.core.PickingVertex;
import oripa.paint.cptool.Painter;
import oripa.value.OriLine;

public class SelectingSecondVertexForSegment extends PickingVertex{

		
	public SelectingSecondVertexForSegment(){
		super();
	}

	@Override
	protected void onResult(PaintContextInterface context) {
		
		if(context.getVertexCount() != 2){
			throw new RuntimeException();
		}
		
		OriLine line = new OriLine(context.getVertex(0),
				context.getVertex(1), PaintConfig.inputLineType);

		Doc document = ORIPA.doc;
		CreasePatternInterface creasePattern = document.getCreasePattern();

		document.pushUndoInfo();

		Painter painter = new Painter();
		painter.addLine(line, creasePattern);

        context.clear(false);
	}

	@Override
	protected void initialize() {
		setPreviousClass(SelectingFirstVertexForSegment.class);
		setNextClass(SelectingFirstVertexForSegment.class);

//		System.out.println("SelectingSecondVertex.initialize() is called");
	}
}	
