package oripa.domain.paint.outline;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.vecmath.Vector2d;

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
			OriLine line;
			Painter painter = new Painter(creasePattern);

			line = new OriLine(element, nextElement, OriLine.TYPE_CUT);
			painter.addLine(line);
			return true;
		}
	}

	public void execute(final CreasePatternInterface creasePattern,
			final Collection<Vector2d> outlinevertices) {

		// Delete the current outline

		List<OriLine> outlines = creasePattern.stream()
				.filter(line -> line.typeVal == OriLine.TYPE_CUT).collect(Collectors.toList());
		creasePattern.removeAll(outlines);

		// Update the contour line
		PairLoop.iterateAll(
				outlinevertices, new ContourLineAdder(creasePattern));

		// To delete a segment out of the contour
		while (true) {
			boolean bDeleteLine = false;
			for (OriLine line : creasePattern) {
				if (line.typeVal == OriLine.TYPE_CUT) {
					continue;
				}
				double eps = creasePattern.getPaperSize() * 0.001;
				Vector2d OnPoint0 = isOnTmpOutlineLoop(outlinevertices, line.p0, eps);
				Vector2d OnPoint1 = isOnTmpOutlineLoop(outlinevertices, line.p1, eps);

				Painter painter = new Painter(creasePattern);
				if (OnPoint0 != null && OnPoint0 == OnPoint1) {
					painter.removeLine(line);
					bDeleteLine = true;
					break;
				}

				if ((OnPoint0 == null && isOutsideOfTmpOutlineLoop(outlinevertices, line.p0))
						|| (OnPoint1 == null
								&& isOutsideOfTmpOutlineLoop(outlinevertices, line.p1))) {
					painter.removeLine(line);
					bDeleteLine = true;
					break;
				}
			}
			if (!bDeleteLine) {
				break;
			}
		}

		outlinevertices.clear();
		// ORIPA.mainFrame.uiPanel.modeChanged();

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
