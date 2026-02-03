package oripa.domain.paint.byvalue;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import jakarta.inject.Singleton;

@Singleton
public class ByValueContextImpl implements ByValueContext {

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    private double length = 0;

    private double angle = 0;

    @Override
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
