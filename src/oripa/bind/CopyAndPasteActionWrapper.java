package oripa.bind;

import java.awt.geom.AffineTransform;

import oripa.ORIPA;
import oripa.appstate.ApplicationState;
import oripa.appstate.StateManager;
import oripa.paint.EditMode;
import oripa.paint.PaintContext;
import oripa.paint.copypaste.CopyAndPasteAction;

public class CopyAndPasteActionWrapper extends CopyAndPasteAction {

	
	private boolean isCut;
	
	public CopyAndPasteActionWrapper(boolean isCut) {
		this.isCut = isCut;
	}

	@Override
	public void recover(PaintContext context) {
		super.recover(context);
		
		if(isCut){
			ORIPA.doc.deleteSelectedLines();
		}
	}
	
	@Override
	public void onRightClick(PaintContext context, AffineTransform affine,
			boolean differentAction) {
		
		
		/*TODO:
		 *
		 * I am trying to emulate the original ORIPA's action.
		 * On right click, copy-and-paste action has to finish
		 * and get back to previous action.
		 **/
		

		StateManager stateManager = StateManager.getInstance();
		ApplicationState<EditMode> prev = stateManager.pop();
		
		prev.performActions(null);
		
	}

	
	
}
