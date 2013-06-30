package oripa.viewsetting.uipanel;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import oripa.doc.TypeForChange;

public class FromLineTypeItemListener implements ItemListener {

	@Override
	public void itemStateChanged(ItemEvent e) {
		
		UIPanelSettingDB settingDB = UIPanelSettingDB.getInstance();
		
		if(e.getStateChange() == ItemEvent.SELECTED){
			settingDB.setTypeFrom(
					(TypeForChange)e.getItem());	

		}
	}
}
