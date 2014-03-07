package oripa.bind.state.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import oripa.ORIPA;
import oripa.controller.paint.GraphicMouseActionInterface;
import oripa.controller.paint.MouseActionHolder;
import oripa.controller.paint.PaintContextInterface;
import oripa.controller.paint.ScreenUpdaterInterface;
import oripa.controller.paint.core.PaintContext;
import oripa.domain.cptool.Painter;
import oripa.domain.creasepattern.CreasePatternInterface;
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

	public PaintActionSetter(MouseActionHolder actionHolder,
			GraphicMouseActionInterface mouseAction) {
		this.actionHolder = actionHolder;
		this.mouseAction = mouseAction;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		PaintContextInterface context = PaintContext.getInstance();

		GraphicMouseActionInterface currentAction = actionHolder
				.getMouseAction();
		currentAction.destroy(context);
		mouseAction.recover(context);

		actionHolder.setMouseAction(mouseAction);

		if (mouseAction.needSelect() == false) {
			CreasePatternInterface creasePattern = ORIPA.doc.getCreasePattern();
			Painter painter = new Painter();
			painter.resetSelectedOriLines(creasePattern);
		}

		ScreenUpdaterInterface screenUpdater = ScreenUpdater.getInstance();
		screenUpdater.updateScreen();
	}

}
