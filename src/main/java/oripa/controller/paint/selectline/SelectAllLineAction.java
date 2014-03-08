package oripa.controller.paint.selectline;

import oripa.controller.paint.PaintContextInterface;
import oripa.domain.cptool.Painter;

public class SelectAllLineAction extends SelectLineAction {

	public SelectAllLineAction(final PaintContextInterface context) {
		super(context);
	}

	@Override
	public void recover(final PaintContextInterface context) {
		Painter painter = context.getPainter();
		painter.selectAllOriLines();
		super.recover(context);
	}

}
