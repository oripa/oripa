package oripa.gui.viewsetting.main.uipanel;

import oripa.gui.viewsetting.ChangeViewSetting;

public class ChangeOnAngleSnapButtonSelected implements ChangeViewSetting {
	private final UIPanelSetting setting;

	/**
	 * change UIPanel view settings for Angle Snap Line Input Tool
	 */
	public ChangeOnAngleSnapButtonSelected(final UIPanelSetting uiPanelSetting) {
		setting = uiPanelSetting;
	}

	@Override
	public void changeViewSetting() {
		setting.selectInputMode();

		setting.setByValuePanelVisible(false);

		setting.setLineSelectionPanelVisible(false);
		setting.setLineInputPanelVisible(true);

		setting.setAngleStepPanelVisible(true);

		setting.setAlterLineTypePanelVisible(false);
	}

}
