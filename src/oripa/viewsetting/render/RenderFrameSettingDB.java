package oripa.viewsetting.render;

import oripa.viewsetting.ViewSettingDataBase;

public class RenderFrameSettingDB extends ViewSettingDataBase{

	private boolean frameVisible;
	
	
	
	
	private static RenderFrameSettingDB instance = null;

	private RenderFrameSettingDB(){}
	
	public static RenderFrameSettingDB getInstance(){
		if(instance == null){
			instance = new RenderFrameSettingDB();
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
