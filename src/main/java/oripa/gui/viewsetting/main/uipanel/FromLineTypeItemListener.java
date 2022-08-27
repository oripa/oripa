package oripa.gui.viewsetting.main.uipanel;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class FromLineTypeItemListener implements ItemListener {
	private final UIPanelSetting setting;

	/**
	 * listen for changes in the fromLineType drop down
	 */
	public FromLineTypeItemListener(final UIPanelSetting uiPanelSetting) {
		setting = uiPanelSetting;
	}

	@Override
	public void itemStateChanged(final ItemEvent e) {

		if (e.getStateChange() == ItemEvent.SELECTED) {
			setting.setTypeFrom((String) e.getItem());
		}
	}
}
