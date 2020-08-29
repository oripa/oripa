package oripa.viewsetting.main.uipanel;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import oripa.domain.cptool.TypeForChange;

public class FromLineTypeItemListener implements ItemListener {
	private final UIPanelSetting setting;

	/**
	 * Constructor
	 */
	public FromLineTypeItemListener(final UIPanelSetting uiPanelSetting) {
		setting = uiPanelSetting;
	}

	@Override
	public void itemStateChanged(final ItemEvent e) {

		if (e.getStateChange() == ItemEvent.SELECTED) {
			setting.setTypeFrom(
					(TypeForChange) e.getItem());
		}
	}
}
