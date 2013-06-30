package oripa.bind;

import java.awt.geom.AffineTransform;

import oripa.appstate.ApplicationState;
import oripa.appstate.StateManager;
import oripa.paint.EditMode;
import oripa.paint.Globals;
import oripa.paint.GraphicMouseAction;
import oripa.paint.PaintContext;
import oripa.paint.outline.EditOutlineAction;

public class EditOutlineActionWrapper extends EditOutlineAction {
	
	
	
	
	
	
	@Override
	public GraphicMouseAction onLeftClick(PaintContext context,
			AffineTransform affine, boolean differentAction) {
		GraphicMouseAction next = super.onLeftClick(context, affine, differentAction);
		
		if(context.isMissionCompleted()){
			popPreviousState();
			next = Globals.getMouseAction();
		}
		
		return next;
	}

	@Override
	public void onRightClick(PaintContext context, AffineTransform affine,
			boolean differentAction) {

		popPreviousState();
	}

	private void popPreviousState(){
		StateManager stateManager = StateManager.getInstance();
		ApplicationState<EditMode> prev = stateManager.pop();
		
		prev.performActions(null);

	}
	
}
