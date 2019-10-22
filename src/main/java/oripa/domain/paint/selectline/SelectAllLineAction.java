package oripa.domain.paint.selectline;

import oripa.domain.cptool.Painter;
import oripa.domain.paint.PaintContextInterface;

public class SelectAllLineAction extends SelectLineAction {

	public SelectAllLineAction(final PaintContextInterface context) {
		super();
	}

	@Override
	protected void recoverImpl(final PaintContextInterface context) {
		Painter painter = context.getPainter();
		painter.selectAllOriLines();
	}

}
