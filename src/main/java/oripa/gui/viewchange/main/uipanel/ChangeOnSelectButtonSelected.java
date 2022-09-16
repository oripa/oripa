package oripa.gui.viewchange.main.uipanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.gui.view.main.UIPanelSetting;
import oripa.gui.viewchange.ChangeViewSetting;

public class ChangeOnSelectButtonSelected implements ChangeViewSetting {
	private static final Logger logger = LoggerFactory.getLogger(ChangeOnSelectButtonSelected.class);

	private final UIPanelSetting setting;

	/**
	 * UIPanel Settings for select lines mode
	 */
	public ChangeOnSelectButtonSelected(final UIPanelSetting uiPanelSetting) {
		setting = uiPanelSetting;
	}

	@Override
	public void changeViewSetting() {
		logger.debug("change UI to selection mode");

		setting.selectSelectMode();

		setting.setByValuePanelVisible(false);
		setting.setAngleStepPanelVisible(false);

		setting.setLineSelectionPanelVisible(true);
		setting.setLineInputPanelVisible(false);

		setting.setAlterLineTypePanelVisible(false);
	}

}
