package oripa.domain.cptool;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import oripa.geom.GeomUtil;
import oripa.geom.Line;
import oripa.value.OriLine;
import oripa.value.OriPoint;

public class LineMirror {
	/**
	 *
	 *
	 * @param baseLine
	 *            a line to be the axis of symmetry
	 * @param lines
	 *            lines to be mirrored. the .selected field will be ignored.
	 * @param creasePattern
	 *            destination of mirrored lines
	 */
	public Collection<OriLine> createMirroredLines(
			final OriLine baseLine, final Collection<OriLine> lines) {

		List<OriLine> copiedLines = lines.stream()
				.filter(line -> !line.equals(baseLine))
				.map(line -> createMirroredLine(line, baseLine))
				.collect(Collectors.toList());

		return copiedLines;
	}

	/**
	 * create a mirrored line.
	 *
	 * @param line
	 *            a line to be mirrored
	 * @param baseOriLine
	 *            a line to be axis of symmetry
	 * @return mirrored line
	 */
	private OriLine createMirroredLine(
			final OriLine line, final OriLine baseOriLine) {

		OriPoint q0 = createMirroredVertex(line.p0, baseOriLine);
		OriPoint q1 = createMirroredVertex(line.p1, baseOriLine);

		OriLine mirroredLine = new OriLine(q0, q1, line.getType());

		return mirroredLine;
	}

	/**
	 * create a mirrored vertex.
	 *
	 * @param vertex
	 *            p vertex to be mirrored
	 * @param baseOriLine
	 *            a line to be axis of symmetry
	 * @return
	 */
	private OriPoint createMirroredVertex(
			final OriPoint vertex, final OriLine baseOriLine) {

		Line baseLine = baseOriLine.getLine();
		double dist0 = GeomUtil.Distance(vertex, baseLine);

		OriPoint dir0 = new OriPoint();
		if (GeomUtil.isRightSide(vertex, baseLine)) {
			dir0.set(-baseLine.dir.y, baseLine.dir.x);
		} else {
			dir0.set(baseLine.dir.y, -baseLine.dir.x);
		}
		dir0.normalize();
		OriPoint q0 = new OriPoint(
				vertex.x + dir0.x * dist0 * 2,
				vertex.y + dir0.y * dist0 * 2);

		return q0;
	}
}
