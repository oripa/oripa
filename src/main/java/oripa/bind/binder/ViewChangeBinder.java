package oripa.bind.binder;

import java.awt.event.KeyListener;

import javax.swing.AbstractButton;

import oripa.gui.viewsetting.ChangeViewSetting;

public class ViewChangeBinder extends AbstractButtonBinder<ChangeViewSetting> {

	public ViewChangeBinder() {
	}

	@Override
	public AbstractButton createButton(
			final Class<? extends AbstractButton> buttonClass,
			final ChangeViewSetting target, final String textID,
			final KeyListener keyListener) {
		AbstractButton button = createEmptyButton(buttonClass, textID);

		if (keyListener != null) {
			button.addKeyListener(keyListener);
		}

		if (target != null) {
			button.addActionListener(event -> target.changeViewSetting());
		}

		return button;
	}

}
