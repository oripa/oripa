package oripa.domain.paint.copypaste;

import javax.vecmath.Vector2d;

import oripa.domain.paint.PaintContext;

public class SelectionOriginHolderImpl implements SelectionOriginHolder {

	public SelectionOriginHolderImpl() {
	}

	private Vector2d origin = null;

	@Override
	public void setOrigin(final Vector2d p) {
		origin = p;
	}

	@Override
	public void resetOrigin(final PaintContext context) {
		if (origin == null) {
			if (context.getLineCount() > 0) {
				origin = context.getLine(0).p0;
			}
		}
	}

	@Override
	public Vector2d getOrigin(final PaintContext context) {
		resetOrigin(context);

		return origin;
	}

}
