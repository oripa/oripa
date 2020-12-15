package oripa.application.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.cptool.Painter;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.ScreenUpdaterInterface;

public class DeleteSelectedLinesActionListener implements ActionListener {
	private static final Logger logger = LoggerFactory
			.getLogger(DeleteSelectedLinesActionListener.class);
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

		try {
			painter.removeSelectedLines();
		} catch (Exception ex) {
			logger.error("error when deleting selected lines", ex);
		}
		if (context.isPasting() == false) {
			context.clear(false);
		}

		screenUpdater.updateScreen();

	}

}
