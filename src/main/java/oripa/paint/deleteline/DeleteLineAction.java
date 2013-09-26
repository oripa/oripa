package oripa.paint.deleteline;

import java.awt.Graphics2D;
import java.util.Collection;

import oripa.ORIPA;
import oripa.paint.core.EditMode;
import oripa.paint.core.PaintContext;
import oripa.paint.core.RectangularSelectableAction;
import oripa.value.OriLine;

public class DeleteLineAction extends RectangularSelectableAction {


	public DeleteLineAction(){
		setEditMode(EditMode.OTHER);

		setActionState(new DeletingLine());

	}

	@Override
	public void onDraw(Graphics2D g2d, PaintContext context) {

		super.onDraw(g2d, context);

		drawPickCandidateLine(g2d, context);
		
	}

	@Override
	protected void afterRectangularSelection(Collection<OriLine> selectedLines,
			PaintContext context) {

		if(selectedLines.isEmpty() == false){
			ORIPA.doc.pushUndoInfo();
			for (OriLine l : selectedLines) {
				ORIPA.doc.removeLine(l);
			}
		}		
	}



}
