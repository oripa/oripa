package oripa.viewsetting.main;

import oripa.viewsetting.ViewSettingDataBase;

public class MainScreenSettingDB extends ViewSettingDataBase{

	
	private boolean gridVisible = true;


	// ---------
	private static MainScreenSettingDB instance = null;

	private MainScreenSettingDB(){}
	
	public static MainScreenSettingDB getInstance(){
		if(instance == null){
			instance = new MainScreenSettingDB();
		}
		
		return instance;
	}

//	public static final String REDRAW_REQUESTED = "redraw requested";
//	public void requestRedraw(){
//		setChanged();
//		notifyObservers(REDRAW_REQUESTED);
//	}
	
	
	public boolean isGridVisible() {
		return gridVisible;
	}



	public void setGridVisible(boolean gridVisible) {
		this.gridVisible = gridVisible;
		this.setChanged();
	}


//	@Override
//	public String getName() {
//		return this.getClass().getName();
//	}
}
