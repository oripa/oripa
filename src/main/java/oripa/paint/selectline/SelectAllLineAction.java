package oripa.paint.selectline;

import oripa.ORIPA;
import oripa.paint.CreasePatternInterface;
import oripa.paint.PaintContextInterface;
import oripa.paint.cptool.Painter;

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
