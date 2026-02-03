package oripa.gui.viewchange.main.uipanel;

import oripa.gui.view.main.UIPanelSetting;
import oripa.gui.viewchange.ChangeViewSetting;

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
