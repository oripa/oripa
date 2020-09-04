package oripa.viewsetting.main;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import oripa.domain.paint.copypaste.OriginHolder;

public class MainScreenSetting {

	private boolean gridVisible = true;
	public static final String GRID_VISIBLE = "grid visible";

	private boolean crossLineVisible = false;
	public static final String CROSS_LINE_VISIBLE = "cross line visible";

	private final OriginHolder originHolder = new OriginHolder();

	private final PropertyChangeSupport support = new PropertyChangeSupport(this);

	public void addPropertyChangeListener(final String propertyName,
			final PropertyChangeListener listener) {
		support.addPropertyChangeListener(propertyName, listener);
	}

	public void setGridVisible(final boolean gridVisible) {
		var old = this.gridVisible;
		this.gridVisible = gridVisible;
		support.firePropertyChange(GRID_VISIBLE, old, gridVisible);
	}

	public void setCrossLineVisible(final boolean visible) {
		var old = crossLineVisible;
		crossLineVisible = visible;
		support.firePropertyChange(CROSS_LINE_VISIBLE, old, visible);
	}

	public OriginHolder getOriginHolder() {
		return originHolder;
	}
}
