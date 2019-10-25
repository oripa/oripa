package oripa.viewsetting.estimation;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class RenderFrameSettingDB {

	private final PropertyChangeSupport support = new PropertyChangeSupport(this);

	private boolean frameVisible;
	public static final String FRAME_VISIBLE = "frame visible";

	private static RenderFrameSettingDB instance = null;

	private RenderFrameSettingDB() {
	}

	public static RenderFrameSettingDB getInstance() {
		if (instance == null) {
			instance = new RenderFrameSettingDB();
		}

		return instance;
	}

	public void addPropertyChangeListener(
			final String propertyName, final PropertyChangeListener listener) {
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
