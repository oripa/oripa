package oripa.paint.mirror;

import java.awt.geom.AffineTransform;

import oripa.paint.EditMode;
import oripa.paint.core.BasicUndo;
import oripa.paint.core.PaintContext;
import oripa.paint.selectline.SelectLineAction;

public class MirrorCopyAction extends SelectLineAction {

	
	public MirrorCopyAction(PaintContext context){
		super(context);

		setEditMode(EditMode.INPUT);
		setNeedSelect(true);
		
		setActionState(new SelectingLineForMirror());
	}

	
	
	@Override
	public void destroy(PaintContext context) {
		context.clear(false);
	}



	/**
	 * do usual undo.
	 */
	@Override
	public void onRightClick(PaintContext context, AffineTransform affine,
			boolean differentAction) {
		BasicUndo.undo(this.getActionState(), context);
	}

	
	

	
}
