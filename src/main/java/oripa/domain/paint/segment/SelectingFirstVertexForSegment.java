package oripa.domain.paint.segment;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.PickingVertex;

public class SelectingFirstVertexForSegment extends PickingVertex {

	public SelectingFirstVertexForSegment() {
		super();
	}

	@Override
	public void undoAction(final PaintContext context) {
		context.clear(false);
	}

	@Override
	public void onResult(final PaintContext context, final boolean doSpecial) {

	}

	@Override
	protected void initialize() {
		setPreviousClass(this.getClass());
		setNextClass(SelectingSecondVertexForSegment.class);
	}

}
