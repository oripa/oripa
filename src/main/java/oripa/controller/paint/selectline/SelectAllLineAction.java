package oripa.controller.paint.selectline;

import oripa.ORIPA;
import oripa.controller.paint.PaintContextInterface;
import oripa.domain.cptool.Painter;
import oripa.domain.creasepattern.CreasePatternInterface;

public class SelectAllLineAction extends SelectLineAction {

	public SelectAllLineAction(PaintContextInterface context) {
		super(context);
	}

	@Override
	public void recover(PaintContextInterface context) {
		Painter painter = new Painter();
		CreasePatternInterface creasePattern = ORIPA.doc.getCreasePattern();
		painter.selectAllOriLines(creasePattern);
		super.recover(context);
	}
	


}
