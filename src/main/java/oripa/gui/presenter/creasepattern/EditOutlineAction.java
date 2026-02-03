package oripa.gui.presenter.creasepattern;

import java.util.Collection;
import java.util.List;

import oripa.domain.cptool.OverlappingLineExtractor;
import oripa.domain.paint.PaintContext;
import oripa.domain.paint.outline.CloseTempOutlineFactory;
import oripa.domain.paint.outline.IsOnTempOutlineLoop;
import oripa.domain.paint.outline.IsOutsideOfTempOutlineLoop;
import oripa.domain.paint.outline.SelectingVertexForOutline;
import oripa.domain.paint.util.PairLoop;
import oripa.gui.presenter.creasepattern.geometry.NearestItemFinder;
import oripa.gui.view.creasepattern.ObjectGraphicDrawer;
import oripa.vecmath.Vector2d;

public class EditOutlineAction extends AbstractGraphicMouseAction {

    public EditOutlineAction() {
        setActionState(new SelectingVertexForOutline(new CloseTempOutlineFactory(new IsOnTempOutlineLoop(),
                new IsOutsideOfTempOutlineLoop(), new OverlappingLineExtractor())));
        setEditMode(EditMode.OTHER);
    }

    private void drawTempOutlines(final ObjectGraphicDrawer drawer, final Collection<Vector2d> outlineVertices) {
        if (outlineVertices.size() > 1) {
            PairLoop.iterateWithCount(
                    outlineVertices, outlineVertices.size() - 1, (p0, p1) -> {
                        drawer.drawLine(p0, p1);
                        return true;
                    });
        }
    }

    @Override
    public void onDraw(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
            final PaintContext paintContext) {

        this.drawPickCandidateVertex(drawer, viewContext, paintContext);

        super.onDraw(drawer, viewContext, paintContext);

        List<Vector2d> outlinevertices = paintContext.getPickedVertices();

        // Shows the outline of the editing
        int outlineVnum = outlinevertices.size();

        if (outlineVnum != 0) {
            drawer.selectEditingOutlineColor();
            drawer.selectEditingOutlineStroke(viewContext.getScale());

            drawTempOutlines(drawer, outlinevertices);

            var cv = NearestItemFinder.getCandidateVertexOrMousePoint(viewContext, paintContext);
            drawer.drawLine(outlinevertices.get(0), cv);
            drawer.drawLine(outlinevertices.get(outlineVnum - 1), cv);
        }

    }
}
