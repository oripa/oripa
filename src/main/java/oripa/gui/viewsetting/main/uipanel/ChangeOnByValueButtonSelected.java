package oripa.gui.viewsetting.main.uipanel;

import oripa.gui.viewsetting.ChangeViewSetting;

public class ChangeOnByValueButtonSelected implements ChangeViewSetting {
	private final UIPanelSetting setting;

	/**
	 * UIPanel view settings for draw line by value tool
	 */
	public ChangeOnByValueButtonSelected(final UIPanelSetting uiPanelSetting) {
		setting = uiPanelSetting;
	}

	@Override
	public void changeViewSetting() {
		setting.selectInputMode();

		setting.setByValuePanelVisible(true);
		setting.setLineInputPanelVisible(true);
		setting.setAngleStepPanelVisible(false);

		setting.setAlterLineTypePanelVisible(false);
	}

}
