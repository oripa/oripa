package oripa.paint.selectline;

import oripa.ORIPA;
import oripa.paint.core.PaintContext;
import oripa.paint.creasepattern.CreasePattern;
import oripa.paint.creasepattern.Painter;

public class SelectAllLineAction extends SelectLineAction {

	public SelectAllLineAction(PaintContext context) {
		super(context);
	}

	@Override
	public void recover(PaintContext context) {
		Painter painter = new Painter();
		CreasePattern creasePattern = ORIPA.doc.getCreasePattern();
		painter.selectAllOriLines(creasePattern);
		super.recover(context);
	}
	


}
