package oripa.gui.presenter.creasepattern.byvalue;

import static java.lang.Math.*;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.byvalue.ByValueContext;
import oripa.domain.paint.byvalue.SelectingVertexToDrawLine;
import oripa.gui.presenter.creasepattern.AbstractGraphicMouseAction;
import oripa.gui.presenter.creasepattern.CreasePatternViewContext;
import oripa.gui.view.creasepattern.ObjectGraphicDrawer;
import oripa.vecmath.Vector2d;

public class LineByValueAction extends AbstractGraphicMouseAction {

    private final ByValueContext byValueContext;

    public LineByValueAction(final ByValueContext byValueContext) {
        super();
        setActionState(new SelectingVertexToDrawLine(byValueContext));

        this.byValueContext = byValueContext;
    }

    @Override
    protected void recoverImpl(final PaintContext context) {
        context.clear(true);
    }

    @Override
    public void onDraw(final ObjectGraphicDrawer drawer, final CreasePatternViewContext viewContext,
            final PaintContext paintContext) {

        drawPickCandidateVertex(drawer, viewContext, paintContext);

        super.onDraw(drawer, viewContext, paintContext);

        var vOpt = paintContext.getCandidateVertexToPick();
        if (vOpt.isEmpty()) {
            return;
        }

        var v = vOpt.get();

        double angle = byValueContext.getAngle();
        double length = byValueContext.getLength();

        double radianAngle = toRadians(angle);

        drawer.selectColor(paintContext.getLineTypeOfNewLines());
        drawer.selectStroke(
                paintContext.getLineTypeOfNewLines(),
                viewContext.getScale(), viewContext.isZeroLineWidth());

        var dir = new Vector2d(cos(radianAngle), -sin(radianAngle)).multiply(length);
        var w = v.add(dir);
        drawer.drawLine(v, w);
    }
}
