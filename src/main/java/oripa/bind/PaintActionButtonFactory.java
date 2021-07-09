package oripa.bind;

import java.awt.Component;
import java.awt.event.KeyListener;

import javax.swing.AbstractButton;

import oripa.appstate.ApplicationState;
import oripa.bind.binder.ApplicationStateButtonBinder;
import oripa.bind.state.PaintBoundStateFactory;
import oripa.domain.paint.PaintContextInterface;
import oripa.gui.presenter.creasepattern.EditMode;
import oripa.gui.presenter.creasepattern.MouseActionHolder;
import oripa.gui.presenter.creasepattern.ScreenUpdaterInterface;

/**
 * A class for application-specific binding of state actions and buttons.
 *
 * @author koji
 *
 */
public class PaintActionButtonFactory implements ButtonFactory {

	private final PaintBoundStateFactory stateFactory;
	private final PaintContextInterface context;
	private final MouseActionHolder actionHolder;
	private final ScreenUpdaterInterface screenUpater;

	public PaintActionButtonFactory(
			final PaintBoundStateFactory stateFactory,
			final PaintContextInterface aContext,
			final MouseActionHolder actionHolder,
			final ScreenUpdaterInterface screenUpater) {
		this.stateFactory = stateFactory;
		context = aContext;
		this.actionHolder = actionHolder;
		this.screenUpater = screenUpater;

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see oripa.bind.ButtonFactory#create(java.awt.Component, java.lang.Class,
	 * java.lang.String)
	 */
	@Override
	public AbstractButton create(final Component parent,
			final Class<? extends AbstractButton> buttonClass,
			final String id,
			final KeyListener keyListener) throws IllegalArgumentException {

		ApplicationState<EditMode> state = stateFactory.create(
				parent, actionHolder, context, screenUpater, id);

		if (state == null) {
			throw new IllegalArgumentException("Wrong ID for creating state");
		}

		ApplicationStateButtonBinder paintBinder = new ApplicationStateButtonBinder();
		AbstractButton button = paintBinder.createButton(buttonClass, state, id, keyListener);

		return button;
	}

}
