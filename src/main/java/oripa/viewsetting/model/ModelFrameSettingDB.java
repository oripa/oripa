package oripa.viewsetting.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ModelFrameSettingDB {

	private final PropertyChangeSupport support = new PropertyChangeSupport(this);

	private boolean frameVisible;
	public static final String FRAME_VISIBLE = "frame visible";

	private static ModelFrameSettingDB instance = null;

	private ModelFrameSettingDB() {
	}

	public static ModelFrameSettingDB getInstance() {
		if (instance == null) {
			instance = new ModelFrameSettingDB();
		}

		return instance;
	}

	public void addPropertyChangeListener(final String propertyName,
			final PropertyChangeListener listener) {
		support.addPropertyChangeListener(propertyName, listener);
	}

	public boolean isFrameVisible() {
		return frameVisible;
	}

	public void setFrameVisible(final boolean frameVisible) {
		var old = this.frameVisible;
		this.frameVisible = frameVisible;
		support.firePropertyChange(FRAME_VISIBLE, old, frameVisible);
	}
}
