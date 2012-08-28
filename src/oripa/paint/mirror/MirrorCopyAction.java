package oripa.paint.mirror;

import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

import oripa.paint.MouseContext;
import oripa.paint.selectline.SelectLineAction;

public class MirrorCopyAction extends SelectLineAction {

	
	public MirrorCopyAction(MouseContext context){
		super(context);

		setEditMode(EditMode.NORMAL);
		setNeedSelect(true);
		
		setActionState(new SelectingLineForMirror());
	}

	
	
	@Override
	public void onDestroy(MouseContext context) {
		context.clear(false);
	}



	@Override
	public void onRightClick(MouseContext context, AffineTransform affine,
			MouseEvent event) {
		undo(context);
	}

	
	

	
}
