package oripa.bind.copypaste;

import java.awt.geom.AffineTransform;

import oripa.ORIPA;
import oripa.appstate.ApplicationState;
import oripa.appstate.StateManager;
import oripa.paint.copypaste.CopyAndPasteAction;
import oripa.paint.core.EditMode;
import oripa.paint.core.PaintContext;

public class CopyAndPasteActionWrapper extends CopyAndPasteAction {

	
	private boolean isCut;
	
	public CopyAndPasteActionWrapper(boolean isCut) {
		super();
		this.isCut = isCut;
		if(isCut){
			super.setEditMode(EditMode.CUT);
		}
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
		
		StateManager stateManager = StateManager.getInstance();
		ApplicationState<EditMode> prev = stateManager.pop();
		
		if(prev == null){
			return;
		}
		
		// a case having switched copy to cut.
		prev.performActions(null);
	}

	
	
}
