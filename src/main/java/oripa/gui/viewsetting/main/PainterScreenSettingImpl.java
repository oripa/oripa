package oripa.gui.viewsetting.main;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.gui.view.main.InitialVisibilities;
import oripa.gui.view.main.PainterScreenSetting;

public class PainterScreenSettingImpl implements PainterScreenSetting {
	private static final Logger logger = LoggerFactory.getLogger(PainterScreenSettingImpl.class);

	private boolean gridVisible = InitialVisibilities.GRID;
	private boolean crossLineVisible = InitialVisibilities.CROSS;
	private boolean zeroLineWidth = InitialVisibilities.ZERO_LINE_WIDTH;
	private boolean vertexVisible = InitialVisibilities.VERTEX;
	private boolean mvLineVisible = InitialVisibilities.MV;
	private boolean auxLineVisible = InitialVisibilities.AUX;
	private final PropertyChangeSupport support = new PropertyChangeSupport(this);

	@Override
	public void addPropertyChangeListener(final String propertyName,
			final PropertyChangeListener listener) {
		support.addPropertyChangeListener(propertyName, listener);
	}

	@Override
	public void setGridVisible(final boolean gridVisible) {
		var old = this.gridVisible;
		this.gridVisible = gridVisible;
		support.firePropertyChange(GRID_VISIBLE, old, gridVisible);
	}

	@Override
	public void setCrossLineVisible(final boolean visible) {
		var old = crossLineVisible;
		crossLineVisible = visible;
		support.firePropertyChange(CROSS_LINE_VISIBLE, old, visible);
	}

	@Override
	public void setZeroLineWidth(final boolean zeroLineWidth) {
		var old = this.zeroLineWidth;
		this.zeroLineWidth = zeroLineWidth;
		support.firePropertyChange(ZERO_LINE_WIDTH, old, zeroLineWidth);
	}

	@Override
	public void setVertexVisible(final boolean vertexVisible) {
		var old = this.vertexVisible;
		this.vertexVisible = vertexVisible;
		logger.debug("firing change of vertexVisible: " + old + " -> " + vertexVisible);
		support.firePropertyChange(VERTEX_VISIBLE, old, vertexVisible);
	}

	@Override
	public void setMVLineVisible(final boolean mvLineVisible) {
		var old = this.mvLineVisible;
		this.mvLineVisible = mvLineVisible;
		logger.debug("firing change of mvLineVisible: " + old + " -> " + mvLineVisible);
		support.firePropertyChange(MV_LINE_VISIBLE, old, mvLineVisible);
	}

	@Override
	public void setAuxLineVisible(final boolean auxLineVisible) {
		var old = this.auxLineVisible;
		this.auxLineVisible = auxLineVisible;
		logger.debug("firing change of auxLineVisible: " + old + " -> " + auxLineVisible);
		support.firePropertyChange(AUX_LINE_VISIBLE, old, auxLineVisible);
	}
}
