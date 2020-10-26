package oripa.domain.fold;

import javax.vecmath.Vector2d;

public class BoundBox {
	private Vector2d leftAndTop;
	private Vector2d rightAndBottom;

	public BoundBox(final Vector2d lt, final Vector2d rb) {
		leftAndTop = lt;
		rightAndBottom = rb;
	}

	public Vector2d getRightAndBottom() {
		return rightAndBottom;
	}

	public void setRightAndBottom(final Vector2d rightAndBottom) {
		this.rightAndBottom = rightAndBottom;
	}

	public Vector2d getLeftAndTop() {
		return leftAndTop;
	}

	public void setLeftAndTop(final Vector2d leftAndTop) {
		this.leftAndTop = leftAndTop;
	}

	public double getWidth() {
		return rightAndBottom.x - leftAndTop.x;
	}

	public double getHeight() {
		return rightAndBottom.y - leftAndTop.y;
	}

	public double getCenterX() {
		return (leftAndTop.x + rightAndBottom.x) / 2;
	}

	public double getCenterY() {
		return (leftAndTop.y + rightAndBottom.y) / 2;
	}
}
