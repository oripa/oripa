package oripa.paint.linetype;

import java.awt.Graphics2D;
import java.util.Collection;

import oripa.ORIPA;
import oripa.geom.OriLine;
import oripa.paint.MouseContext;
import oripa.paint.RectangularSelectableAction;
import oripa.view.UIPanelSettingDB;

public class ChangeLineTypeAction extends RectangularSelectableAction {


	public ChangeLineTypeAction(){
		setActionState(new SelectingLineForLineType());
	}


	@Override
	protected void afterRectangularSelection(Collection<OriLine> selectedLines,
			MouseContext context) {
		if(selectedLines.isEmpty() == false){
			ORIPA.doc.pushUndoInfo();

			UIPanelSettingDB setting = UIPanelSettingDB.getInstance();
			for (OriLine l : selectedLines) {
				// Change line type
				ORIPA.doc.alterLineType(l, setting.getLineTypeFromIndex(), setting.getLineTypeToIndex());
			}

		}
	}


	@Override
	public void onDraw(Graphics2D g2d, MouseContext context) {

		super.onDraw(g2d, context);

		drawPickCandidateLine(g2d, context);
	}



}
