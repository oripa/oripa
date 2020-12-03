package oripa.application.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import oripa.domain.cptool.Painter;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.ScreenUpdaterInterface;

public class DeleteSelectedLinesActionListener implements ActionListener {

	private final PaintContextInterface context;
	private final ScreenUpdaterInterface screenUpdater;

	public DeleteSelectedLinesActionListener(final PaintContextInterface aContext,
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
