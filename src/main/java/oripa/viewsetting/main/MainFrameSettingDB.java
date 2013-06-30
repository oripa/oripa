package oripa.viewsetting.main;

import oripa.viewsetting.ViewSettingDataBase;

public class MainFrameSettingDB extends ViewSettingDataBase{

	private String hint;
	
	
	
	
	private static MainFrameSettingDB instance = null;

	private MainFrameSettingDB(){}
	
	public static MainFrameSettingDB getInstance(){
		if(instance == null){
			instance = new MainFrameSettingDB();
		}
		
		return instance;
	}
	
	
	public String getHint() {
		return hint;
	}




	public void setHint(String hint) {
		this.hint = hint;
		this.setChanged();
	}




	@Override
	public String getName() {
		return this.getClass().getName();
	}
}
