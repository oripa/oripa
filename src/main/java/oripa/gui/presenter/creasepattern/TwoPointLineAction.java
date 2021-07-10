package oripa.gui.presenter.creasepattern;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.line.SelectingFirstVertexForLine;

public class TwoPointLineAction extends TwoPointSegmentAction {

	public TwoPointLineAction() {
		setActionState(new SelectingFirstVertexForLine());
	}

	@Override
	public void destroy(final PaintContext context) {
		super.destroy(context);
	}

	@Override
	protected void recoverImpl(final PaintContext context) {
		context.clear(true);
		setActionState(new SelectingFirstVertexForLine());

	}

}
