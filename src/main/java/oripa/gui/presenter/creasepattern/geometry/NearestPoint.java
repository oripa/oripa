package oripa.gui.presenter.creasepattern.geometry;

import oripa.vecmath.Vector2d;

public class NearestPoint {
	public Vector2d point = new Vector2d(0, 0);
	public double distance = Double.MAX_VALUE;

	/**
	 * distance is set to maximum. point is not null but dummy.
	 */
	public NearestPoint() {
	}

	public NearestPoint(final NearestPoint p) {
		if (p != null) {
			point = p.point;
			distance = p.distance;
		}
	}
}
