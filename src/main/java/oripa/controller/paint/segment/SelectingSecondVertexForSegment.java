package oripa.controller.paint.segment;

import oripa.ORIPA;
import oripa.controller.paint.PaintContextInterface;
import oripa.controller.paint.core.PaintConfig;
import oripa.controller.paint.core.PickingVertex;
import oripa.domain.cptool.Painter;
import oripa.domain.creasepattern.CreasePatternInterface;
import oripa.persistent.doc.Doc;
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
