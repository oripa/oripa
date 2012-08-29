package oripa.paint.copypaste;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.LinkedList;

import javax.vecmath.Vector2d;

import oripa.ORIPA;
import oripa.geom.OriLine;
import oripa.paint.GeometricalOperation;
import oripa.paint.GraphicMouseAction;
import oripa.paint.MouseContext;

public class PasteAction extends GraphicMouseAction {


	public PasteAction(){
		setEditMode(EditMode.INPUT);
		setNeedSelect(true);

		setActionState(new PastingOnVertex());

	}


	@Override
	public void onDragged(MouseContext context, AffineTransform affine,
			MouseEvent event) {

	}


	@Override
	public void onReleased(MouseContext context, AffineTransform affine, MouseEvent event) {


	}


	private Collection<OriLine> shiftedLines = new LinkedList<>();

	@Override
	public Vector2d onMove(MouseContext context, AffineTransform affine,
			MouseEvent event) {
		
		super.onMove(context, affine, event);
		
		Point2D.Double current = GeometricalOperation.getLogicalPoint(affine, event.getPoint());

		Vector2d v = new Vector2d(current.x, current.y);
		
		if (context.getLineCount() > 0) {

			double ox = context.getLine(0).p0.x;
			double oy = context.getLine(0).p0.y;
			shiftedLines = 
					GeometricalOperation.shiftLines(context.getLines(), v.x - ox, v.y -oy);

		}		
		return v;
	}


	@Override
	public void onDraw(Graphics2D g2d, MouseContext context) {

		super.onDraw(g2d, context);

		drawPickCandidateVertex(g2d, context);

		if(shiftedLines.isEmpty() == false){
			double ox = context.getLine(0).p0.x;
			double oy = context.getLine(0).p0.y;
			
			g2d.setColor(Color.GREEN);
			drawVertex(g2d, context, ox, oy);
			
	        g2d.setColor(Color.MAGENTA);
			for(OriLine line : shiftedLines){
				this.drawLine(g2d, line);
			}
		}

	}

	@Override
	public void onPressed(MouseContext context, AffineTransform affine,
			MouseEvent event) {
	}



}
