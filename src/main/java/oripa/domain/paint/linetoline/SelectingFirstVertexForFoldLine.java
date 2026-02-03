package oripa.domain.paint.linetoline;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.PickingVertex;

public class SelectingFirstVertexForFoldLine extends PickingVertex {

    @Override
    protected void initialize() {
        setPreviousClass(SelectingSecondLine.class);
        setNextClass(SelectingSecondVertexForFoldLine.class);
    }

    @Override
    protected void onResult(final PaintContext context, final boolean doSpecial) {

    }

    @Override
    protected void undoAction(final PaintContext context) {
        context.clearSnapPoints();
        context.popLine();
    }

}
