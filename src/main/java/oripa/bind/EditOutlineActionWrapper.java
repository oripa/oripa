package oripa.bind;

import java.awt.geom.AffineTransform;

import oripa.appstate.ApplicationState;
import oripa.appstate.StateManager;
import oripa.paint.EditMode;
import oripa.paint.GraphicMouseActionInterface;
import oripa.paint.PaintContextInterface;
import oripa.paint.core.PaintConfig;
import oripa.paint.outline.EditOutlineAction;

public class EditOutlineActionWrapper extends EditOutlineAction {
	
	
	
	
	
	
	@Override
	public GraphicMouseActionInterface onLeftClick(PaintContextInterface context,
			AffineTransform affine, boolean differentAction) {
		GraphicMouseActionInterface next = super.onLeftClick(context, affine, differentAction);
		
		if(context.isMissionCompleted()){
			popPreviousState();
			next = PaintConfig.getMouseAction();
		}
		
		return next;
	}

	@Override
	public void onRightClick(PaintContextInterface context, AffineTransform affine,
			boolean differentAction) {

		popPreviousState();
	}

	private void popPreviousState(){
		StateManager stateManager = StateManager.getInstance();
		ApplicationState<EditMode> prev = stateManager.pop();
		
		prev.performActions(null);

	}
	
}
