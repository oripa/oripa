package oripa.paint.copypaste;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import javax.vecmath.Vector2d;

import oripa.ORIPA;
import oripa.paint.EditMode;
import oripa.paint.PaintContextInterface;
import oripa.paint.core.GraphicMouseAction;
import oripa.paint.creasepattern.CreasePattern;
import oripa.paint.geometry.NearestVertexFinder;
import oripa.paint.geometry.NearestVertexFinderHelper;
import oripa.value.OriLine;

public class PasteAction extends GraphicMouseAction {


	private FilledOriLineArrayList shiftedLines = new FilledOriLineArrayList(0);

	private OriginHolder originHolder = OriginHolder.getInstance();


	public PasteAction(){
		setEditMode(EditMode.INPUT);
		setNeedSelect(true);

		setActionState(new PastingOnVertex());

	}


	@Override
	public void recover(PaintContextInterface context) {
		context.clear(false);


		context.startPasting();

        CreasePattern creasePattern = ORIPA.doc.getCreasePattern();

		for(OriLine line : creasePattern){
			if(line.selected){
				context.pushLine(line);
			}
		}

		shiftedLines = new FilledOriLineArrayList(context.getLines());

	}



	/**
	 * Clear context and mark lines as unselected.
	 */
	@Override
	public void destroy(PaintContextInterface context) {
		context.clear(true);
		context.finishPasting();
	}


	@Override
	public void onDrag(PaintContextInterface context, AffineTransform affine,
			boolean differentAction) {

	}


	@Override
	public void onRelease(PaintContextInterface context, AffineTransform affine, boolean differentAction) {


	}


	@Override
	public Vector2d onMove(PaintContextInterface context, AffineTransform affine,
			boolean differentAction) {

		// vertex-only super's action
		setCandidateVertexOnMove(context, differentAction);
		Vector2d closeVertex = context.getPickCandidateV();


		Vector2d closeVertexOfLines = 
				NearestVertexFinder.pickVertexFromPickedLines(context);

		if(closeVertex == null){
			closeVertex = closeVertexOfLines;
		}


		Point2D.Double current = context.getLogicalMousePoint();
		if(closeVertex != null && closeVertexOfLines != null){
			// get the nearest to current
			closeVertex = NearestVertexFinderHelper.findNearestOf(
					current, closeVertex, closeVertexOfLines);

		}

		context.setPickCandidateV(closeVertex);

//		if (context.getLineCount() > 0) {
//			if(closeVertex == null) {
//				closeVertex = new Vector2d(current.x, current.y);
//			}
//
//		}		
		return closeVertex;
	}



	Line2D.Double g2dLine = new Line2D.Double();
	double diffX, diffY;
	@Override
	public void onDraw(Graphics2D g2d, PaintContextInterface context) {

		super.onDraw(g2d, context);

		drawPickCandidateVertex(g2d, context);

		Vector2d origin = originHolder.getOrigin(context);

		if(origin == null){
			return;
		}


		double ox = origin.x;
		double oy = origin.y;

		g2d.setColor(Color.GREEN);
		drawVertex(g2d, context, ox, oy);

		Vector2d candidateVertex = context.getPickCandidateV();
		if(candidateVertex != null){
			diffX = candidateVertex.x - ox;
			diffY = candidateVertex.y - oy;
		}
		else {
			diffX = context.getLogicalMousePoint().x - ox;
			diffY = context.getLogicalMousePoint().y -oy;
		}
		g2d.setColor(Color.MAGENTA);

		//		GeometricOperation.shiftLines(context.getLines(), shiftedLines,
		//				current.x - ox, current.y -oy);
		//		
		//		for(OriLine line : shiftedLines){
		//			this.drawLine(g2d, line);
		//		}

		// a little faster
		for(OriLine l : context.getLines()){

			g2dLine.x1 = l.p0.x + diffX;
			g2dLine.y1 = l.p0.y + diffY;

			g2dLine.x2 = l.p1.x + diffX;
			g2dLine.y2 = l.p1.y + diffY;

			g2d.draw(g2dLine);
		}

	}

	@Override
	public void onPress(PaintContextInterface context, AffineTransform affine,
			boolean differentAction) {
	}



}
