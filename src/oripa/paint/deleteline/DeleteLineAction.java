package oripa.paint.deleteline;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import oripa.Constants;
import oripa.ORIPA;
import oripa.geom.OriLine;
import oripa.geom.RectangleClipper;
import oripa.paint.Globals;
import oripa.paint.GraphicMouseAction;
import oripa.paint.MouseContext;
import oripa.view.UIPanelSettingDB;

public class DeleteLineAction extends GraphicMouseAction {


	public DeleteLineAction(){
		setEditMode(EditMode.OTHER);

		setActionState(new DeletingLine());

	}

	//	private OriLine closeLine = null;
	//
	//	@Override
	//	public Vector2d onMove(MouseContext context, AffineTransform affine,
	//			MouseEvent event) {
	//		Vector2d result = super.onMove(context, affine, event);
	//
	//		if(closeLine != null){
	//			closeLine.selected = false;
	//		}
	//			
	//		closeLine = context.pickCandidateL;
	//	
	//		if(closeLine != null){
	//			closeLine.selected = true;
	//		}
	//			
	//		return result;
	//	}

	@Override
	public void onDragged(MouseContext context, AffineTransform affine,
			MouseEvent event) {

	}


	java.awt.Point startPoint;
	@Override
	public void onReleased(MouseContext context, AffineTransform affine, MouseEvent event) {
		java.awt.Point currentPoint = event.getPoint();

		Point2D.Double sp = new Point2D.Double();
		Point2D.Double ep = new Point2D.Double();
		try {
			affine.inverseTransform(startPoint, sp);
			affine.inverseTransform(currentPoint, ep);

			RectangleClipper clipper = new RectangleClipper(Math.min(sp.x, ep.x),
					Math.min(sp.y, ep.y),
					Math.max(sp.x, ep.x),
					Math.max(sp.y, ep.y));
			for (OriLine l : ORIPA.doc.lines) {

				if (clipper.clipTest(l)) {
					context.pushLine(l);
				}

			}
		} catch (Exception ex) {
		}

		if (context.getLineCount() > 0) {
			ORIPA.doc.pushUndoInfo();
			for (OriLine l : context.getLines()) {
				ORIPA.doc.removeLine(l);
			}

			context.clear(false);

		}


	}

	@Override
	public void onDraw(Graphics2D g2d, MouseContext context) {

		super.onDraw(g2d, context);

		drawPickCandidateLine(g2d, context);
	}

	@Override
	public void onPressed(MouseContext context, AffineTransform affine,
			MouseEvent event) {
		startPoint = event.getPoint();
	}



}
