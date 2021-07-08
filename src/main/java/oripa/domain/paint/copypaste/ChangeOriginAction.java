package oripa.domain.paint.copypaste;

import java.util.Collection;

import javax.vecmath.Vector2d;

import oripa.domain.paint.GraphicMouseActionInterface;
import oripa.domain.paint.ObjectGraphicDrawer;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.core.GraphicMouseAction;
import oripa.domain.paint.geometry.NearestItemFinder;
import oripa.value.OriLine;

public class ChangeOriginAction extends GraphicMouseAction {

	private final SelectionOriginHolder holder;

	/**
	 * Constructor
	 */
	public ChangeOriginAction(final SelectionOriginHolder holder) {
		this.holder = holder;
	}

	@Override
	public GraphicMouseActionInterface onLeftClick(final PaintContextInterface context,
			final boolean keepDoing) {

		return this;
	}

	@Override
	public void doAction(final PaintContextInterface context, final Vector2d point,
			final boolean differntAction) {

	}

	@Override
	public void undo(final PaintContextInterface context) {
	}

	@Override
	public void onPress(final PaintContextInterface context, final boolean differentAction) {

	}

	@Override
	public void onDrag(final PaintContextInterface context, final boolean differentAction) {

	}

	@Override
	public void onRelease(final PaintContextInterface context, final boolean differentAction) {

	}

	@Override
	public Vector2d onMove(final PaintContextInterface context, final boolean differentAction) {
		Vector2d closeVertex = NearestItemFinder.pickVertexFromPickedLines(context);
		context.setCandidateVertexToPick(closeVertex);

		if (closeVertex != null) {
			holder.setOrigin(closeVertex);
		}

		return closeVertex;
	}

	@Override
	public void onDraw(final ObjectGraphicDrawer drawer, final PaintContextInterface context) {
		super.onDraw(drawer, context);

		Collection<OriLine> lines = context.getPickedLines();

		drawer.selectAssistLineColor();

		for (OriLine line : lines) {
			this.drawVertex(drawer, context, line.p0);
			this.drawVertex(drawer, context, line.p1);
		}

		this.drawPickCandidateVertex(drawer, context);
	}
}
