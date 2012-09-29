package oripa.bind;

import java.awt.geom.AffineTransform;

import oripa.appstate.ApplicationState;
import oripa.appstate.StateManager;
import oripa.paint.EditMode;
import oripa.paint.PaintContext;
import oripa.paint.outline.EditOutlineAction;

public class EditOutlineActionWrapper extends EditOutlineAction {
	@Override
	public void onRightClick(PaintContext context, AffineTransform affine,
			boolean differentAction) {

		StateManager stateManager = StateManager.getInstance();
		ApplicationState<EditMode> prev = stateManager.pop();
		
		prev.performActions(null);
	}

}
