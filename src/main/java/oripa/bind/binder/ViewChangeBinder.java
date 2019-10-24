package oripa.bind.binder;

import java.awt.event.KeyListener;

import javax.swing.AbstractButton;

import oripa.viewsetting.ChangeViewSetting;

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
		button.addActionListener(event -> target.changeViewSetting());

		return button;
	}

}
