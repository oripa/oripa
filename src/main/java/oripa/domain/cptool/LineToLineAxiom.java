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

import oripa.geom.GeomUtil;
import oripa.geom.Line;
import oripa.geom.Segment;
import oripa.vecmath.Vector2d;

/**
 * @author OUCHI Koji
 *
 */
public class LineToLineAxiom {
	public List<Line> createFoldLines(final Segment s0, final Segment s1, final double pointEps) {

		var line0 = s0.getLine();
		var line1 = s1.getLine();

		if (GeomUtil.isParallel(line0.getDirection(), line1.getDirection())) {
			return createForParallelSegments(line0, line1);
		} else {

			var segmentCrossPointOpt = GeomUtil.getCrossPoint(s0, s1);

			return segmentCrossPointOpt
					.map(crossPoint -> createForSegmentsWithCross(s0, s1, crossPoint, pointEps))
					.orElse(createForSegmentsWithoutCross(s0, s1, pointEps));
		}
	}

	private List<Line> createForParallelSegments(final Line line0, final Line line1) {

		var point = line0.getPoint();

		var dir0 = line0.getDirection();
		var verticalLine = new Line(point, dir0.getRightSidePerpendicular());

		var crossPointOpt = GeomUtil.getCrossPoint(verticalLine, line1);

		return crossPointOpt.map(crossPoint -> {
			var midPoint = point.add(crossPoint).multiply(0.5);
			return List.of(new Line(midPoint, dir0));
		}).get();
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

		var pointOnLine0 = segmentCrossPoint.add(line0.getDirection());

		var pointOnLine1 = segmentCrossPoint.add(line1.getDirection());

		var reversedDir = line1.getDirection().multiply(-1);
		var pointOnLine1Reversed = segmentCrossPoint.add(reversedDir);

		var foldLineDir0 = GeomUtil.getBisectorVec(pointOnLine0, segmentCrossPoint, pointOnLine1);

		var foldLineDir1 = GeomUtil.getBisectorVec(pointOnLine0, segmentCrossPoint, pointOnLine1Reversed);

		return List.of(new Line(segmentCrossPoint, foldLineDir0),
				new Line(segmentCrossPoint, foldLineDir1));
	}

	private List<Line> createForSegmentsWithoutCross(final Segment s0, final Segment s1, final double pointEps) {
		var line0 = s0.getLine();
		var line1 = s1.getLine();
		var lineCrossPointOpt = GeomUtil.getCrossPoint(line0, line1);

		return lineCrossPointOpt.map(lineCrossPoint -> {
			var point0 = selectFarEndPoint(s0, lineCrossPoint);
			var point1 = selectFarEndPoint(s1, lineCrossPoint);

			var foldLineDir = GeomUtil.getBisectorVec(point0, lineCrossPoint, point1);

			return List.of(new Line(lineCrossPoint, foldLineDir));
		}).get();
	}

	private Vector2d selectFarEndPoint(final Segment s, final Vector2d p) {
		return GeomUtil.distance(s.getP0(), p) > GeomUtil.distance(s.getP1(), p)
				? s.getP0()
				: s.getP1();
	}

}
