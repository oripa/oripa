package oripa.domain.paint;

import oripa.domain.creasepattern.CreasePatternFactory;
import oripa.resource.Constants;

public class PaintContextFactory {

	public PaintContextInterface createContext() {
		CreasePatternFactory patternFactory = new CreasePatternFactory();

		PaintContextInterface context = new PaintContext();
		context.setCreasePattern(
				patternFactory.createCreasePattern(
						Constants.DEFAULT_PAPER_SIZE));

		context.setGridDivNum(Constants.DEFAULT_GRID_DIV_NUM);
		return context;
	}
}
