package oripa.gui.presenter.creasepattern;

import java.util.Optional;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.symmetric.SelectingVertexForSymmetric;
import oripa.gui.view.creasepattern.ObjectGraphicDrawer;
import oripa.vecmath.Vector2d;

public class SymmetricalLineAction extends AbstractGraphicMouseAction {

    public SymmetricalLineAction() {
        setActionState(new SelectingVertexForSymmetric());
    }

    @Override
    public Optional<Vector2d> onMove(
            final CreasePatternViewContext viewContext, final PaintContext paintContext,
            final boolean differentAction) {

        if (paintContext.getVertexCount() < 2) {
            return super.onMove(viewContext, paintContext, differentAction);
        }

        // enable auto-walk selection only
        return super.onMove(viewContext, paintContext, false);
    }

    @Override
    public void onDraw(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
            final PaintContext paintContext) {

        drawPickCandidateVertex(drawer, viewContext, paintContext);

        super.onDraw(drawer, viewContext, paintContext);
    }
}
