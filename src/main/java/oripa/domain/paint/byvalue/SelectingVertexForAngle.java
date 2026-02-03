package oripa.domain.paint.byvalue;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.PickingVertex;
import oripa.util.Command;

public class SelectingVertexForAngle extends PickingVertex {

    private final ByValueContext valueSetting;

    public SelectingVertexForAngle(final ByValueContext valueSetting) {
        super();
        this.valueSetting = valueSetting;
    }

    @Override
    protected void initialize() {
    }

    @Override
    public void onResult(final PaintContext context, final boolean doSpecial) {
        if (context.getVertexCount() < 3) {
            return;
        }

        Command command = new AngleMeasureCommand(context, valueSetting);
        command.execute();
    }

}
