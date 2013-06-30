package oripa.viewsetting.model;

import oripa.viewsetting.ViewSettingDataBase;

public class ModelFrameSettingDB extends ViewSettingDataBase{

	private boolean frameVisible;
	
	
	
	
	private static ModelFrameSettingDB instance = null;

	private ModelFrameSettingDB(){}
	
	public static ModelFrameSettingDB getInstance(){
		if(instance == null){
			instance = new ModelFrameSettingDB();
		}
		
		return instance;
	}
	
	




	public boolean isFrameVisible() {
		return frameVisible;
	}

	public void setFrameVisible(boolean frameVisible) {
		this.frameVisible = frameVisible;
		this.setChanged();
	}

	@Override
	public String getName() {
		return this.getClass().getName();
	}
}
