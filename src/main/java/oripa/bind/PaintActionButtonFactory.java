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
import oripa.viewsetting.main.MainFrameSettingDB;
import oripa.viewsetting.main.uipanel.UIPanelSettingDB;

/**
 * A class for application-specific binding of state actions and buttons.
 *
 * @author koji
 *
 */
public class PaintActionButtonFactory implements ButtonFactory {

	private final PaintContextInterface context;
	private final MainFrameSettingDB mainFrameSetting;
	private final UIPanelSettingDB uiPanelSetting;

	public PaintActionButtonFactory(final PaintContextInterface aContext,
			final MainFrameSettingDB mainFrameSetting, final UIPanelSettingDB uiPanelSetting) {
		context = aContext;
		this.mainFrameSetting = mainFrameSetting;
		this.uiPanelSetting = uiPanelSetting;
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
				uiPanelSetting);

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
