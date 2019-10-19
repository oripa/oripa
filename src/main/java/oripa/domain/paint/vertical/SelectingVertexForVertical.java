package oripa.domain.paint.vertical;

import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.core.PickingVertex;

public class SelectingVertexForVertical extends PickingVertex {

	public SelectingVertexForVertical() {
		super();
	}

	@Override
	public void undoAction(final PaintContextInterface context) {
		context.clear(false);
	}

	@Override
	public void onResult(final PaintContextInterface context, final boolean doSpecial) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initialize() {
		setPreviousClass(this.getClass());
		setNextClass(SelectingLineForVertical.class);

//		System.out.println("SelectingFirstVertex.initialize() is called");
	}

}
