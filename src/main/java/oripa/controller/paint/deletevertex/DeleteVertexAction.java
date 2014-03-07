package oripa.controller.paint.deletevertex;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import oripa.controller.paint.EditMode;
import oripa.controller.paint.PaintContextInterface;
import oripa.controller.paint.core.GraphicMouseAction;

public class DeleteVertexAction extends GraphicMouseAction {


	public DeleteVertexAction(){
		setEditMode(EditMode.VERTEX);

		setActionState(new DeletingVertex());

	}


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
