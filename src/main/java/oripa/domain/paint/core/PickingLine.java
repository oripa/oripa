package oripa.domain.paint.core;

import oripa.domain.paint.PaintContext;

/**
 * abstract class specified for picking line.
 *
 * @author koji
 *
 */
public abstract class PickingLine extends AbstractActionState {

    public PickingLine() {
        super();
    }

    /**
     * Picks the nearest line and push it into context.
     *
     * @return true if the action succeed, false otherwise.
     */

    @Override
    protected boolean onAct(final PaintContext context, final boolean doSpecial) {
        var pickedOpt = context.getCandidateLineToPick();

        pickedOpt.ifPresent(context::pushLine);

        return pickedOpt.isPresent();
    }

    /**
     * delete from context the latest picked line.
     */
    @Override
    protected void undoAction(final PaintContext context) {
        context.popLine();
    }

}
