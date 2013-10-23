package oripa.paint.deletevertex;

import java.util.Collection;

import oripa.ORIPA;
import oripa.doc.Doc;
import oripa.paint.PaintContextInterface;
import oripa.paint.core.PickingVertex;
import oripa.paint.creasepattern.Painter;
import oripa.value.OriLine;

public class DeletingVertex extends PickingVertex {

	@Override
	protected void initialize() {

	}

	@Override
	protected void onResult(PaintContextInterface context) {
		Doc document = ORIPA.doc;
		Collection<OriLine> creasePattern = document.getCreasePattern();

		if(context.getVertexCount() > 0){
			document.pushUndoInfo();

			Painter painter = new Painter();
			painter.removeVertex(context.popVertex(), creasePattern);

		}
		
		context.clear(false);
	}

}
