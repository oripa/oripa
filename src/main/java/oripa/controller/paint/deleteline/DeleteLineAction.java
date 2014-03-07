package oripa.controller.paint.deleteline;

import java.awt.Graphics2D;
import java.util.Collection;

import oripa.ORIPA;
import oripa.controller.paint.EditMode;
import oripa.controller.paint.PaintContextInterface;
import oripa.controller.paint.core.GraphicMouseAction;
import oripa.controller.paint.core.PaintContext;
import oripa.controller.paint.core.RectangularSelectableAction;
import oripa.domain.cptool.Painter;
import oripa.persistent.doc.Doc;
import oripa.value.OriLine;

public class DeleteLineAction extends RectangularSelectableAction {


	public DeleteLineAction(){
		setEditMode(EditMode.OTHER);

		setActionState(new DeletingLine());

	}

	@Override
	public void onDraw(Graphics2D g2d, PaintContextInterface context) {

		super.onDraw(g2d, context);

		drawPickCandidateLine(g2d, context);
		
	}

	/**
	 * Reset selection mark to avoid undesired deletion.
	 * @see GraphicMouseAction#recover(PaintContext)
	 * @param context
	 */
	@Override
	public void recover(PaintContextInterface context) {
		context.clear(true);
	}
	
	@Override
	protected void afterRectangularSelection(Collection<OriLine> selectedLines,
			PaintContextInterface context) {

		Doc document = ORIPA.doc;
		Collection<OriLine> creasePattern = document.getCreasePattern();

		if(selectedLines.isEmpty() == false){
			document.pushUndoInfo();
			Painter painter = new Painter();
			for (OriLine l : selectedLines) {
				painter.removeLine(l, creasePattern);
			}
		}		
	}



}
