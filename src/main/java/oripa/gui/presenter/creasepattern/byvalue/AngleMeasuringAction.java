package oripa.gui.presenter.creasepattern.byvalue;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.byvalue.ByValueContext;
import oripa.domain.paint.byvalue.SelectingVertexForAngle;
import oripa.gui.presenter.creasepattern.AbstractGraphicMouseAction;
import oripa.gui.presenter.creasepattern.CreasePatternViewContext;
import oripa.gui.presenter.creasepattern.GraphicMouseAction;
import oripa.gui.view.creasepattern.ObjectGraphicDrawer;

public class AngleMeasuringAction extends AbstractGraphicMouseAction {

    private final ByValueContext byValueContext;

    public AngleMeasuringAction(final ByValueContext byValueContext) {
        setActionState(new SelectingVertexForAngle(byValueContext));
        this.byValueContext = byValueContext;
    }

    @Override
    public GraphicMouseAction onLeftClick(final CreasePatternViewContext viewContext,
            final PaintContext paintContext,
            final boolean differentAction) {
        int vertexCountBeforeAction = paintContext.getVertexCount();

        GraphicMouseAction action;
        action = super.onLeftClick(viewContext, paintContext, differentAction);

        int vertexCountAfterAction = paintContext.getVertexCount();

        if (isActionPerformed(vertexCountBeforeAction, vertexCountAfterAction)) {
            action = new LineByValueAction(byValueContext);
        }

        return action;
    }

    private boolean isActionPerformed(final int countBeforeAction, final int countAfterAction) {
        return countBeforeAction > 0 && countAfterAction == 0;
    }

    @Override
    public void onDraw(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
            final PaintContext paintContext) {

        drawPickCandidateVertex(drawer, viewContext, paintContext);

        super.onDraw(drawer, viewContext, paintContext);
    }
}
