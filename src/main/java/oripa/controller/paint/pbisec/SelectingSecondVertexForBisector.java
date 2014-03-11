package oripa.controller.paint.pbisec;

import javax.vecmath.Vector2d;

import oripa.controller.paint.PaintContextInterface;
import oripa.controller.paint.core.PickingVertex;
import oripa.domain.cptool.Painter;

public class SelectingSecondVertexForBisector extends PickingVertex {

	public SelectingSecondVertexForBisector() {
		super();
	}

	@Override
	public void onResult(final PaintContextInterface context) {

		if (context.getVertexCount() != 2) {
			throw new RuntimeException();
		}

		Vector2d p0, p1;
		p0 = context.getVertex(0);
		p1 = context.getVertex(1);

		context.creasePatternUndo().pushUndoInfo();

		Painter painter = context.getPainter();
		painter.addPBisector(
				p0, p1);

		context.clear(false);
	}

	@Override
	protected void initialize() {
		setPreviousClass(SelectingFirstVertexForBisector.class);
		setNextClass(SelectingFirstVertexForBisector.class);

//		System.out.println("SelectingFirstVertex.initialize() is called");
	}

}
