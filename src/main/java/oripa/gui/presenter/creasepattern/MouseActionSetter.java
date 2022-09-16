package oripa.gui.presenter.creasepattern;

import oripa.domain.paint.PaintContext;

/**
 * Add this listener to Button object or something for selecting paint action.
 *
 * @author koji
 *
 */
public class MouseActionSetter implements Runnable {

	private final GraphicMouseAction mouseAction;
	private final MouseActionHolder actionHolder;
	private final ScreenUpdater screenUpdater;
	private final PaintContext context;

	public MouseActionSetter(final MouseActionHolder anActionHolder,
			final GraphicMouseAction thisMouseAction,
			final ScreenUpdater screenUpdater,
			final PaintContext aContext) {
		actionHolder = anActionHolder;
		mouseAction = thisMouseAction;
		this.screenUpdater = screenUpdater;
		context = aContext;
	}

	@Override
	public void run() {

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
