package oripa.controller.paint.deletevertex;

import java.util.Collection;

import oripa.ORIPA;
import oripa.controller.paint.PaintContextInterface;
import oripa.controller.paint.core.PickingVertex;
import oripa.domain.cptool.Painter;
import oripa.persistent.doc.Doc;
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
