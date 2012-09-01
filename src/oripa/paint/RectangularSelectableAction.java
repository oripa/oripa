package oripa.paint;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.LinkedList;

import oripa.Config;
import oripa.ORIPA;
import oripa.geom.OriLine;
import oripa.geom.RectangleClipper;
import oripa.paint.GeometricalOperation;
import oripa.paint.Globals;
import oripa.paint.GraphicMouseAction;
import oripa.paint.MouseContext;
import oripa.resource.Constants;

public abstract class RectangularSelectableAction extends GraphicMouseAction {


	private Point2D.Double startPoint = null; 
	private Point2D.Double draggingPoint = null; 

	@Override
	public void onPressed(MouseContext context, AffineTransform affine,
			MouseEvent event) {
		startPoint = GeometricalOperation.getLogicalPoint(affine, event.getPoint());
	}

	@Override
	public void onDragged(MouseContext context, AffineTransform affine,
			MouseEvent event) {
		
		draggingPoint = GeometricalOperation.getLogicalPoint(affine, event.getPoint());		
	
	}



	@Override
	public void onReleased(MouseContext context, AffineTransform affine, MouseEvent event) {

		if(startPoint != null && draggingPoint != null){
			selectByRectangularArea(context);
		}

		startPoint = null;
		draggingPoint = null;
		
	}

	protected abstract void afterRectangularSelection(Collection<OriLine> selectedLines, MouseContext context);
	
	protected final void selectByRectangularArea(MouseContext context){
		LinkedList<OriLine> selectedLines = new LinkedList<>();

		try {

			RectangleClipper clipper = new RectangleClipper(Math.min(startPoint.x, draggingPoint.x),
					Math.min(startPoint.y, draggingPoint.y),
					Math.max(startPoint.x, draggingPoint.x),
					Math.max(startPoint.y, draggingPoint.y));
			for (OriLine l : ORIPA.doc.lines) {


					if (clipper.clipTest(l)) {
					
						selectedLines.addLast(l);

					}
			}

		} catch (Exception ex) {

		}

		afterRectangularSelection(selectedLines, context);
	}
	
	
	@Override
	public void onDraw(Graphics2D g2d, MouseContext context) {

		super.onDraw(g2d, context);

		if(startPoint != null && draggingPoint != null){
						
	        g2d.setStroke(Config.STROKE_SELECT_BY_AREA);
	        g2d.setColor(Color.BLACK);
	        double sx = Math.min(startPoint.x, draggingPoint.x);
	        double sy = Math.min(startPoint.y, draggingPoint.y);
	        double w = Math.abs(startPoint.x - draggingPoint.x);
	        double h = Math.abs(startPoint.y - draggingPoint.y);
	        g2d.draw(new Rectangle2D.Double(sx, sy, w, h));

		}

	}



}
