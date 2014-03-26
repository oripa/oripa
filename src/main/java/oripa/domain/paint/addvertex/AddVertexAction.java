package oripa.domain.paint.addvertex;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import oripa.domain.paint.EditMode;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.core.GraphicMouseAction;

public class AddVertexAction extends GraphicMouseAction {


	public AddVertexAction(){
		setEditMode(EditMode.VERTEX);

		setActionState(new AddingVertex());

	}



//	@Override
//	public Vector2d onMove(MouseContext context, AffineTransform affine,
//			boolean differentAction) {
//		Point2D.Double current = GeometricalOperation.getLogicalPoint(affine, differentAction.getPoint());
//
//		Vector2d closeVertex = GeometricalOperation.pickVertex(
//				context, current, true);
//
//		context.pickCandidateV = closeVertex;
//		
//		return closeVertex;
//	}



	@Override
	public void onDrag(PaintContextInterface context, AffineTransform affine,
			boolean differentAction) {

	}


	@Override
	public void onRelease(PaintContextInterface context, AffineTransform affine, boolean differentAction) {


	}

	@Override
	public void onDraw(Graphics2D g2d, PaintContextInterface context) {

		super.onDraw(g2d, context);

		drawPickCandidateVertex(g2d, context);
	}

	@Override
	public void onPress(PaintContextInterface context, AffineTransform affine,
			boolean differentAction) {
	}



}
