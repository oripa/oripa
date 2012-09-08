package oripa.paint.copypaste;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.LinkedList;

import javax.vecmath.Vector2d;

import oripa.ORIPA;
import oripa.geom.OriLine;
import oripa.paint.GraphicMouseAction;
import oripa.paint.PaintContext;
import oripa.paint.geometry.GeometricOperation;

public class PasteAction extends GraphicMouseAction {


	public PasteAction(){
		setEditMode(EditMode.INPUT);
		setNeedSelect(true);

		setActionState(new PastingOnVertex());

	}


	@Override
	public void recover(PaintContext context) {
		context.clear(false);

		for(OriLine line : ORIPA.doc.lines){
			if(line.selected){
				context.pushLine(line);
			}
		}

		ORIPA.doc.prepareForCopyAndPaste();

	}


	@Override
	public void onDrag(PaintContext context, AffineTransform affine,
			boolean differentAction) {

	}


	@Override
	public void onRelease(PaintContext context, AffineTransform affine, boolean differentAction) {


	}


	private Collection<OriLine> shiftedLines = new LinkedList<>();
	private OriginHolder originHolder = OriginHolder.getInstance();
	@Override
	public Vector2d onMove(PaintContext context, AffineTransform affine,
			boolean differentAction) {
		
		super.onMove(context, affine, differentAction);
		
		Point2D.Double current = context.getLogicalMousePoint();

		Vector2d v = new Vector2d(current.x, current.y);
		
		if (context.getLineCount() > 0) {
			
			Vector2d origin = originHolder.getOrigin(context);
			double ox = origin.x;
			double oy = origin.y;
			shiftedLines = 
					GeometricOperation.shiftLines(context.getLines(), v.x - ox, v.y -oy);

		}		
		return v;
	}


	@Override
	public void onDraw(Graphics2D g2d, PaintContext context) {

		super.onDraw(g2d, context);

		drawPickCandidateVertex(g2d, context);

		if(shiftedLines.isEmpty() == false){
			Vector2d origin = originHolder.getOrigin(context);
			double ox = origin.x;
			double oy = origin.y;
			
			g2d.setColor(Color.GREEN);
			drawVertex(g2d, context, ox, oy);
			
	        g2d.setColor(Color.MAGENTA);
			for(OriLine line : shiftedLines){
				this.drawLine(g2d, line);
			}
		}

	}

	@Override
	public void onPress(PaintContext context, AffineTransform affine,
			boolean differentAction) {
	}



}
