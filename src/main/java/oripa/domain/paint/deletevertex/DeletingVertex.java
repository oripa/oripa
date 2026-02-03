package oripa.domain.paint.deletevertex;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.PickingVertex;
import oripa.util.Command;

public class DeletingVertex extends PickingVertex {
    @Override
    protected void initialize() {

    }

    @Override
    protected void onResult(final PaintContext context, final boolean doSpecial) {
        Command command = new VertexDeleterCommand(context);
        command.execute();
    }
}
