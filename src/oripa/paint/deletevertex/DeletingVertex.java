package oripa.paint.deletevertex;

import oripa.ORIPA;
import oripa.paint.MouseContext;
import oripa.paint.PickingVertex;

public class DeletingVertex extends PickingVertex {

	@Override
	protected void initialize() {

	}

	@Override
	protected void onResult(MouseContext context) {

		if(context.getVertexCount() > 0){
			ORIPA.doc.pushUndoInfo();
			
			ORIPA.doc.removeVertex(context.popVertex());

		}
		
		context.clear(false);
	}

}
