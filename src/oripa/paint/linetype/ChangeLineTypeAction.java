package oripa.paint.linetype;

import java.awt.Graphics2D;
import java.util.Collection;

import oripa.ORIPA;
import oripa.geom.OriLine;
import oripa.paint.EditMode;
import oripa.paint.PaintContext;
import oripa.paint.RectangularSelectableAction;
import oripa.viewsetting.uipanel.UIPanelSettingDB;

public class ChangeLineTypeAction extends RectangularSelectableAction {


	public ChangeLineTypeAction(){
		setEditMode(EditMode.CHANGE_TYPE);
		setActionState(new SelectingLineForLineType());
	}


	@Override
	protected void afterRectangularSelection(Collection<OriLine> selectedLines,
			PaintContext context) {
		if(selectedLines.isEmpty() == false){
			ORIPA.doc.pushUndoInfo();

			UIPanelSettingDB setting = UIPanelSettingDB.getInstance();
			for (OriLine l : selectedLines) {
				// Change line type
				ORIPA.doc.alterLineType(l, setting.getTypeFrom(), setting.getTypeTo());
				//ORIPA.doc.alterLineType(l, setting.getLineTypeFromIndex(), setting.getLineTypeToIndex());
			}

		}
	}


	@Override
	public void onDraw(Graphics2D g2d, PaintContext context) {

		super.onDraw(g2d, context);

		drawPickCandidateLine(g2d, context);
	}



}
