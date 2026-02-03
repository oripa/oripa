package oripa.gui.presenter.creasepattern;

import java.util.Optional;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.linetoline.SelectingFirstLine;
import oripa.gui.presenter.creasepattern.geometry.NearestItemFinder;
import oripa.gui.view.creasepattern.ObjectGraphicDrawer;
import oripa.vecmath.Vector2d;

public class LineToLineAxiomAction extends AbstractGraphicMouseAction {

    public LineToLineAxiomAction() {
        setActionState(new SelectingFirstLine());
    }

    @Override
    protected void recoverImpl(final PaintContext context) {
        super.recoverImpl(context);
        setActionState(new SelectingFirstLine());
    }

    @Override
    public Optional<Vector2d> onMove(final CreasePatternViewContext viewContext, final PaintContext paintContext,
            final boolean differentAction) {
        if (paintContext.getLineCount() < 2) {
            return super.onMove(viewContext, paintContext, differentAction);
        }

        var snapPointOpt = NearestItemFinder.getNearestInSnapPoints(viewContext, paintContext);

        snapPointOpt.ifPresent(snapPoint -> paintContext.setCandidateVertexToPick(snapPoint));

        return snapPointOpt;
    }

    @Override
    public void onDraw(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
            final PaintContext paintContext) {

        if (paintContext.getLineCount() < 2) {
            drawPickCandidateLine(drawer, viewContext, paintContext);
        }

        if (paintContext.getLineCount() == 2) {
            drawSnapPoints(drawer, viewContext, paintContext);
            drawPickCandidateVertex(drawer, viewContext, paintContext);
        }

        if (paintContext.getVertexCount() == 1) {
            drawTemporaryLine(drawer, viewContext, paintContext);
        }

        super.onDraw(drawer, viewContext, paintContext);
    }
}
