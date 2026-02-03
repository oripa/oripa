package oripa.domain.paint.outline;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.PickingVertex;
import oripa.util.Command;

public class SelectingVertexForOutline extends PickingVertex {
    private final CloseTempOutlineFactory closeTempOutlineFactory;

    /**
     * Constructor
     */
    public SelectingVertexForOutline(final CloseTempOutlineFactory factory) {
        super();
        this.closeTempOutlineFactory = factory;
    }

    @Override
    protected void initialize() {

    }

    @Override
    protected boolean onAct(final PaintContext context, final boolean freeSelection) {
        return super.onAct(context, freeSelection);
    }

    @Override
    protected void onResult(final PaintContext context, final boolean doSpecial) {
        Command command = new OutlineEditerCommand(context, closeTempOutlineFactory);
        command.execute();
    }
}
