package oripa.geom;

import java.util.Collection;

import javax.vecmath.Vector2d;

import oripa.value.OriLine;

/**
 * A rectangle domain fitting to given lines.
 *
 * Position coordinate is the same as screen. (top is smaller)
 */
public class RectangleDomain {
	private double left, right, top, bottom;

	/**
	 * construct this instance fit to given lines
	 *
	 * @param target
	 */
	public RectangleDomain(final Collection<OriLine> target) {

		initialize();

		for (OriLine line : target) {
			enlarge(line.p0);
			enlarge(line.p1);
		}

	}

	/**
	 * Hide from others since this is meaningless.
	 */
	@SuppressWarnings("unused")
	private RectangleDomain() {
	}

	private void initialize() {
		left = Double.POSITIVE_INFINITY;
		right = Double.NEGATIVE_INFINITY;
		top = Double.POSITIVE_INFINITY;
		bottom = Double.NEGATIVE_INFINITY;

	}

	/**
	 * Enlarge this domain as including given point.
	 *
	 * @param v
	 */
	private void enlarge(final Vector2d v) {
		left = Math.min(left, v.x);
		right = Math.max(right, v.x);
		top = Math.min(top, v.y);
		bottom = Math.max(bottom, v.y);

	}

	/**
	 * @return left
	 */
	public double getLeft() {
		return left;
	}

	/**
	 * @return right
	 */
	public double getRight() {
		return right;
	}

	/**
	 * @return top
	 */
	public double getTop() {
		return top;
	}

	/**
	 * @return bottom
	 */
	public double getBottom() {
		return bottom;
	}

	public double getWidth() {
		return computeGap(left, right);
	}

	public double getHeight() {
		return computeGap(top, bottom);
	}

	public double maxWidthHeight() {
		return Math.max(getWidth(), getHeight());
	}

	public double getCenterX() {
		return (left + right) / 2;
	}

	public double getCenterY() {
		return (top + bottom) / 2;
	}

	private double computeGap(final double a, final double b) {
		return Math.max(a, b) - Math.min(a, b);
	}
}