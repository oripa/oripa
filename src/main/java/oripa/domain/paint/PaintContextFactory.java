package oripa.domain.paint;

import oripa.domain.creasepattern.CreasePatternFactory;
import oripa.domain.paint.core.PaintContext;
import oripa.resource.Constants;

public class PaintContextFactory {

	public PaintContextInterface createContext() {
		CreasePatternFactory patternFactory = new CreasePatternFactory();

		PaintContextInterface context = PaintContext.getInstance();
		context.setCreasePattern(
				patternFactory.createCreasePattern(
						Constants.DEFAULT_PAPER_SIZE));

		return context;
	}
}
