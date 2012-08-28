package oripa.paint.addvertex;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

import javax.vecmath.Vector2d;

import oripa.paint.GeometricalOperation;
import oripa.paint.GraphicMouseAction;
import oripa.paint.MouseContext;

public class AddVertexAction extends GraphicMouseAction {


	public AddVertexAction(){
		setEditMode(EditMode.OTHER);

		setActionState(new AddingVertex());

	}



	@Override
	public Vector2d onMove(MouseContext context, AffineTransform affine,
			MouseEvent event) {
		Point2D.Double current = GeometricalOperation.getLogicalPoint(affine, event.getPoint());

		Vector2d closeVertex = GeometricalOperation.pickVertex(
				context, current, true);

		context.pickCandidateV = closeVertex;
		
		return closeVertex;
	}



	@Override
	public void onDragged(MouseContext context, AffineTransform affine,
			MouseEvent event) {

	}


	@Override
	public void onReleased(MouseContext context, AffineTransform affine, MouseEvent event) {


	}

	@Override
	public void onDraw(Graphics2D g2d, MouseContext context) {

		super.onDraw(g2d, context);

		drawPickCandidateVertex(g2d, context);
	}

	@Override
	public void onPressed(MouseContext context, AffineTransform affine,
			MouseEvent event) {
	}



}
