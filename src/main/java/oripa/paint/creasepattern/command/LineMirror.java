package oripa.paint.creasepattern.command;

import java.util.ArrayList;
import java.util.Collection;

import javax.vecmath.Vector2d;

import oripa.geom.GeomUtil;
import oripa.geom.Line;
import oripa.value.OriLine;

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

			copiedLines.add(createMirrorCopiedLine(line, baseLine));
		}

		return copiedLines;
	}    

	/**
	 * 
	 * @param line         a line to be mirrored
	 * @param baseOriLine  a line to be axis of symmetry
	 * @return mirrored line
	 */
	private OriLine createMirrorCopiedLine(
			OriLine line, OriLine baseOriLine) {
		Line baseLine = baseOriLine.getLine();
		double dist0 = GeomUtil.Distance(line.p0, baseLine);
		Vector2d dir0 = new Vector2d();
		if (GeomUtil.isRightSide(line.p0, baseLine)) {
			dir0.set(-baseLine.dir.y, baseLine.dir.x);
		} else {
			dir0.set(baseLine.dir.y, -baseLine.dir.x);
		}
		dir0.normalize();
		Vector2d q0 = new Vector2d(
				line.p0.x + dir0.x * dist0 * 2,
				line.p0.y + dir0.y * dist0 * 2);

		double dist1 = GeomUtil.Distance(line.p1, baseLine);
		Vector2d dir1 = new Vector2d();
		if (GeomUtil.isRightSide(line.p1, baseLine)) {
			dir1.set(-baseLine.dir.y, baseLine.dir.x);
		} else {
			dir1.set(baseLine.dir.y, -baseLine.dir.x);
		}
		dir1.normalize();
		Vector2d q1 = new Vector2d(
				line.p1.x + dir1.x * dist1 * 2,
				line.p1.y + dir1.y * dist1 * 2);

		OriLine mirroredLine = new OriLine(q0, q1, line.typeVal);

		return mirroredLine;
	}

}
