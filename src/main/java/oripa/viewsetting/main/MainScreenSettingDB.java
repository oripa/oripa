package oripa.viewsetting.main;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class MainScreenSettingDB {

	private boolean gridVisible = true;
	public static final String GRID_VISIBLE = "grid visible";

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

	public void addPropertyChangeListener(final String propertyName,
			final PropertyChangeListener listener) {
		support.addPropertyChangeListener(propertyName, listener);
	}

	public void setGridVisible(final boolean gridVisible) {
		var old = this.gridVisible;
		this.gridVisible = gridVisible;
		support.firePropertyChange(GRID_VISIBLE, old, gridVisible);
	}

//	@Override
//	public String getName() {
//		return this.getClass().getName();
//	}
}
