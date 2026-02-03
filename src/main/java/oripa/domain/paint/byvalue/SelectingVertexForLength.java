package oripa.domain.paint.byvalue;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.PickingVertex;
import oripa.util.Command;

public class SelectingVertexForLength extends PickingVertex {

    private final ByValueContext valueSetting;

    public SelectingVertexForLength(final ByValueContext valueSetting) {
        super();
        this.valueSetting = valueSetting;
    }

    @Override
    protected void initialize() {
    }

    @Override
    public void onResult(final PaintContext context, final boolean doSpecial) {
        if (context.getVertexCount() < 2) {
            return;
        }

        Command command = new LengthMeasureCommand(context, valueSetting);
        command.execute();
    }

}
