package oripa.fold;

import javax.vecmath.Vector2d;

public class BoundBox {
	private Vector2d leftAndTop;
	private Vector2d rightAndBottom;

	public BoundBox(Vector2d lt, Vector2d rb) {
		leftAndTop     = lt;
		rightAndBottom = rb;
	}
	
	public Vector2d getRightAndBottom() {
		return rightAndBottom;
	}
	public void setRightAndBottom(Vector2d rightAndBottom) {
		this.rightAndBottom = rightAndBottom;
	}
	public Vector2d getLeftAndTop() {
		return leftAndTop;
	}
	public void setLeftAndTop(Vector2d leftAndTop) {
		this.leftAndTop = leftAndTop;
	}


	
	
}
