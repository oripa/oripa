package oripa.viewsetting.main;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.paint.copypaste.SelectionOriginHolder;

public class MainScreenSetting {
	private static final Logger logger = LoggerFactory.getLogger(MainScreenSetting.class);

	private boolean gridVisible = true;
	public static final String GRID_VISIBLE = "grid visible";

	private boolean crossLineVisible = false;
	public static final String CROSS_LINE_VISIBLE = "cross line visible";

	private boolean zeroLineWidth = false;
	public static final String ZERO_LINE_WIDTH = "zero line width";

	private boolean vertexVisible = false;
	public static final String VERTEX_VISIBLE = "vertex visible";

	private boolean mvLineVisible = false;
	public static final String MV_LINE_VISIBLE = "mv line visible";

	private boolean auxLineVisible = false;
	public static final String AUX_LINE_VISIBLE = "aux line visible";

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

	public void setVertexVisible(final boolean vertexVisible) {
		var old = this.vertexVisible;
		this.vertexVisible = vertexVisible;
		logger.debug("firing change of vertexVisible: " + old + " -> " + vertexVisible);
		support.firePropertyChange(VERTEX_VISIBLE, old, vertexVisible);
	}

	public void setMVLineVisible(final boolean mvLineVisible) {
		var old = this.mvLineVisible;
		this.mvLineVisible = mvLineVisible;
		logger.debug("firing change of mvLineVisible: " + old + " -> " + mvLineVisible);
		support.firePropertyChange(MV_LINE_VISIBLE, old, mvLineVisible);
	}

	public void setAuxLineVisible(final boolean auxLineVisible) {
		var old = this.auxLineVisible;
		this.auxLineVisible = auxLineVisible;
		logger.debug("firing change of auxLineVisible: " + old + " -> " + auxLineVisible);
		support.firePropertyChange(AUX_LINE_VISIBLE, old, auxLineVisible);
	}

	public SelectionOriginHolder getSelectionOriginHolder() {
		return originHolder;
	}
}
