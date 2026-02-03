package oripa.domain.paint.line;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.PickingVertex;

public class SelectingFirstVertexForLine extends PickingVertex {

    public SelectingFirstVertexForLine() {
        super();
    }

    @Override
    public void undoAction(final PaintContext context) {
        context.clear(false);
    }

    @Override
    public void onResult(final PaintContext context, final boolean doSpecial) {

    }

    @Override
    protected void initialize() {
        setPreviousClass(this.getClass());
        setNextClass(SelectingSecondVertexForLine.class);
    }

}
