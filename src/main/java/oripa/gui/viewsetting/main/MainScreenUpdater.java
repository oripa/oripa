package oripa.gui.viewsetting.main;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import oripa.gui.viewsetting.ViewScreenUpdater;

public class MainScreenUpdater implements ViewScreenUpdater {

	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	public MainScreenUpdater() {
	}

	@Override
	public void addPropertyChangeListener(final String propertyName,
			final PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.viewsetting.main.ViewScreenUpdater#updateScreen()
	 */
	@Override
	public void updateScreen() {
		propertyChangeSupport.firePropertyChange(REDRAW_REQUESTED, null, null);
	}
}
