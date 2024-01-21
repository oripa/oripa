package oripa.domain.paint.copypaste;

import oripa.domain.paint.PaintContext;
import oripa.vecmath.Vector2d;

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
				origin = context.getLine(0).getP0();
			}
		}
	}

	@Override
	public Vector2d getOrigin(final PaintContext context) {
		resetOrigin(context);

		return origin;
	}

}
