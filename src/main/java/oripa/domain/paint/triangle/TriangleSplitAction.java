package oripa.domain.paint.triangle;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.core.GraphicMouseAction;

public class TriangleSplitAction extends GraphicMouseAction {

	public TriangleSplitAction() {
		setActionState(new SelectingVertexForTriangleSplit());
	}

//	private OriLine closeLine = null;
//
//	@Override
//	public Vector2d onMove(MouseContext context, AffineTransform affine,
//			MouseEvent event) {
//		Vector2d result = super.onMove(context, affine, event);
//
//		if(context.getVertexCount() == 3){
//			if(closeLine != null){
//				closeLine.selected = false;
//			}
//
//			closeLine = context.pickCandidateL;
//
//			if(closeLine != null){
//				closeLine.selected = true;
//			}
//		}
//		return result;
//	}

	@Override
	public void onDrag(final PaintContextInterface context, final AffineTransform affine,
			final boolean differentAction) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRelease(final PaintContextInterface context, final AffineTransform affine,
			final boolean differentAction) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDraw(final Graphics2D g2d, final PaintContextInterface context) {

		super.onDraw(g2d, context);

		drawPickCandidateVertex(g2d, context);
	}

	@Override
	public void onPress(final PaintContextInterface context, final AffineTransform affine,
			final boolean differentAction) {
		// TODO Auto-generated method stub

	}

}
