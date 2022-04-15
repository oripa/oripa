package oripa.gui.viewsetting.main.uipanel;

import oripa.gui.viewsetting.ChangeViewSetting;

public class ChangeOnOtherCommandButtonSelected implements ChangeViewSetting {
	private final UIPanelSetting setting;

	/**
	 * UIPanel settings for buttons without any tool settings
	 */
	public ChangeOnOtherCommandButtonSelected(final UIPanelSetting uiPanelSetting) {
		setting = uiPanelSetting;
	}

	@Override
	public void changeViewSetting() {
		setting.setByValuePanelVisible(false);

		setting.setLineSelectionPanelVisible(false);
		setting.setLineInputPanelVisible(false);

		setting.setAngleStepPanelVisible(false);

		setting.setAlterLineTypePanelVisible(false);
	}

}
