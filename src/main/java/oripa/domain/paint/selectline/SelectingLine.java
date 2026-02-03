package oripa.domain.paint.selectline;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.PickingLine;
import oripa.util.Command;

public class SelectingLine extends PickingLine {

    public SelectingLine() {
        super();
    }

    @Override
    protected void initialize() {
    }

    @Override
    protected void onResult(final PaintContext context, final boolean doSpecial) {
        Command command = new LineSelectionTogglerCommand(context);
        command.execute();
    }

}
