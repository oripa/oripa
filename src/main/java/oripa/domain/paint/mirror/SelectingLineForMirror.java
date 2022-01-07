package oripa.domain.paint.mirror;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.selectline.SelectingLine;
import oripa.util.Command;

public class SelectingLineForMirror extends SelectingLine {

	public SelectingLineForMirror() {
		super();
	}

	@Override
	protected void onResult(final PaintContext context, final boolean doSpecial) {
		if (doSpecial) {
			Command command = new LineMirrorCommand(context);
			command.execute();
		} else {
			super.onResult(context, false);
		}
	}

}
