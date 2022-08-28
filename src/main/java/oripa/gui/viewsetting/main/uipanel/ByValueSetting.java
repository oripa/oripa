package oripa.gui.viewsetting.main.uipanel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import oripa.domain.paint.byvalue.ValueSetting;

public class ByValueSetting implements ValueSetting {

	private final PropertyChangeSupport support = new PropertyChangeSupport(this);

	private double length = 0;
	public static final String LENGTH = "length";

	private double angle = 0;
	public static final String ANGLE = "angle";

	public void addPropertyChangeListener(
			final String propertyName, final PropertyChangeListener listener) {
		support.addPropertyChangeListener(propertyName, listener);
	}

	@Override
	public double getLength() {
		return length;
	}

	@Override
	public void setLength(final double length) {
		var old = this.length;
		this.length = length;
		support.firePropertyChange(LENGTH, old, length);
	}

	@Override
	public double getAngle() {
		return angle;
	}

	@Override
	public void setAngle(final double angle) {
		var old = this.angle;
		this.angle = angle;
		support.firePropertyChange(ANGLE, old, angle);
	}
}
