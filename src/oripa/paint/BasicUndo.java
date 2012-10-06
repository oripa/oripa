package oripa.paint;

import oripa.ORIPA;

public class BasicUndo {

	public static void undo(ActionState state, PaintContext context){
		if(context.getLineCount() > 0 || context.getVertexCount() > 0){
			state = state.undo(context);
		}
		else {
			ORIPA.doc.loadUndoInfo();
		}

	}
}
