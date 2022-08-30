package oripa.gui.viewsetting.main;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import oripa.gui.viewsetting.ViewScreenUpdater;

public class MainScreenUpdater implements ViewScreenUpdater {

	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	public MainScreenUpdater() {
	}

	private void addPropertyChangeListener(final String propertyName,
			final PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	@Override
	public void addListener(final Runnable listener) {
		addPropertyChangeListener(REDRAW_REQUESTED, e -> listener.run());
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
