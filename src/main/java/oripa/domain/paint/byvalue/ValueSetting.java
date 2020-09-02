package oripa.domain.paint.byvalue;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ValueSetting {

	private final PropertyChangeSupport support = new PropertyChangeSupport(this);

	private double length = 0;
	public static final String LENGTH = "length";

	private double angle = 0;
	public static final String ANGLE = "angle";

	public void addPropertyChangeListener(
			final String propertyName, final PropertyChangeListener listener) {
		support.addPropertyChangeListener(propertyName, listener);
	}

	public void set(final double length, final double angle) {
		setLength(length);
		setAngle(angle);
	}

	public double getLength() {
		return length;
	}

	public void setLength(final double length) {
		var old = this.length;
		this.length = length;
		support.firePropertyChange(LENGTH, old, length);
	}

	/**
	 *
	 * @return angle [degree]
	 */
	public double getAngle() {
		return angle;
	}

	public void setAngle(final double angle) {
		var old = this.angle;
		this.angle = angle;
		support.firePropertyChange(ANGLE, old, angle);
	}
//
//	/**
//	 * @return full-path class name
//	 */
//	@Override
//	public String toString() {
//		return this.getClass().getName();
//	}

}
