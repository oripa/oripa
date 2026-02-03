package oripa.gui.presenter.creasepattern.copypaste;

import oripa.appstate.StatePopper;
import oripa.domain.paint.PaintContext;
import oripa.domain.paint.copypaste.SelectionOriginHolder;
import oripa.gui.presenter.creasepattern.CreasePatternViewContext;
import oripa.gui.presenter.creasepattern.EditMode;

public class CopyAndPasteActionWrapper extends CopyAndPasteAction {

    private final StatePopper<EditMode> statePopper;

    public CopyAndPasteActionWrapper(
            final StatePopper<EditMode> statePopper,
            final SelectionOriginHolder originHolder) {

        super(originHolder, new PasteAction(originHolder));

        this.statePopper = statePopper;
    }

    @Override
    protected void recoverImpl(final PaintContext context) {
        super.recoverImpl(context);
    }

    @Override
    public void onRightClick(final CreasePatternViewContext viewContext, final PaintContext paintContext,
            final boolean differentAction) {
        statePopper.run();
    }

}
