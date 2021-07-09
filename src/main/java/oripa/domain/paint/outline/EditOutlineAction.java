package oripa.domain.paint.outline;

import java.util.Collection;
import java.util.List;

import javax.vecmath.Vector2d;

import oripa.domain.cptool.OverlappingLineExtractor;
import oripa.domain.paint.EditMode;
import oripa.domain.paint.ObjectGraphicDrawer;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.core.GraphicMouseAction;
import oripa.domain.paint.util.PairLoop;

public class EditOutlineAction extends GraphicMouseAction {

	public EditOutlineAction() {
		setActionState(new SelectingVertexForOutline(new CloseTempOutlineFactory(new IsOnTempOutlineLoop(),
				new IsOutsideOfTempOutlineLoop(), new OverlappingLineExtractor())));
		setEditMode(EditMode.OTHER);
	}

	private void drawTempOutlines(final ObjectGraphicDrawer drawer, final Collection<Vector2d> outlineVertices,
			final double scale) {
		drawer.selectEditingOutlineColor();
		drawer.selectEditingOutlineStroke(scale);

		if (outlineVertices.size() > 1) {
			PairLoop.iterateWithCount(
					outlineVertices, outlineVertices.size() - 1, (p0, p1) -> {
						drawer.drawLine(p0, p1);
						return true;
					});
		}
	}

	@Override
	public void onDraw(final ObjectGraphicDrawer drawer, final PaintContextInterface context) {
		super.onDraw(drawer, context);

		this.drawPickCandidateVertex(drawer, context);

		List<Vector2d> outlinevertices = context.getPickedVertices();

		// Shows the outline of the editing
		int outlineVnum = outlinevertices.size();

		if (outlineVnum != 0) {

			drawTempOutlines(drawer, outlinevertices, context.getScale());

			Vector2d cv = (context.getCandidateVertexToPick() == null)
					? new Vector2d(context.getLogicalMousePoint().getX(),
							context.getLogicalMousePoint().getY())
					: context.getCandidateVertexToPick();
			drawer.drawLine(outlinevertices.get(0), cv);
			drawer.drawLine(outlinevertices.get(outlineVnum - 1), cv);
		}

	}
}
