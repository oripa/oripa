package oripa.domain.paint.bisector;

import javax.vecmath.Vector2d;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.PickingVertex;

public class SelectingVertexForBisector extends PickingVertex {

	public SelectingVertexForBisector() {
		super();
	}

	@Override
	protected void initialize() {
		setPreviousClass(this.getClass());
		setNextClass(SelectingLineForBisector.class);
	}

	@Override
	protected boolean onAct(final PaintContext context, final Vector2d currentPoint,
			final boolean doSpecial) {
		boolean vertexIsSelected = super.onAct(context, currentPoint, doSpecial);

		if (!vertexIsSelected) {
			return false;
		}

		if (context.getVertexCount() < 3) {
			return false;
		}
		return true; // 3 vertices are selected. go to selecting a line.
	}

	@Override
	public void onResult(final PaintContext context, final boolean doSpecial) {

	}

}
