package oripa.paint.creasepattern.tool;

import java.util.ArrayList;
import java.util.Collection;

import oripa.geom.GeomUtil;
import oripa.geom.Line;
import oripa.value.OriLine;
import oripa.value.OriPoint;

public class LineMirror {
	/**
	 * 
	 * 
	 * @param baseLine       a line to be the axis of symmetry
	 * @param lines          lines to be mirrored
	 * @param creasePattern  destination of mirrored lines
	 */
	public Collection<OriLine> createMirroredLines(
			OriLine baseLine, Collection<OriLine> lines) {

		ArrayList<OriLine> copiedLines = new ArrayList<OriLine>(lines.size());
		for (OriLine line : lines) {
			if (!line.selected) {
				continue;
			}
			if (line.equals(baseLine)) {
				continue;
			}

			copiedLines.add(createMirroredLine(line, baseLine));
		}

		return copiedLines;
	}    

	/**
	 * create a mirrored line.
	 * @param line         a line to be mirrored
	 * @param baseOriLine  a line to be axis of symmetry
	 * @return mirrored line
	 */
	private OriLine createMirroredLine(
			OriLine line, OriLine baseOriLine) {
				
		OriPoint q0 = createMirroredVertex(line.p0, baseOriLine);
		OriPoint q1 = createMirroredVertex(line.p1, baseOriLine);

		OriLine mirroredLine = new OriLine(q0, q1, line.typeVal);

		return mirroredLine;
	}

	/**
	 * create a mirrored vertex.
	 * @param vertex       p vertex to be mirrored
	 * @param baseOriLine  a line to be axis of symmetry
	 * @return
	 */
	private OriPoint createMirroredVertex(
			OriPoint vertex, OriLine baseOriLine) {

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
