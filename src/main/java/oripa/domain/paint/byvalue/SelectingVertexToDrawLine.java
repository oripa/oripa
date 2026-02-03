package oripa.domain.paint.byvalue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.PickingVertex;
import oripa.util.Command;

public class SelectingVertexToDrawLine extends PickingVertex {
    private static final Logger logger = LoggerFactory.getLogger(SelectingVertexToDrawLine.class);

    private final ByValueContext valueSetting;

    /**
     * Constructor
     */
    public SelectingVertexToDrawLine(final ByValueContext valueSetting) {
        super();
        this.valueSetting = valueSetting;
    }

    @Override
    protected void initialize() {

    }

    @Override
    protected void onResult(final PaintContext context, final boolean doSpecial) {
        logger.debug("start onResult()");

        Command command = new LineByValueCommand(context, valueSetting);
        command.execute();

        logger.debug("end onResult()");

    }

}
