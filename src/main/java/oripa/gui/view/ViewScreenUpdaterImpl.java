package oripa.gui.view;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

class ViewScreenUpdaterImpl implements ViewScreenUpdater {

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public static final String REDRAW_REQUESTED = "redraw requested";

    public ViewScreenUpdaterImpl() {
    }

    private void addPropertyChangeListener(final String propertyName,
            final PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    @Override
    public void addListener(final Runnable listener) {
        addPropertyChangeListener(REDRAW_REQUESTED, e -> listener.run());
    }

    @Override
    public void updateScreen() {
        propertyChangeSupport.firePropertyChange(REDRAW_REQUESTED, null, null);
    }
}
