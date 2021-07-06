package oripa.viewsetting.main.uipanel;

import oripa.viewsetting.ChangeViewSetting;

public class ChangeOnSelectButtonSelected implements ChangeViewSetting {
	private final UIPanelSetting setting;

	/**
	 * UIPanel Settings for select lines mode
	 */
	public ChangeOnSelectButtonSelected(final UIPanelSetting uiPanelSetting) {
		setting = uiPanelSetting;
	}

	@Override
	public void changeViewSetting() {
		setting.selectSelectMode();

		setting.setByValuePanelVisible(false);
		setting.setAngleStepPanelVisible(false);
		setting.setLineInputPanelVisible(false);

		setting.setAlterLineTypePanelVisible(false);
	}

}
