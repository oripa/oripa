package oripa.domain.paint.vertical;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.PickingVertex;

public class SelectingVertexForVertical extends PickingVertex {

	public SelectingVertexForVertical() {
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
		setNextClass(SelectingLineForVertical.class);

//		System.out.println("SelectingFirstVertex.initialize() is called");
	}

}
