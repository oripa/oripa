package oripa.paint.selectline;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

import oripa.Config;
import oripa.Constants;
import oripa.ORIPA;
import oripa.geom.OriLine;
import oripa.geom.RectangleClipper;
import oripa.paint.GeometricalOperation;
import oripa.paint.Globals;
import oripa.paint.GraphicMouseAction;
import oripa.paint.MouseContext;
import oripa.paint.RectangularSelectableAction;

public class SelectLineAction extends RectangularSelectableAction {


	public SelectLineAction(MouseContext context){
		setEditMode(EditMode.SELECT);
		setNeedSelect(true);
		
		setActionState(new SelectingLine());


		recoverSelection(context);
	}

	

	@Override
	public void onRightClick(MouseContext context, AffineTransform affine,
			MouseEvent event) {
		ORIPA.doc.loadUndoInfo();

		recoverSelection(context);
	}


	@Override
	public void recoverSelection(MouseContext context) {
		context.clear(false);

		for(OriLine line : ORIPA.doc.lines){
			if(line.selected){
				context.pushLine(line);
			}
		}
	}


	@Override
	protected void afterRectangularSelection(Collection<OriLine> selectedLines,
			MouseContext context) {

		if(selectedLines.isEmpty() == false){

			ORIPA.doc.pushUndoInfo();

			for(OriLine line : selectedLines){
				if (line.typeVal == OriLine.TYPE_CUT) {
					continue;
				}
				// Don't select if the line is hidden
				if (!Globals.dispMVLines && (line.typeVal == OriLine.TYPE_RIDGE
						|| line.typeVal == OriLine.TYPE_VALLEY)) {
					continue;
				}
				if (!Globals.dispAuxLines && line.typeVal == OriLine.TYPE_NONE) {
					continue;
				}

				if(context.getLines().contains(line) == false){
					line.selected = true;
					context.pushLine(line);
				}

			}

		}
	}



	@Override
	public void onDraw(Graphics2D g2d, MouseContext context) {
		super.onDraw(g2d, context);

		this.drawPickCandidateLine(g2d, context);
	}





}
