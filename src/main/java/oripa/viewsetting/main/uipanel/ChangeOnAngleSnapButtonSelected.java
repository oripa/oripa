package oripa.viewsetting.main.uipanel;

import oripa.viewsetting.ChangeViewSetting;

public class ChangeOnAngleSnapButtonSelected implements ChangeViewSetting {
	private final UIPanelSetting setting;

	/**
	 * Constructor
	 */
	public ChangeOnAngleSnapButtonSelected(final UIPanelSetting uiPanelSetting) {
		setting = uiPanelSetting;
	}

	@Override
	public void changeViewSetting() {
		setting.selectInputMode();

		setting.setByValuePanelVisible(false);
		setting.setAngleStepVisible(true);

		setting.setAlterLineTypePanelVisible(false);

		setting.setMountainButtonEnabled(true);
		setting.setValleyButtonEnabled(true);
		setting.setAuxButtonEnabled(true);

	}

}
