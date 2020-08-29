package oripa.viewsetting.main.uipanel;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import oripa.domain.cptool.TypeForChange;

public class ToLineTypeItemListener implements ItemListener {
	private final UIPanelSettingDB setting;

	/**
	 * Constructor
	 */
	public ToLineTypeItemListener(final UIPanelSettingDB uiPanelSetting) {
		setting = uiPanelSetting;
	}

	@Override
	public void itemStateChanged(final ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			setting.setTypeTo(
					(TypeForChange) e.getItem());
		}
	}
}
