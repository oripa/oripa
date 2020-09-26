package oripa.viewsetting.main.uipanel;

import oripa.viewsetting.ChangeViewSetting;

public class ChangeOnOtherCommandButtonSelected implements ChangeViewSetting {
	private final UIPanelSetting setting;

	/**
	 * Constructor
	 */
	public ChangeOnOtherCommandButtonSelected(final UIPanelSetting uiPanelSetting) {
		setting = uiPanelSetting;
	}

	@Override
	public void changeViewSetting() {
		setting.setByValuePanelVisible(false);
		setting.setAngleStepVisible(false);

		setting.setAlterLineTypePanelVisible(false);

		setting.setMountainButtonEnabled(false);
		setting.setValleyButtonEnabled(false);
		setting.setAuxButtonEnabled(false);
	}

}
