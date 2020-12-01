package oripa.domain.cptool;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.vecmath.Vector2d;

import oripa.geom.GeomUtil;
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
	 * @return mirrored lines
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

		Vector2d mirrored = GeomUtil.getSymmetricPoint(vertex, baseOriLine.p0, baseOriLine.p1);
		return new OriPoint(mirrored.x, mirrored.y);
	}
}
