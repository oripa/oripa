package oripa.controller.paint.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import oripa.controller.paint.PaintContextInterface;
import oripa.controller.paint.ScreenUpdaterInterface;
import oripa.controller.paint.core.PaintContext;
import oripa.domain.cptool.Painter;
import oripa.viewsetting.main.ScreenUpdater;

public class DeleteSelectedLines implements ActionListener {

	private PaintContextInterface context = PaintContext.getInstance();

	public DeleteSelectedLines(final PaintContextInterface aContext) {
		context = aContext;
	}

	@Override
	public void actionPerformed(final ActionEvent e) {

		context.getUndoer().pushUndoInfo();

		Painter painter = context.getPainter();
		painter.removeSelectedLines();

		if (context.isPasting() == false) {
			context.clear(false);
		}

		ScreenUpdaterInterface screenUpdater = ScreenUpdater.getInstance();

		screenUpdater.updateScreen();

	}

}
