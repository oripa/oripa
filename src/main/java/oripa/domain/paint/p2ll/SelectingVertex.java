package oripa.domain.paint.p2ll;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.PickingVertex;

/**
 *
 * @author OUCHI Koji
 *
 */
public class SelectingVertex extends PickingVertex {

    @Override
    protected void initialize() {
        setNextClass(SelectingFirstLine.class);
    }

    @Override
    protected void onResult(final PaintContext context, final boolean doSpecial) {

    }

}
