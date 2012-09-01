package oripa.viewsetting.uipanel;

import oripa.viewsetting.ChangeViewSetting;


public class OnNormalCommandButtonSelected implements ChangeViewSetting {

	@Override
	public void changeViewSetting() {
		UIPanelSettingDB setting = UIPanelSettingDB.getInstance();
				
		setting.setValuePanelVisible(false);
		
		setting.setAlterLineTypePanelVisible(false);

		setting.setMountainButtonEnabled(true);
		setting.setValleyButtonEnabled(true);
		setting.setAuxButtonEnabled(true);

		setting.notifyObservers();

		
		
	}

}
