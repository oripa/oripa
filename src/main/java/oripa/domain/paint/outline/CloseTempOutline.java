package oripa.domain.paint.outline;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.cptool.Painter;
import oripa.value.OriLine;
import oripa.vecmath.Vector2d;

public class CloseTempOutline {
	private static final Logger logger = LoggerFactory.getLogger(CloseTempOutline.class);

	private final OutsideLineRemover remover;
	private final OutlineAdder adder;

	/**
	 * Constructor
	 */
	public CloseTempOutline(final OutsideLineRemover remover, final OutlineAdder adder) {
		this.remover = remover;
		this.adder = adder;
	}

	public void execute(final Collection<Vector2d> outlineVertices, final Painter painter) {
		var creasePattern = painter.getCreasePattern();

		// Delete the current outline
		List<OriLine> outlines = creasePattern.stream()
				.filter(OriLine::isBoundary).toList();
		creasePattern.removeAll(outlines);

		adder.addOutlines(painter, outlineVertices);

		remover.removeLinesOutsideOfOutlines(painter, outlineVertices);
	}
}
