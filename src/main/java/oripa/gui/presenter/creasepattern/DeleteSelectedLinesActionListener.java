package oripa.gui.presenter.creasepattern;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.deleteline.SelectedLineDeleterCommand;

public class DeleteSelectedLinesActionListener implements Runnable {
	private final PaintContext context;
	private final ScreenUpdater screenUpdater;

	public DeleteSelectedLinesActionListener(final PaintContext aContext,
			final ScreenUpdater updater) {
		context = aContext;
		screenUpdater = updater;
	}

	@Override
	public void run() {
		var command = new SelectedLineDeleterCommand(context);
		command.execute();

		screenUpdater.updateScreen();
	}

}
