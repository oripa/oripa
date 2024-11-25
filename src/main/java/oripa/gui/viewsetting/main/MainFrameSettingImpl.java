package oripa.gui.viewsetting.main;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import jakarta.inject.Singleton;
import oripa.gui.view.main.MainFrameSetting;

@Singleton
public class MainFrameSettingImpl implements MainFrameSetting {

	private final PropertyChangeSupport support = new PropertyChangeSupport(this);

	private String hint;

	@Override
	public void addPropertyChangeListener(final String propertyName,
			final PropertyChangeListener listener) {
		support.addPropertyChangeListener(propertyName, listener);
	}

	@Override
	public void setHint(final String hint) {
		var old = this.hint;
		this.hint = hint;
		support.firePropertyChange(HINT, old, hint);
	}
}
