package oripa.domain.paint.addvertex;

import java.awt.geom.AffineTransform;

import oripa.domain.paint.EditMode;
import oripa.domain.paint.ObjectGraphicDrawer;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.core.GraphicMouseAction;

public class AddVertexAction extends GraphicMouseAction {

	public AddVertexAction() {
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
	public void onDrag(final PaintContextInterface context, final AffineTransform affine,
			final boolean differentAction) {

	}

	@Override
	public void onRelease(final PaintContextInterface context, final AffineTransform affine,
			final boolean differentAction) {

	}

	@Override
	public void onDraw(final ObjectGraphicDrawer drawer, final PaintContextInterface context) {

		super.onDraw(drawer, context);

		drawPickCandidateVertex(drawer, context);
	}

	@Override
	public void onPress(final PaintContextInterface context, final AffineTransform affine,
			final boolean differentAction) {
	}

}
