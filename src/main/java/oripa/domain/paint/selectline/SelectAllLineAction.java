package oripa.domain.paint.selectline;

import oripa.domain.cptool.Painter;
import oripa.domain.paint.PaintContextInterface;

@Deprecated
public class SelectAllLineAction extends SelectLineAction {

	public SelectAllLineAction() {
		super();
	}

	/*
	 * (non Javadoc)
	 *
	 * @see
	 * oripa.domain.paint.selectline.SelectLineAction#undo(oripa.domain.paint.
	 * PaintContextInterface)
	 */
	@Override
	public void undo(final PaintContextInterface context) {
		context.creasePatternUndo().undo();

		super.recoverImpl(context);

	}

	@Override
	protected void recoverImpl(final PaintContextInterface context) {
		context.creasePatternUndo().pushUndoInfo();

		Painter painter = context.getPainter();
		painter.selectAllOriLines();

		context.getCreasePattern().stream()
				.filter(line -> line.selected).forEach(line -> context.pushLine(line));
	}

}
