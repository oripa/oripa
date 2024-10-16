package oripa.gui.presenter.creasepattern;

import java.util.Optional;

import oripa.domain.paint.PaintContext;
import oripa.gui.view.creasepattern.ObjectGraphicDrawer;
import oripa.vecmath.Vector2d;

/**
 * Interface for GUI interaction specified for ORIPA.
 *
 * @author OUCHI Koji
 *
 */
public interface GraphicMouseAction {

	/**
	 * True if the implementation uses line-selected marks set by previous
	 * action. default is false.
	 *
	 * @return
	 */
	default boolean needSelect() {
		return false;
	}

	/**
	 * True if the implementation uses Ctrl key to switch behavior on drag.
	 * default is false.
	 *
	 * @return
	 */
	default boolean isUsingCtrlKeyOnDrag() {
		return false;
	}

	/**
	 * The type of this action.
	 */
	abstract EditMode getEditMode();

	/**
	 * Define action on destroy. This method is expected to be called when the
	 * action is switched, before recover() of new action.
	 *
	 * @param context
	 */
	abstract void destroy(PaintContext context);

	/**
	 * Define action for recovering the status of this object with given
	 * context. This method should be called when the action is switched, after
	 * destroy() of old action.
	 *
	 * @param context
	 */
	abstract void recover(PaintContext context);

	/**
	 * Performs action. The default implementation calls
	 * {@link #doAction(PaintContext, Vector2d, boolean)}.
	 *
	 * @param viewContext
	 * @param paintContext
	 * @param differentAction
	 *
	 * @return Next mouse action.
	 */
	default GraphicMouseAction onLeftClick(
			final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean differentAction) {
		var clickPoint = viewContext.getLogicalMousePoint();

		doAction(paintContext, clickPoint, differentAction);
		return this;
	}

	/**
	 * Do the action for left click.
	 *
	 * @param context
	 * @param point
	 *            the point that button is clicked at.
	 * @param differntAction
	 */
	abstract void doAction(PaintContext context, Vector2d point,
			boolean differntAction);

	/**
	 * Does undo action. The default implementation calls
	 * {@link #undo(PaintContext)}.
	 *
	 * @param viewContext
	 * @param paintContext
	 * @param differentAction
	 */
	default void onRightClick(final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean doSpecial) {
		undo(paintContext);
	}

	abstract void undo(PaintContext context);

	abstract void redo(PaintContext context);

	/**
	 * Defines the behavior when the mouse moves. At least this method should
	 * search a vertex close enough to the mouse cursor. The result must be
	 * stored into candidateVertexToPick properties of {@code paintContext}.
	 *
	 * @param viewContext
	 * @param paintContext
	 * @param differentAction
	 * @return candidate vertex. Empty if not found.
	 */
	abstract Optional<Vector2d> onMove(final CreasePatternViewContext viewContext,
			final PaintContext paintContext, boolean differentAction);

	/**
	 * Defines the behavior when the left mouse button gets down.
	 *
	 * @param viewContext
	 * @param paintContext
	 * @param differentAction
	 */
	abstract void onPress(final CreasePatternViewContext viewContext, final PaintContext paintContext,
			boolean differentAction);

	/**
	 * Defines the behavior when the mouse is dragged with left button.
	 *
	 * @param viewContext
	 * @param paintContext
	 * @param differentAction
	 */
	abstract void onDrag(final CreasePatternViewContext viewContext, final PaintContext paintContext,
			boolean differentAction);

	/**
	 * Defines the behavior when the left mouse button gets released.
	 *
	 * @param viewContext
	 * @param paintContext
	 * @param differentAction
	 */
	abstract void onRelease(final CreasePatternViewContext viewContext, final PaintContext paintContext,
			boolean differentAction);

	/**
	 * Draw the result of event processing using {@code drawer}. Basically the
	 * implementation should draw the lines and vertices in {@code paintContext}
	 * and doesn't have to draw existing lines and vertices. Of course other
	 * drawing is allowed to make the interaction fancier.
	 *
	 * @param drawer
	 * @param viewContext
	 * @param paintContext
	 */
	abstract void onDraw(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
			final PaintContext paintContext);

}