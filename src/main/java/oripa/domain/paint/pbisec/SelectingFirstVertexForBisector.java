package oripa.domain.paint.pbisec;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.PickingVertex;

public class SelectingFirstVertexForBisector extends PickingVertex {

	public SelectingFirstVertexForBisector() {
		super();
	}

	@Override
	public void onResult(final PaintContext context, final boolean doSpecial) {

	}

	@Override
	protected void initialize() {
		setPreviousClass(this.getClass());
		setNextClass(SelectingSecondVertexForBisector.class);

//		System.out.println("SelectingFirstVertex.initialize() is called");
	}

}
