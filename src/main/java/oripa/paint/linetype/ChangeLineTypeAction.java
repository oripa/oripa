package oripa.paint.linetype;

import java.awt.Graphics2D;
import java.util.Collection;

import oripa.ORIPA;
import oripa.doc.Doc;
import oripa.paint.EditMode;
import oripa.paint.core.PaintContext;
import oripa.paint.core.RectangularSelectableAction;
import oripa.paint.creasepattern.Painter;
import oripa.value.OriLine;
import oripa.viewsetting.main.uipanel.UIPanelSettingDB;

public class ChangeLineTypeAction extends RectangularSelectableAction {


	public ChangeLineTypeAction(){
		setEditMode(EditMode.CHANGE_TYPE);
		setActionState(new SelectingLineForLineType());
	}


	@Override
	protected void afterRectangularSelection(Collection<OriLine> selectedLines,
			PaintContext context) {

		if(selectedLines.isEmpty() == false){
			Doc document = ORIPA.doc;
			Collection<OriLine> creasePattern = document.getCreasePattern();
			document.pushUndoInfo();

			UIPanelSettingDB setting = UIPanelSettingDB.getInstance();
			for (OriLine l : selectedLines) {
				Painter painter = new Painter();
				// Change line type
				painter.alterLineType(
						l, setting.getTypeFrom(), setting.getTypeTo(),
						creasePattern);
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
