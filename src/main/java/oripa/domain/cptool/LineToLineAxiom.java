/**
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2013-     ORIPA OSS Project  https://github.com/oripa/oripa
 * Copyright (C) 2005-2009 Jun Mitani         http://mitani.cs.tsukuba.ac.jp/

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package oripa.domain.cptool;

import java.util.List;

import javax.vecmath.Vector2d;

import oripa.geom.GeomUtil;
import oripa.geom.Line;
import oripa.geom.Segment;

/**
 * @author OUCHI Koji
 *
 */
public class LineToLineAxiom {
	public List<Line> createFoldLines(final Segment s0, final Segment s1, final double pointEps) {

		var line0 = s0.getLine();
		var line1 = s1.getLine();

		if (GeomUtil.isParallel(line0.dir, line1.dir)) {
			return createForParallelSegments(line0, line1);
		} else {

			var segmentCrossPoint = GeomUtil.getCrossPoint(s0, s1);

			if (segmentCrossPoint == null) {
				return createForSegmentsWithoutCross(s0, s1, pointEps);
			} else {
				return createForSegmentsWithCross(s0, s1, segmentCrossPoint, pointEps);
			}
		}
	}

	private List<Line> createForParallelSegments(final Line line0, final Line line1) {

		var point = line0.p;

		var dir0 = line0.dir;
		var verticalLine = new Line(point, new Vector2d(-dir0.getY(), dir0.getX()));

		var crossPoint = GeomUtil.getCrossPoint(verticalLine, line1);

		var midPoint = new Vector2d(point.getX() + crossPoint.getX(), point.getY() + crossPoint.getY());
		midPoint.scale(0.5);

		return List.of(new Line(midPoint, dir0));
	}

	/**
	 * For version compatibility: Probably won't happen on recent versions since
	 * current ORIPA always makes a cross point after input.
	 *
	 * @param s0
	 * @param s1
	 * @param segmentCrossPoint
	 * @param pointEps
	 * @return
	 */
	private List<Line> createForSegmentsWithCross(final Segment s0, final Segment s1,
			final Vector2d segmentCrossPoint, final double pointEps) {

		if (GeomUtil.areEqual(s0.getP0(), s1.getP0(), pointEps) ||
				GeomUtil.areEqual(s0.getP0(), s1.getP1(), pointEps) ||
				GeomUtil.areEqual(s0.getP1(), s1.getP0(), pointEps) ||
				GeomUtil.areEqual(s0.getP1(), s1.getP1(), pointEps)) {
			return createForSegmentsWithoutCross(s0, s1, pointEps);
		}

		var line0 = s0.getLine();
		var line1 = s1.getLine();

		var pointOnLine0 = new Vector2d(segmentCrossPoint);
		pointOnLine0.add(line0.dir);

		var pointOnLine1 = new Vector2d(segmentCrossPoint);
		pointOnLine1.add(line1.dir);

		var pointOnLine1Reversed = new Vector2d(segmentCrossPoint);
		var reversedDir = new Vector2d(line1.dir);
		reversedDir.scale(-1);
		pointOnLine1Reversed.add(reversedDir);

		var foldLineDir0 = GeomUtil.getBisectorVec(pointOnLine0, segmentCrossPoint, pointOnLine1);

		var foldLineDir1 = GeomUtil.getBisectorVec(pointOnLine0, segmentCrossPoint, pointOnLine1Reversed);

		return List.of(new Line(segmentCrossPoint, foldLineDir0),
				new Line(segmentCrossPoint, foldLineDir1));
	}

	private List<Line> createForSegmentsWithoutCross(final Segment s0, final Segment s1, final double pointEps) {
		var line0 = s0.getLine();
		var line1 = s1.getLine();
		var lineCrossPoint = GeomUtil.getCrossPoint(line0, line1);

		var point0 = selectFarEndPoint(s0, lineCrossPoint);
		var point1 = selectFarEndPoint(s1, lineCrossPoint);

		var foldLineDir = GeomUtil.getBisectorVec(point0, lineCrossPoint, point1);

		return List.of(new Line(lineCrossPoint, foldLineDir));
	}

	private Vector2d selectFarEndPoint(final Segment s, final Vector2d p) {
		return GeomUtil.distance(s.getP0(), p) > GeomUtil.distance(s.getP1(), p)
				? s.getP0()
				: s.getP1();
	}

}
