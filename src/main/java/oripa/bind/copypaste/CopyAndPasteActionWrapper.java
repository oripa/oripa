package oripa.bind.copypaste;

import java.awt.geom.AffineTransform;
import java.util.Collection;

import oripa.ORIPA;
import oripa.appstate.ApplicationState;
import oripa.appstate.StateManager;
import oripa.controller.paint.EditMode;
import oripa.controller.paint.PaintContextInterface;
import oripa.controller.paint.copypaste.CopyAndPasteAction;
import oripa.domain.cptool.Painter;
import oripa.persistent.doc.Doc;
import oripa.value.OriLine;

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
	public void recover(PaintContextInterface context) {
		super.recover(context);
		Doc document = ORIPA.doc;
		Collection<OriLine> creasePattern = document.getCreasePattern();
		if(isCut){
			Painter painter = new Painter();
			painter.removeSelectedLines(creasePattern);
		}
	}
	
	@Override
	public void onRightClick(PaintContextInterface context, AffineTransform affine,
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
