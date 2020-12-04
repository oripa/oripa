package oripa.domain.paint.outline;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.vecmath.Vector2d;

import oripa.domain.cptool.OverlappingLineExtractor;
import oripa.domain.cptool.Painter;
import oripa.domain.creasepattern.CreasePatternInterface;
import oripa.domain.paint.util.PairLoop;
import oripa.value.OriLine;

public class CloseTempOutline {

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

		// To delete a segment out of the contour
		while (true) {
			boolean bDeleteLine = false;
			for (OriLine line : creasePattern) {
				if (line.isBoundary()) {
					continue;
				}
				double eps = creasePattern.getPaperSize() * 0.001;
				Vector2d onPoint0 = isOnTmpOutlineLoop(outlineVertices, line.p0, eps);
				Vector2d onPoint1 = isOnTmpOutlineLoop(outlineVertices, line.p1, eps);

				Painter painter = new Painter(creasePattern);
				if (onPoint0 != null && onPoint0 == onPoint1) {
					painter.removeLine(line);
					bDeleteLine = true;
					break;
				}

				if ((onPoint0 == null && isOutsideOfTmpOutlineLoop(outlineVertices, line.p0))
						|| (onPoint1 == null
								&& isOutsideOfTmpOutlineLoop(outlineVertices, line.p1))) {
					painter.removeLine(line);
					bDeleteLine = true;
					break;
				}
			}
			if (!bDeleteLine) {
				break;
			}
		}
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
