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

	private final Painter painter;
	private final OverlappingLineExtractor overlappingExtractor;

	/**
	 * Constructor
	 */
	public CloseTempOutline(final Painter painter, final OverlappingLineExtractor overlappingExtractor) {
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
			Vector2d onPoint0 = isOnTmpOutlineLoop(outlineVertices, line.p0, eps);
			Vector2d onPoint1 = isOnTmpOutlineLoop(outlineVertices, line.p1, eps);

			logger.debug("line = " + line);
			logger.debug("onPoint0 = " + onPoint0);
			logger.debug("onPoint1 = " + onPoint1);
			// meaningless?
//			if (onPoint0 != null && onPoint0 == onPoint1) {
//				toBeRemoved.add(line);
//				logger.debug("line is removed: it's on contour.");
//			}

			var isOutsideP0 = isOutsideOfTmpOutlineLoop(outlineVertices, line.p0);
			var isOutsideP1 = isOutsideOfTmpOutlineLoop(outlineVertices, line.p1);

			logger.debug(String.join(",", outlineVertices.stream()
					.map(v -> v.toString()).collect(Collectors.toList())));
			logger.debug("isOutsideP0 = " + isOutsideP0);
			logger.debug("isOutsideP1 = " + isOutsideP1);

			if ((onPoint0 == null && isOutsideP0) || (onPoint1 == null && isOutsideP1)) {
				toBeRemoved.add(line);
				logger.debug("line is removed: it's outside of contour.");
			}
		}

		painter.removeLines(toBeRemoved);
	}

	private Vector2d isOnTmpOutlineLoop(
			final Collection<Vector2d> outlinevertices, final Vector2d v, final double eps) {

		return (new IsOnTempOutlineLoop()).execute(outlinevertices, v, eps);
	}

	private boolean isOutsideOfTmpOutlineLoop(
			final Collection<Vector2d> outlinevertices, final Vector2d v) {

		return (new IsOutsideOfTempOutlineLoop()).execute(outlinevertices, v);
	}

}
