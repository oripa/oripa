package oripa.controller.paint;

import oripa.controller.paint.core.PaintContext;

public class PaintContextFactory {

	public PaintContextInterface createContext() {
		return PaintContext.getInstance();
	}
}
