package oripa.domain.paint.bisector;

import java.awt.geom.Point2D.Double;

import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.core.PickingVertex;

public class SelectingVertexForBisector extends PickingVertex {

	public SelectingVertexForBisector() {
		super();
	}

	@Override
	protected void initialize() {
		setPreviousClass(this.getClass());
		setNextClass(SelectingLineForBisector.class);

//		System.out.println("SelectingFirstVertex.initialize() is called");
	}

	@Override
	protected boolean onAct(final PaintContextInterface context, final Double currentPoint,
			final boolean doSpecial) {

		if (context.getVertexCount() == 0) {
			context.creasePatternUndo().cacheUndoInfo();
		}

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
	public void onResult(final PaintContextInterface context, final boolean doSpecial) {

	}

}
