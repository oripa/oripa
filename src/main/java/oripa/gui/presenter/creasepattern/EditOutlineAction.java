package oripa.gui.presenter.creasepattern;

import java.util.Collection;
import java.util.List;

import javax.vecmath.Vector2d;

import oripa.domain.cptool.OverlappingLineExtractor;
import oripa.domain.paint.PaintContext;
import oripa.domain.paint.outline.CloseTempOutlineFactory;
import oripa.domain.paint.outline.IsOnTempOutlineLoop;
import oripa.domain.paint.outline.IsOutsideOfTempOutlineLoop;
import oripa.domain.paint.outline.SelectingVertexForOutline;
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
	public void onDraw(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
			final PaintContext paintContext) {
		super.onDraw(drawer, viewContext, paintContext);

		this.drawPickCandidateVertex(drawer, viewContext, paintContext);

		List<Vector2d> outlinevertices = paintContext.getPickedVertices();

		// Shows the outline of the editing
		int outlineVnum = outlinevertices.size();

		if (outlineVnum != 0) {

			drawTempOutlines(drawer, outlinevertices, paintContext.getScale());

			Vector2d cv = (paintContext.getCandidateVertexToPick() == null)
					? new Vector2d(paintContext.getLogicalMousePoint().getX(),
							paintContext.getLogicalMousePoint().getY())
					: paintContext.getCandidateVertexToPick();
			drawer.drawLine(outlinevertices.get(0), cv);
			drawer.drawLine(outlinevertices.get(outlineVnum - 1), cv);
		}

	}
}
