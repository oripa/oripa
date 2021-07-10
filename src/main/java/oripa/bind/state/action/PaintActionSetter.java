package oripa.bind.state.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import oripa.domain.paint.PaintContext;
import oripa.gui.presenter.creasepattern.GraphicMouseAction;
import oripa.gui.presenter.creasepattern.MouseActionHolder;
import oripa.gui.presenter.creasepattern.ScreenUpdaterInterface;

/**
 * Add this listener to Button object or something for selecting paint action.
 *
 * @author koji
 *
 */
public class PaintActionSetter implements ActionListener {

	private final GraphicMouseAction mouseAction;
	private final MouseActionHolder actionHolder;
	private final ScreenUpdaterInterface screenUpdater;
	private final PaintContext context;

	public PaintActionSetter(final MouseActionHolder anActionHolder,
			final GraphicMouseAction thisMouseAction,
			final ScreenUpdaterInterface screenUpdater,
			final PaintContext aContext) {
		actionHolder = anActionHolder;
		mouseAction = thisMouseAction;
		this.screenUpdater = screenUpdater;
		context = aContext;
	}

	@Override
	public void actionPerformed(final ActionEvent e) {

		GraphicMouseAction currentAction = actionHolder
				.getMouseAction();
		if (currentAction != null) {
			currentAction.destroy(context);
		}
		mouseAction.recover(context);

		actionHolder.setMouseAction(mouseAction);

		screenUpdater.updateScreen();
	}

}
