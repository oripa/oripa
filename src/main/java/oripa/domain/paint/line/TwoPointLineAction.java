package oripa.domain.paint.line;

import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.segment.TwoPointSegmentAction;

public class TwoPointLineAction extends TwoPointSegmentAction {

	public TwoPointLineAction() {
		setActionState(new SelectingFirstVertexForLine());
	}

	@Override
	public void destroy(final PaintContextInterface context) {
		// TODO Auto-generated method stub
		super.destroy(context);
	}

	@Override
	public void recover(final PaintContextInterface context) {
		context.clear(true);
		setActionState(new SelectingFirstVertexForLine());

	}

}
