package oripa.viewsetting.main.uipanel;

import oripa.viewsetting.ChangeViewSetting;

public class ChangeOnAlterTypeButtonSelected implements ChangeViewSetting {
	private final UIPanelSetting setting;

	/**
	 * Constructor
	 */
	public ChangeOnAlterTypeButtonSelected(final UIPanelSetting uiPanelSetting) {
		this.setting = uiPanelSetting;
	}

	@Override
	public void changeViewSetting() {

		setting.setByValuePanelVisible(false);
		setting.setAngleStepVisible(false);

		setting.setAlterLineTypePanelVisible(true);

		setting.setMountainButtonEnabled(false);
		setting.setValleyButtonEnabled(false);
		setting.setAuxButtonEnabled(false);

	}

}
