package oripa.gui.viewsetting;

import java.beans.PropertyChangeListener;

public interface ViewScreenUpdater {

	public static final String REDRAW_REQUESTED = "redraw requested";

	public void updateScreen();

	public void addPropertyChangeListener(final String propertyName,
			final PropertyChangeListener listener);
}