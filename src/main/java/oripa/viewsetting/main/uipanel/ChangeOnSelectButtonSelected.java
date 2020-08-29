package oripa.viewsetting.main.uipanel;

import oripa.viewsetting.ChangeViewSetting;

public class ChangeOnSelectButtonSelected implements ChangeViewSetting {
	private final UIPanelSettingDB setting;

	/**
	 * Constructor
	 */
	public ChangeOnSelectButtonSelected(final UIPanelSettingDB uiPanelSetting) {
		setting = uiPanelSetting;
	}

	@Override
	public void changeViewSetting() {
		setting.selectSelectMode();

		setting.setByValuePanelVisible(false);

		setting.setAlterLineTypePanelVisible(false);

		setting.setMountainButtonEnabled(false);
		setting.setValleyButtonEnabled(false);
		setting.setAuxButtonEnabled(false);

	}

}
