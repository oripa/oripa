package oripa.viewsetting.main;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class MainScreenSettingDB {

	private boolean gridVisible = true;

	private final PropertyChangeSupport support = new PropertyChangeSupport(this);

	// ---------
	private static MainScreenSettingDB instance = null;

	private MainScreenSettingDB() {
	}

	public static MainScreenSettingDB getInstance() {
		if (instance == null) {
			instance = new MainScreenSettingDB();
		}

		return instance;
	}

//	public static final String REDRAW_REQUESTED = "redraw requested";
//	public void requestRedraw(){
//		setChanged();
//		notifyObservers(REDRAW_REQUESTED);
//	}

	public void addPropertyChangeListener(final PropertyChangeListener listener) {
		support.addPropertyChangeListener(listener);
	}

	public void setGridVisible(final boolean gridVisible) {
		support.firePropertyChange("gridVisible", this.gridVisible, gridVisible);
		this.gridVisible = gridVisible;
	}

//	@Override
//	public String getName() {
//		return this.getClass().getName();
//	}
}
