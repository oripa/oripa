package oripa.paint.creasepattern.tool;

import java.util.Collection;
import java.util.LinkedList;

import oripa.domain.cptool.LineAdder;
import oripa.value.OriLine;
import oripa.value.OriPoint;

/**
 * a factory for creating simple test data
 *
 * @author Koji
 *
 */
public class TestCreasePatternFactory {

	/**
	 * creates a X shape composed by four lines.
	 *
	 * @param size
	 * @param center
	 * @return
	 */
	public Collection<OriLine> createCrossedLines(final double size, final OriPoint center) {
		LinkedList<OriLine> creasePattern = new LinkedList<>();

		double partSize = size / 2;

		OriLine slashLeftTop = new OriLine(
				new OriPoint(center.x - partSize, center.y - partSize),
				center,
				OriLine.Type.MOUNTAIN);

		OriLine slashRightTop = new OriLine(
				new OriPoint(center.x + partSize, center.y - partSize),
				center,
				OriLine.Type.MOUNTAIN);

		OriLine slashRightBottom = new OriLine(
				new OriPoint(center.x + partSize, center.y + partSize),
				center,
				OriLine.Type.MOUNTAIN);

		OriLine slashLeftBottom = new OriLine(
				new OriPoint(center.x - partSize, center.y + partSize),
				center,
				OriLine.Type.MOUNTAIN);

		LineAdder adder = new LineAdder();

		adder.addLine(slashLeftTop, creasePattern);
		adder.addLine(slashRightTop, creasePattern);
		adder.addLine(slashRightBottom, creasePattern);
		adder.addLine(slashLeftBottom, creasePattern);

		return creasePattern;
	}
}
