package oripa.paint.core;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.LinkedList;

import oripa.ORIPA;
import oripa.geom.RectangleClipper;
import oripa.paint.creasepattern.CreasePattern;
import oripa.value.OriLine;

public abstract class RectangularSelectableAction extends GraphicMouseAction {


	private Point2D.Double startPoint = null; 
	private Point2D.Double draggingPoint = null; 

	@Override
	public void onPress(PaintContext context, AffineTransform affine,
			boolean differentAction) {
		startPoint = context.getLogicalMousePoint();
	}

	@Override
	public void onDrag(PaintContext context, AffineTransform affine,
			boolean differentAction) {
		
		draggingPoint = context.getLogicalMousePoint();
	
	}



	@Override
	public void onRelease(PaintContext context, AffineTransform affine, boolean differentAction) {

		if(startPoint != null && draggingPoint != null){
			selectByRectangularArea(context);
		}

		startPoint = null;
		draggingPoint = null;
		
	}

	/**
	 * defines what to do for the selected lines.
	 * @param selectedLines		lines selected by dragging
	 * @param context
	 */
	protected abstract void afterRectangularSelection(
			Collection<OriLine> selectedLines, PaintContext context);
	
	protected final void selectByRectangularArea(PaintContext context){
		LinkedList<OriLine> selectedLines = new LinkedList<>();

		try {

			RectangleClipper clipper = new RectangleClipper(Math.min(startPoint.x, draggingPoint.x),
					Math.min(startPoint.y, draggingPoint.y),
					Math.max(startPoint.x, draggingPoint.x),
					Math.max(startPoint.y, draggingPoint.y));
			
	        CreasePattern creasePattern = ORIPA.doc.getCreasePattern();

			for (OriLine l : creasePattern) {


					if (clipper.clipTest(l)) {
					
						selectedLines.addLast(l);

					}
			}

		} catch (Exception ex) {

		}

		afterRectangularSelection(selectedLines, context);
	}
	
	
	@Override
	public void onDraw(Graphics2D g2d, PaintContext context) {

		super.onDraw(g2d, context);

		if(startPoint != null && draggingPoint != null){
			float dash[] = {3.0f};
	        g2d.setStroke(new BasicStroke((float) (0.75 / context.scale), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
			g2d.setColor(PaintConfig.colors.getUiOverlayColor());

	        double sx = Math.min(startPoint.x, draggingPoint.x);
	        double sy = Math.min(startPoint.y, draggingPoint.y);
	        double w = Math.abs(startPoint.x - draggingPoint.x);
	        double h = Math.abs(startPoint.y - draggingPoint.y);
	        g2d.draw(new Rectangle2D.Double(sx, sy, w, h));

		}

	}



}
