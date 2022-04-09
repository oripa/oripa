package oripa.gui.bind;

import java.awt.Component;
import java.awt.event.KeyListener;

import javax.swing.AbstractButton;

import oripa.appstate.ApplicationState;
import oripa.domain.paint.PaintContext;
import oripa.gui.bind.binder.ApplicationStateButtonBinder;
import oripa.gui.bind.state.PaintBoundStateFactory;
import oripa.gui.presenter.creasepattern.EditMode;
import oripa.gui.presenter.creasepattern.MouseActionHolder;
import oripa.gui.presenter.creasepattern.ScreenUpdater;

/**
 * A class for application-specific binding of state actions and buttons.
 *
 * @author koji
 *
 */
public class PaintActionButtonFactory implements ButtonFactory {

	private final PaintBoundStateFactory stateFactory;
	private final PaintContext context;
	private final MouseActionHolder actionHolder;
	private final ScreenUpdater screenUpater;

	public PaintActionButtonFactory(
			final PaintBoundStateFactory stateFactory,
			final PaintContext aContext,
			final MouseActionHolder actionHolder,
			final ScreenUpdater screenUpater) {
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
	public <TButton extends AbstractButton> TButton create(final Component parent,
			final Class<TButton> buttonClass,
			final String id,
			final KeyListener keyListener) throws IllegalArgumentException {

		ApplicationState<EditMode> state = stateFactory.create(
				parent, actionHolder, context, screenUpater, id);

		if (state == null) {
			throw new IllegalArgumentException("Wrong ID for creating state");
		}

		ApplicationStateButtonBinder paintBinder = new ApplicationStateButtonBinder();
		TButton button = paintBinder.createButton(buttonClass, state, id, keyListener);

		return button;
	}

}
