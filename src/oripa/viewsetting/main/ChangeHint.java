package oripa.viewsetting.main;

import oripa.ORIPA;
import oripa.viewsetting.ChangeViewSetting;

public class ChangeHint implements ChangeViewSetting {

	private MainFrameSettingDB frameSetting = MainFrameSettingDB.getInstance();

	private String id;
	
	public ChangeHint(String resourceID){
		this.id = resourceID;
	}
	
	@Override
	public void changeViewSetting() {
		frameSetting.setHint(ORIPA.res.getString(id));
		
		frameSetting.notifyObservers();
	}

}
