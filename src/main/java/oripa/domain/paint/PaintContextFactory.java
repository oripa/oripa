package oripa.domain.paint;

import oripa.Config;
import oripa.domain.creasepattern.CreasePatternFactory;
import oripa.domain.paint.core.PaintContext;
import oripa.resource.Constants;

public class PaintContextFactory {

	public PaintContextInterface createContext() {
		CreasePatternFactory patternFactory = new CreasePatternFactory();

		PaintContextInterface context = new PaintContext();
		context.setCreasePattern(
				patternFactory.createCreasePattern(
						Constants.DEFAULT_PAPER_SIZE));

		context.setGridDivNum(Config.DEFAULT_GRID_DIV_NUM);
		return context;
	}
}
