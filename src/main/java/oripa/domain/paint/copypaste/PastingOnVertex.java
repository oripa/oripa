package oripa.domain.paint.copypaste;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.PickingVertex;
import oripa.util.Command;

public class PastingOnVertex extends PickingVertex {

	private final SelectionOriginHolder originHolder;
	private final ShiftedLineFactory factory = new ShiftedLineFactory();

	/**
	 * Constructor
	 */
	public PastingOnVertex(final SelectionOriginHolder originHolder) {
		this.originHolder = originHolder;
	}

	@Override
	protected void initialize() {
	}

	@Override
	protected void undoAction(final PaintContext context) {
		// context.setMissionCompleted(false);
		context.creasePatternUndo().undo();
	}

	@Override
	protected void onResult(final PaintContext context, final boolean doSpecial) {
		if (context.getVertexCount() != 1) {
			throw new IllegalStateException("Wrong state: impossible selection.");
		}

		Command command = new LinePasterCommand(context, originHolder, factory);
		command.execute();
	}
}
