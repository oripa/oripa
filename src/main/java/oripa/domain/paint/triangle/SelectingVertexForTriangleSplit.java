package oripa.domain.paint.triangle;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.PickingVertex;
import oripa.util.Command;

public class SelectingVertexForTriangleSplit extends PickingVertex {

	public SelectingVertexForTriangleSplit() {
		super();
	}

	@Override
	protected void initialize() {
	}

	@Override
	protected void onResult(final PaintContext context, final boolean doSpecial) {
		if (context.getVertexCount() < 3) {
			return;
		}

		Command command = new TriangleSplitAdderCommand(context);
		command.execute();
	}
}
