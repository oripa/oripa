package oripa.domain.paint.symmetric;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.PickingVertex;
import oripa.util.Command;

public class SelectingVertexForSymmetric extends PickingVertex {

	public SelectingVertexForSymmetric() {
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

		Command command = new SymmetricLineAdderCommand(context, doSpecial);
		command.execute();
	}

}
