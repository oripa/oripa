package oripa.gui.presenter.creasepattern.copypaste;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.copypaste.SelectionOriginHolder;

/**
 * This class assumes lines to be pasted have been put to paint context.
 *
 * @author OUCHI Koji
 *
 */
public class ImportAction extends PasteAction {

	public ImportAction(
			final SelectionOriginHolder originHolder) {

		super(originHolder);
	}

	@Override
	protected void recoverImpl(final PaintContext context) {
		context.loadFromImportedLines();
		context.clearImportedLines();
		context.startPasting();
	}
}
