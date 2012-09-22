package oripa.paint.linetype;

import oripa.ORIPA;
import oripa.paint.PaintContext;
import oripa.paint.PickingLine;
import oripa.viewsetting.uipanel.UIPanelSettingDB;

public class SelectingLineForLineType extends PickingLine {

	
	
	public SelectingLineForLineType() {
		super();
	}

	@Override
	protected void initialize() {
	}
	
	
	@Override
	protected void undoAction(PaintContext context) {
		// TODO Auto-generated method stub
		super.undoAction(context);
	}

	@Override
	protected void onResult(PaintContext context) {
		// TODO Auto-generated method stub
        ORIPA.doc.pushUndoInfo();

    	UIPanelSettingDB setting = UIPanelSettingDB.getInstance();
        ORIPA.doc.alterLineType(context.peekLine(),  setting.getTypeFrom(), setting.getTypeTo());

        context.clear(false);
	}

}
