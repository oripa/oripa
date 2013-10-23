package oripa.paint;

import oripa.paint.core.PaintContext;

public class PaintContextFactory {

	public PaintContextInterface createContext() {
		return PaintContext.getInstance();
	}
}
