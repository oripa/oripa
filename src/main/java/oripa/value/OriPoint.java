package oripa.value;

import javax.vecmath.Vector2d;

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

	public OriPoint() {
		super();
	}

	public OriPoint(final Vector2d p) {
		super(p);
	}

	public OriPoint(final double x, final double y) {
		super(x, y);
	}

	@Override
	public int compareTo(final Vector2d o) {
		if (this.x == o.x) {
			return (int) Math.signum(this.y - o.y);
		}
		return (int) Math.signum(this.x - o.x);
	}
}
