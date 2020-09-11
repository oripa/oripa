package oripa.viewsetting.main.uipanel;

import oripa.viewsetting.ChangeViewSetting;

public class ChangeOnByValueButtonSelected implements ChangeViewSetting {
	private final UIPanelSetting setting;

	/**
	 * Constructor
	 */
	public ChangeOnByValueButtonSelected(final UIPanelSetting uiPanelSetting) {
		setting = uiPanelSetting;
	}

	@Override
	public void changeViewSetting() {
		setting.selectInputMode();

		setting.setByValuePanelVisible(true);
		setting.setAlterLineTypePanelVisible(false);
		setting.setMountainButtonEnabled(true);
		setting.setValleyButtonEnabled(true);
		setting.setAuxButtonEnabled(true);
	}

}
