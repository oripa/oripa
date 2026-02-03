package oripa.gui.presenter.creasepattern;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.triangle.SelectingVertexForTriangleSplit;
import oripa.gui.view.creasepattern.ObjectGraphicDrawer;

public class TriangleSplitAction extends AbstractGraphicMouseAction {

    public TriangleSplitAction() {
        setActionState(new SelectingVertexForTriangleSplit());
    }

    @Override
    public void onDraw(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
            final PaintContext paintContext) {

        drawPickCandidateVertex(drawer, viewContext, paintContext);

        super.onDraw(drawer, viewContext, paintContext);
    }
}
