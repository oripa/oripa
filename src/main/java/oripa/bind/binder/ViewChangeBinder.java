package oripa.bind.binder;

import javax.swing.AbstractButton;

import oripa.paint.ScreenUpdaterInterface;
import oripa.viewsetting.ChangeViewSetting;
import oripa.viewsetting.ViewChangeListener;
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
		ScreenUpdaterInterface screenUpdater = ScreenUpdater.getInstance();
		button.addKeyListener(screenUpdater.getKeyListener());

		button.addActionListener(new ViewChangeListener(target));
		
		return button;
	}


}
