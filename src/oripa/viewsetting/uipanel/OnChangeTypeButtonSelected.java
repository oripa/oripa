package oripa.viewsetting.uipanel;

import oripa.viewsetting.ChangeViewSetting;

public class OnChangeTypeButtonSelected implements ChangeViewSetting{
	@Override
	public void changeViewSetting() {
		UIPanelSettingDB setting = UIPanelSettingDB.getInstance();
		
		setting.setValuePanelVisible(false);
		
		setting.setAlterLineTypePanelVisible(true);

		setting.setMountainButtonEnabled(false);
		setting.setValleyButtonEnabled(false);
		setting.setAuxButtonEnabled(false);

		setting.notifyObservers();

	}

}
