package oripa.bind.binder;

import javax.swing.AbstractButton;

import oripa.viewsetting.ChangeViewSetting;
import oripa.viewsetting.ViewChangeListener;
import oripa.viewsetting.ViewScreenUpdater;
import oripa.viewsetting.main.ScreenUpdater;

public class ViewChangeBinder extends AbstractButtonBinder<ChangeViewSetting> {

	
	public ViewChangeBinder() {
	}
	
	@Override
	public AbstractButton createButton(
			Class<? extends AbstractButton> buttonClass,
			ChangeViewSetting target, String textID) {
		AbstractButton button = createEmptyButton(buttonClass, textID);

		// For catching key actions which requires immediate drawing(e.g., for catching Ctrl pressed)
		ViewScreenUpdater screenUpdater = ScreenUpdater.getInstance();
		button.addKeyListener(screenUpdater.getKeyListener());

		button.addActionListener(new ViewChangeListener(target));
		
		return button;
	}


}
