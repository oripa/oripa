package oripa.domain.paint;

public class BasicUndo {

	/**
	 * executes undo of the state if some vertex or line is picked. If the
	 * context has no such element (we assume it the starting point), undo of
	 * input is executed.
	 *
	 * @param state
	 * @param context
	 * @return next state defined by the given state.
	 */
	public static ActionState undo(final ActionState state, final PaintContext context) {

		if (context.getLineCount() > 0 || context.getVertexCount() > 0) {
			return state.undo(context);
		}

		context.creasePatternUndo().undo();

		return state;
	}
}
