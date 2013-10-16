package oripa.paint.selectline;

import oripa.ORIPA;
import oripa.paint.core.PaintContext;

public class SelectAllLineAction extends SelectLineAction {

	public SelectAllLineAction(PaintContext context) {
		super(context);
	}

	@Override
	public void recover(PaintContext context) {
		ORIPA.doc.selectAllOriLines();
		super.recover(context);
	}
	


}
