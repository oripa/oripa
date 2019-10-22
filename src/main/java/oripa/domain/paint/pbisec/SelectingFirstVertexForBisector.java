package oripa.domain.paint.pbisec;

import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.core.PickingVertex;

public class SelectingFirstVertexForBisector extends PickingVertex {

	public SelectingFirstVertexForBisector() {
		super();
	}

	@Override
	public void onResult(final PaintContextInterface context, final boolean doSpecial) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initialize() {
		setPreviousClass(this.getClass());
		setNextClass(SelectingSecondVertexForBisector.class);

//		System.out.println("SelectingFirstVertex.initialize() is called");
	}

}
