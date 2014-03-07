package oripa.controller.paint.selectline;

import java.awt.Graphics2D;
import java.util.Collection;

import oripa.ORIPA;
import oripa.controller.paint.EditMode;
import oripa.controller.paint.PaintContextInterface;
import oripa.controller.paint.core.PaintConfig;
import oripa.controller.paint.core.RectangularSelectableAction;
import oripa.value.OriLine;

public class SelectLineAction extends RectangularSelectableAction {


	public SelectLineAction(PaintContextInterface context){
		setEditMode(EditMode.SELECT);
		setNeedSelect(true);
		
		setActionState(new SelectingLine());


		recover(context);
	}

	/**
	 * set old line-selected marks to current context.s
	 */
	@Override
	public void undo(PaintContextInterface context) {
		ORIPA.doc.loadUndoInfo();

		recover(context);
	}


	@Override
	public void recover(PaintContextInterface context) {
		context.clear(false);

		Collection<OriLine> creasePattern = ORIPA.doc.getCreasePattern();
		if(creasePattern == null){
			return;
		}
		
		for(OriLine line : creasePattern){
			if(line.selected){
				context.pushLine(line);
			}
		}
	}


	@Override
	protected void afterRectangularSelection(Collection<OriLine> selectedLines,
			PaintContextInterface context) {

		if(selectedLines.isEmpty() == false){

			ORIPA.doc.pushUndoInfo();

			for(OriLine line : selectedLines){
				if (line.typeVal == OriLine.TYPE_CUT) {
					continue;
				}
				// Don't select if the line is hidden
				if (!PaintConfig.dispMVLines && (line.typeVal == OriLine.TYPE_RIDGE
						|| line.typeVal == OriLine.TYPE_VALLEY)) {
					continue;
				}
				if (!PaintConfig.dispAuxLines && line.typeVal == OriLine.TYPE_NONE) {
					continue;
				}

				if(context.getPickedLines().contains(line) == false){
					line.selected = true;
					context.pushLine(line);
				}

			}

		}
	}



	@Override
	public void onDraw(Graphics2D g2d, PaintContextInterface context) {
		super.onDraw(g2d, context);

		this.drawPickCandidateLine(g2d, context);
	}





}
