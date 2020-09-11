package oripa.viewsetting.main;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class MainFrameSetting {

	private final PropertyChangeSupport support = new PropertyChangeSupport(this);

	private String hint;
	public static String HINT = "hint";

	public void addPropertyChangeListener(final String propertyName,
			final PropertyChangeListener listener) {
		support.addPropertyChangeListener(propertyName, listener);
	}

	public String getHint() {
		return hint;
	}

	public void setHint(final String hint) {
		var old = this.hint;
		this.hint = hint;
		support.firePropertyChange(HINT, old, hint);
	}
}
