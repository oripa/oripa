package oripa.viewsetting.main;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class MainFrameSettingDB {

	private final PropertyChangeSupport support = new PropertyChangeSupport(this);

	private String hint;
	public static String HINT = "hint";

	private static MainFrameSettingDB instance = null;

	private MainFrameSettingDB() {
	}

	public static MainFrameSettingDB getInstance() {
		if (instance == null) {
			instance = new MainFrameSettingDB();
		}

		return instance;
	}

	public void addPropertyChangeListener(final PropertyChangeListener listener) {
		support.addPropertyChangeListener(listener);
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
