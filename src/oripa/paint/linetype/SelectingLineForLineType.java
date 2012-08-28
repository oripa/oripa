package oripa.paint.linetype;

import oripa.ORIPA;
import oripa.paint.MouseContext;
import oripa.paint.PickingLine;
import oripa.view.UIPanelSettingDB;

public class SelectingLineForLineType extends PickingLine {

	
	
	public SelectingLineForLineType() {
		super();
	}

	@Override
	protected void initialize() {
	}
	
	
	@Override
	protected void undoAction(MouseContext context) {
		// TODO Auto-generated method stub
		super.undoAction(context);
	}

	@Override
	protected void onResult(MouseContext context) {
		// TODO Auto-generated method stub
        ORIPA.doc.pushUndoInfo();

    	UIPanelSettingDB setting = UIPanelSettingDB.getInstance();
        ORIPA.doc.alterLineType(context.peekLine(), setting.getLineTypeFromIndex(), setting.getLineTypeToIndex());

        context.clear(false);
	}

}
