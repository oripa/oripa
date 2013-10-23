package oripa.paint.mirror;

import java.awt.geom.AffineTransform;

import oripa.paint.EditMode;
import oripa.paint.PaintContextInterface;
import oripa.paint.core.BasicUndo;
import oripa.paint.selectline.SelectLineAction;

public class MirrorCopyAction extends SelectLineAction {

	
	public MirrorCopyAction(PaintContextInterface context){
		super(context);

		setEditMode(EditMode.INPUT);
		setNeedSelect(true);
		
		setActionState(new SelectingLineForMirror());
	}

	
	
	@Override
	public void destroy(PaintContextInterface context) {
		context.clear(false);
	}



	/**
	 * do usual undo.
	 */
	@Override
	public void onRightClick(PaintContextInterface context, AffineTransform affine,
			boolean differentAction) {
		BasicUndo.undo(this.getActionState(), context);
	}

	
	

	
}
