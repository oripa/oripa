package oripa.controller.paint.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import oripa.controller.paint.PaintContextInterface;
import oripa.controller.paint.ScreenUpdaterInterface;
import oripa.domain.cptool.Painter;

public class DeleteSelectedLines implements ActionListener {

	private final PaintContextInterface context;
	private final ScreenUpdaterInterface screenUpdater;

	public DeleteSelectedLines(final PaintContextInterface aContext,
			final ScreenUpdaterInterface updater) {
		context = aContext;
		screenUpdater = updater;
	}

	@Override
	public void actionPerformed(final ActionEvent e) {

		context.creasePatternUndo().pushUndoInfo();

		Painter painter = context.getPainter();
		painter.removeSelectedLines();

		if (context.isPasting() == false) {
			context.clear(false);
		}

		screenUpdater.updateScreen();

	}

}
