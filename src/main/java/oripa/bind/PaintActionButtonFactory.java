package oripa.bind;

import java.awt.Component;
import java.awt.event.KeyListener;

import javax.swing.AbstractButton;

import oripa.appstate.ApplicationState;
import oripa.bind.binder.ApplicationStateButtonBinder;
import oripa.bind.state.PaintBoundStateFactory;
import oripa.domain.paint.EditMode;
import oripa.domain.paint.MouseActionHolder;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.ScreenUpdaterInterface;
import oripa.domain.paint.copypaste.SelectionOriginHolder;
import oripa.viewsetting.main.MainFrameSetting;
import oripa.viewsetting.main.uipanel.UIPanelSetting;

/**
 * A class for application-specific binding of state actions and buttons.
 *
 * @author koji
 *
 */
public class PaintActionButtonFactory implements ButtonFactory {

	private final PaintContextInterface context;
	private final MainFrameSetting mainFrameSetting;
	private final UIPanelSetting uiPanelSetting;
	private final SelectionOriginHolder originHolder;

	public PaintActionButtonFactory(final PaintContextInterface aContext,
			final MainFrameSetting mainFrameSetting, final UIPanelSetting uiPanelSetting,
			final SelectionOriginHolder originHolder) {
		context = aContext;
		this.mainFrameSetting = mainFrameSetting;
		this.uiPanelSetting = uiPanelSetting;
		this.originHolder = originHolder;
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
			final MouseActionHolder actionHolder,
			final ScreenUpdaterInterface screenUpater,
			final String id,
			final KeyListener keyListener) {

		PaintBoundStateFactory stateFactory = new PaintBoundStateFactory(mainFrameSetting,
				uiPanelSetting, originHolder);

		ApplicationState<EditMode> state = stateFactory.create(
				parent, actionHolder, context, screenUpater, id);

		if (state == null) {
			throw new NullPointerException("Wrong ID for creating state");
		}

		ApplicationStateButtonBinder paintBinder = new ApplicationStateButtonBinder();
		AbstractButton button = paintBinder.createButton(buttonClass, state, id, keyListener);

		return button;
	}

}
