package oripa.application.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.deleteline.SelectedLineDeleter;
import oripa.gui.presenter.creasepattern.ScreenUpdater;

public class DeleteSelectedLinesActionListener implements ActionListener {
	private final PaintContext context;
	private final ScreenUpdater screenUpdater;

	public DeleteSelectedLinesActionListener(final PaintContext aContext,
			final ScreenUpdater updater) {
		context = aContext;
		screenUpdater = updater;
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		var deleter = new SelectedLineDeleter();

		deleter.deleteSelectedLine(context);
		screenUpdater.updateScreen();
	}

}
