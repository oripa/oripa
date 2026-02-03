package oripa.value;

import oripa.vecmath.Vector2d;

/**
 * Comparable vertex
 *
 * @author Koji
 *
 */
public class OriPoint extends Vector2d implements Comparable<Vector2d> {

    /**
     *
     */
    private static final long serialVersionUID = -6753157628675186598L;

    public OriPoint(final Vector2d p) {
        super(p.getX(), p.getY());
    }

    public OriPoint(final double x, final double y) {
        super(x, y);
    }

    @Override
    public int compareTo(final Vector2d o) {
        var x = getX();
        var y = getY();

        var ox = o.getX();
        var oy = o.getY();

        var comp = Double.compare(x, ox);

        if (comp == 0) {
            comp = Double.compare(y, oy);
        }

        return comp;
    }
}
