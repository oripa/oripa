package oripa.domain.paint.outline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.vecmath.Vector2d;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.cptool.OverlappingLineExtractor;
import oripa.domain.cptool.Painter;
import oripa.domain.creasepattern.CreasePatternInterface;
import oripa.domain.paint.util.PairLoop;
import oripa.value.OriLine;

public class CloseTempOutline {
	private static final Logger logger = LoggerFactory.getLogger(CloseTempOutline.class);

	private final IsOnTempOutlineLoop isOnTempOutlineLoop;
	private final IsOutsideOfTempOutlineLoop isOutsideOfTempOutlineLoop;
	private final Painter painter;
	private final OverlappingLineExtractor overlappingExtractor;

	/**
	 * Constructor
	 */
	public CloseTempOutline(final IsOnTempOutlineLoop isOnTempOutlineLoop,
			final IsOutsideOfTempOutlineLoop isOutsideOfTempOutlineLoop, final Painter painter,
			final OverlappingLineExtractor overlappingExtractor) {
		this.isOnTempOutlineLoop = isOnTempOutlineLoop;
		this.isOutsideOfTempOutlineLoop = isOutsideOfTempOutlineLoop;
		this.painter = painter;
		this.overlappingExtractor = overlappingExtractor;
	}

	public void execute(final Collection<Vector2d> outlineVertices) {
		var creasePattern = painter.getCreasePattern();

		// Delete the current outline
		List<OriLine> outlines = creasePattern.stream()
				.filter(line -> line.isBoundary()).collect(Collectors.toList());
		creasePattern.removeAll(outlines);

		addNewOutline(creasePattern, outlineVertices);

		removeLinesOutsideOfOutline(creasePattern, outlineVertices);
	}

	private void addNewOutline(final CreasePatternInterface creasePattern,
			final Collection<Vector2d> outlineVertices) {
		var overlappings = new ArrayList<OriLine>();
		var lines = new ArrayList<OriLine>();

		PairLoop.iterateAll(outlineVertices, (element, nextElement) -> {
			var line = new OriLine(element, nextElement, OriLine.Type.CUT);
			lines.add(line);
			overlappings.addAll(overlappingExtractor.extract(creasePattern, line));

			return true;
		});

		painter.removeLines(overlappings);
		painter.addLines(lines);
	}

	private void removeLinesOutsideOfOutline(final CreasePatternInterface creasePattern,
			final Collection<Vector2d> outlineVertices) {
		var toBeRemoved = new ArrayList<OriLine>();

		for (OriLine line : creasePattern) {
			if (line.isBoundary()) {
				continue;
			}
			double eps = creasePattern.getPaperSize() * 0.001;
			var onPoint0 = isOnTempOutlineLoop.execute(outlineVertices, line.p0, eps);
			var onPoint1 = isOnTempOutlineLoop.execute(outlineVertices, line.p1, eps);

			logger.debug("line = " + line);
			logger.debug("onPoint0 = " + onPoint0);
			logger.debug("onPoint1 = " + onPoint1);
			// meaningless?
//			if (onPoint0 != null && onPoint0 == onPoint1) {
//				toBeRemoved.add(line);
//				logger.debug("line is removed: it's on contour.");
//			}

			var isOutsideP0 = isOutsideOfTempOutlineLoop.execute(outlineVertices, line.p0);
			var isOutsideP1 = isOutsideOfTempOutlineLoop.execute(outlineVertices, line.p1);

			logger.debug(String.join(",", outlineVertices.stream()
					.map(v -> v.toString()).collect(Collectors.toList())));
			logger.debug("isOutsideP0 = " + isOutsideP0);
			logger.debug("isOutsideP1 = " + isOutsideP1);

			if ((!onPoint0 && isOutsideP0) || (!onPoint1 && isOutsideP1)) {
				toBeRemoved.add(line);
				logger.debug("line is removed: it's outside of contour.");
			}
		}

		painter.removeLines(toBeRemoved);
	}
}
