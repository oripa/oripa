package oripa.gui.viewchange.main.uipanel;

import oripa.gui.view.main.UIPanelSetting;
import oripa.gui.viewchange.ChangeViewSetting;

public class ChangeOnAlterTypeButtonSelected implements ChangeViewSetting {
	private final UIPanelSetting setting;

	/**
	 * UIPanel settings for Alter Line Type Tool
	 */
	public ChangeOnAlterTypeButtonSelected(final UIPanelSetting uiPanelSetting) {
		this.setting = uiPanelSetting;
	}

	@Override
	public void changeViewSetting() {

		setting.setByValuePanelVisible(false);

		setting.setLineSelectionPanelVisible(false);
		setting.setLineInputPanelVisible(false);

		setting.setAngleStepPanelVisible(false);

		setting.setAlterLineTypePanelVisible(true);
	}

}
