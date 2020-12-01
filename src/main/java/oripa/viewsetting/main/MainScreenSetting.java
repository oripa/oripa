package oripa.viewsetting.main;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import oripa.domain.paint.copypaste.SelectionOriginHolder;

public class MainScreenSetting {

	private boolean gridVisible = true;
	public static final String GRID_VISIBLE = "grid visible";

	private boolean crossLineVisible = false;
	public static final String CROSS_LINE_VISIBLE = "cross line visible";

	private boolean zeroLineWidth = false;
	public static final String ZERO_LINE_WIDTH = "zero line width";

	private final SelectionOriginHolder originHolder = new SelectionOriginHolder();

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

	public void setZeroLineWidth(final boolean zeroLineWidth) {
		var old = this.zeroLineWidth;
		this.zeroLineWidth = zeroLineWidth;
		support.firePropertyChange(ZERO_LINE_WIDTH, old, zeroLineWidth);
	}

	public SelectionOriginHolder getSelectionOriginHolder() {
		return originHolder;
	}
}
