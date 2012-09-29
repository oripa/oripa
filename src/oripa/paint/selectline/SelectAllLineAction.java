package oripa.paint.selectline;

import oripa.ORIPA;
import oripa.paint.PaintContext;

public class SelectAllLineAction extends SelectLineAction {

	public SelectAllLineAction(PaintContext context) {
		super(context);
		ORIPA.doc.selectAllOriLines();
	}

}
