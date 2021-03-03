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

	private class ContourLineAdder implements PairLoop.Block<Vector2d> {

		private final CreasePatternInterface creasePattern;

		public ContourLineAdder(final CreasePatternInterface creasePattern) {
			this.creasePattern = creasePattern;
		}

		@Override
		public boolean yield(final Vector2d element, final Vector2d nextElement) {
			var painter = new Painter(creasePattern);

			var overlappingExtractor = new OverlappingLineExtractor();
			var line = new OriLine(element, nextElement, OriLine.Type.CUT);
			painter.removeLines(overlappingExtractor.extract(creasePattern, line));
			painter.addLine(line);

			return true;
		}
	}

	public void execute(final CreasePatternInterface creasePattern,
			final Collection<Vector2d> outlineVertices) {

		// Delete the current outline
		List<OriLine> outlines = creasePattern.stream()
				.filter(line -> line.isBoundary()).collect(Collectors.toList());
		creasePattern.removeAll(outlines);

		// Update the contour line
		PairLoop.iterateAll(
				outlineVertices, new ContourLineAdder(creasePattern));

		// Delete segments outside of the contour
		removeLinesOutsideOfOutline(creasePattern, outlineVertices);
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
		var painter = new Painter(creasePattern);
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
