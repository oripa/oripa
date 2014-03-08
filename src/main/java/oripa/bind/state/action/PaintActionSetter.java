package oripa.bind.state.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import oripa.controller.paint.GraphicMouseActionInterface;
import oripa.controller.paint.MouseActionHolder;
import oripa.controller.paint.PaintContextInterface;
import oripa.controller.paint.ScreenUpdaterInterface;
import oripa.viewsetting.main.ScreenUpdater;

/**
 * Add this listener to Button object or something for selecting paint action.
 * 
 * @author koji
 * 
 */
public class PaintActionSetter implements ActionListener {

	private final GraphicMouseActionInterface mouseAction;
	private final MouseActionHolder actionHolder;
	private final PaintContextInterface context;

	public PaintActionSetter(final MouseActionHolder anActionHolder,
			final GraphicMouseActionInterface thisMouseAction,
			final PaintContextInterface aContext) {
		actionHolder = anActionHolder;
		mouseAction = thisMouseAction;
		context = aContext;
	}

	@Override
	public void actionPerformed(final ActionEvent e) {

		GraphicMouseActionInterface currentAction = actionHolder
				.getMouseAction();
		currentAction.destroy(context);
		mouseAction.recover(context);

		actionHolder.setMouseAction(mouseAction);

		if (mouseAction.needSelect() == false) {
			context.getPainter().resetSelectedOriLines();
		}

		ScreenUpdaterInterface screenUpdater = ScreenUpdater.getInstance();
		screenUpdater.updateScreen();
	}

}
