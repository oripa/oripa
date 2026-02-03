package oripa.gui.presenter.creasepattern;

import oripa.appstate.StatePopper;
import oripa.domain.paint.PaintContext;

public class EditOutlineActionWrapper extends EditOutlineAction {

    private final StatePopper<EditMode> statePopper;
    private final MouseActionHolder actionHolder;

    public EditOutlineActionWrapper(final StatePopper<EditMode> statePopper,
            final MouseActionHolder actionHolder) {
        this.statePopper = statePopper;
        this.actionHolder = actionHolder;
    }

    @Override
    public GraphicMouseAction onLeftClick(
            final CreasePatternViewContext viewContext, final PaintContext paintContext,
            final boolean differentAction) {
        int vertexCountBeforeAction = paintContext.getVertexCount();

        GraphicMouseAction next = super.onLeftClick(viewContext, paintContext,
                differentAction);

        int vertexCountAfterAction = paintContext.getVertexCount();

        // Action is performed and the selection is cleared.
        // It's the time to get back to previous graphic action.
        if (isActionPerformed(vertexCountBeforeAction, vertexCountAfterAction)) {
            popPreviousState();
            next = actionHolder.getMouseAction().get();
        }

        return next;
    }

    private boolean isActionPerformed(final int countBeforeAction, final int countAfterAction) {
        return countBeforeAction > 0 && countAfterAction == 0;
    }

    @Override
    public void onRightClick(final CreasePatternViewContext viewContext, final PaintContext paintContext,
            final boolean differentAction) {

        popPreviousState();
    }

    private void popPreviousState() {
        statePopper.run();
    }

}
