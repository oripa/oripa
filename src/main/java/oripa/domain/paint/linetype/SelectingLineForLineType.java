package oripa.domain.paint.linetype;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.PickingLine;
import oripa.util.Command;

public class SelectingLineForLineType extends PickingLine {

	private final TypeForChangeGettable setting;

	public SelectingLineForLineType(final TypeForChangeGettable setting) {
		super();
		this.setting = setting;
	}

	@Override
	protected void initialize() {
	}

	@Override
	protected void undoAction(final PaintContext context) {
		super.undoAction(context);
	}

	@Override
	protected void onResult(final PaintContext context, final boolean doSpecial) {
		Command command = new LineTypeChangerCommand(context, setting);
		command.execute();
	}

}
