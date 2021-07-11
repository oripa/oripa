package oripa.gui.presenter.creasepattern;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import oripa.domain.paint.PaintContext;
import oripa.domain.paint.deleteline.SelectedLineDeleterCommand;

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
		var command = new SelectedLineDeleterCommand(context);
		command.execute();

		screenUpdater.updateScreen();
	}

}
