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
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.geom.GeomUtil;
import oripa.geom.Line;
import oripa.geom.Segment;
import oripa.vecmath.Vector2d;

/**
 * Origami axiom 3
 *
 * @author OUCHI Koji
 *
 */
public class LineToLineAxiom {
    private static final Logger logger = LoggerFactory.getLogger(LineToLineAxiom.class);

    public List<Line> createFoldLines(final Segment s0, final Segment s1, final double pointEps) {

        var line0 = s0.getLine();
        var line1 = s1.getLine();

        if (line0.equals(line1, pointEps)) {
            if (GeomUtil.isOverlap(s0, s1, pointEps)) {
                // out of consideration.
                return List.of();
            }
            return createForSegmentsOnTheSameLine(s0, s1);
        }

        if (line0.isParallel(line1)) {
            return createForParallelSegments(line0, line1);
        }

        var segmentCrossPointOpt = GeomUtil.getCrossPoint(s0, s1);

        return segmentCrossPointOpt
                .map(crossPoint -> createForSegmentsWithCross(s0, s1, crossPoint, pointEps))
                .orElse(createForSegmentsWithoutCross(s0, s1, pointEps));

    }

    /**
     * Extended interpretation of the axiom. This method returns the
     * perpendicular bisector of the closest points between the given segments.
     *
     * @param s0
     * @param s1
     * @return
     */
    private List<Line> createForSegmentsOnTheSameLine(final Segment s0, final Segment s1) {
        var p0 = GeomUtil.getNearestPointToSegment(s1.getP0(), s0);
        var p1 = GeomUtil.getNearestPointToSegment(s0.getP0(), s1);

        var p = p0.add(p1).multiply(0.5);

        return List.of(new Line(p, s0.getLine().getDirection().getRightSidePerpendicular()));
    }

    private List<Line> createForParallelSegments(final Line line0, final Line line1) {

        var point = line0.getPoint();

        var dir0 = line0.getDirection();
        var verticalLine = new Line(point, dir0.getRightSidePerpendicular());

        var crossPointOpt = GeomUtil.getCrossPoint(verticalLine, line1);

        return crossPointOpt.map(crossPoint -> {
            var midPoint = point.add(crossPoint).multiply(0.5);
            return List.of(new Line(midPoint, dir0));
        }).orElse(List.of());
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

        var sharedOpt = s0.getSharedEndPoint(s1, pointEps);

        return sharedOpt.map(shared -> createUsingCrossPoint(s0, s1, shared))
                .orElseGet(() -> {
                    var line0 = s0.getLine();
                    var line1 = s1.getLine();

                    var pointOnLine0 = segmentCrossPoint.add(line0.getDirection());

                    var pointOnLine1 = segmentCrossPoint.add(line1.getDirection());

                    var reversedDir = line1.getDirection().multiply(-1);
                    var pointOnLine1Reversed = segmentCrossPoint.add(reversedDir);

                    return List.of(
                            GeomUtil.getBisectorLine(pointOnLine0, segmentCrossPoint, pointOnLine1),
                            GeomUtil.getBisectorLine(pointOnLine0, segmentCrossPoint, pointOnLine1Reversed));

                });
    }

    private List<Line> createForSegmentsWithoutCross(final Segment s0, final Segment s1, final double pointEps) {
        logger.trace("s0 = {}", s0);
        logger.trace("s1 = {}", s1);

        var lines = tryPotentialCross(s0, s1, pointEps)
                .orElse(tryPotentialCross(s1, s0, pointEps)
                        .orElse(List.of()));

        if (lines.isEmpty()) {
            // no potential cross on segments
            var line0 = s0.getLine();
            var line1 = s1.getLine();
            var lineCrossPointOpt = GeomUtil.getCrossPoint(line0, line1);
            return lineCrossPointOpt
                    .map(lineCrossPoint -> createUsingCrossPoint(s0, s1, lineCrossPoint))
                    .orElse(List.of());
        }

        return lines;
    }

    private Optional<List<Line>> tryPotentialCross(final Segment s0, final Segment s1, final double pointEps) {
        var line1 = s1.getLine();
        return GeomUtil.getCrossPoint(line1, s0)
                .filter(cp -> s0.pointStream().noneMatch(p -> p.equals(cp, pointEps)))
                .map(cp -> createForPotentialCross(s0, s1, cp));
    }

    private List<Line> createForPotentialCross(
            final Segment segmentIncludingCrossPoint, final Segment s, final Vector2d lineCrossPoint) {
        var point0a = segmentIncludingCrossPoint.getP0();
        var point0b = segmentIncludingCrossPoint.getP1();
        var point1 = selectFarEndPoint(s, lineCrossPoint);

        return List.of(
                GeomUtil.getBisectorLine(point0a, lineCrossPoint, point1),
                GeomUtil.getBisectorLine(point0b, lineCrossPoint, point1));
    }

    private List<Line> createUsingCrossPoint(final Segment s0, final Segment s1, final Vector2d crossPoint) {
        var point0 = selectFarEndPoint(s0, crossPoint);
        var point1 = selectFarEndPoint(s1, crossPoint);

        return List.of(GeomUtil.getBisectorLine(point0, crossPoint, point1));
    }

    private Vector2d selectFarEndPoint(final Segment s, final Vector2d p) {
        return s.getP0().distance(p) > s.getP1().distance(p)
                ? s.getP0()
                : s.getP1();
    }

}
