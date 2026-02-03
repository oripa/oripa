/*
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2013-     ORIPA OSS Project  https://github.com/oripa/oripa
 * Copyright (C) 2005-2009 Jun Mitani         http://mitani.cs.tsukuba.ac.jp/
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package oripa.domain.cptool;

import java.util.Collection;
import java.util.Optional;

import oripa.geom.RectangleDomain;
import oripa.geom.Segment;
import oripa.value.OriLine;
import oripa.vecmath.Vector2d;

/**
 * Manages OriLine intersection testing (clipping) with Rectangle Domain
 */
public class RectangleClipper {

    // This implementation encodes to flag bits the position of the end points
    // of the given line. Each bit is 1 if the point is outside of the border
    // line of the rectangle. For example, if LEFT bit is 1 then the point is on
    // the left outside of the rectangle. A point is inside of the rectangle if
    // all bits are 0.
    private static final int LEFT = 1;
    private static final int RIGHT = 1 << 1;
    private static final int TOP = 1 << 2;
    private static final int BOTTOM = 1 << 3;

    private final RectangleDomain domain;
    private final RectangleDomain relaxedDomain;

    /**
     *
     * The domain for clipping is slightly larger than given domain. The margin
     * of the relaxation is determined by epsilon.
     *
     * @param domain
     *            Rectangle domain for clipping
     * @param epsilon
     *            Margin
     */
    public RectangleClipper(final RectangleDomain domain, final double epsilon) {
        double minX = domain.getLeft() - epsilon;
        double minY = domain.getTop() - epsilon;
        double maxX = domain.getRight() + epsilon;
        double maxY = domain.getBottom() + epsilon;

        this.domain = domain;
        relaxedDomain = new RectangleDomain(minX, minY, maxX, maxY);
    }

    public RectangleClipper(final double x0, final double y0, final double x1, final double y1, final double epsilon) {
        this(new RectangleDomain(x0, y0, x1, y1), epsilon);
    }

    /**
     * extracts lines which intersects this rectangle from given {@code lines}
     *
     * @param lines
     * @return Subset of {@code lines}
     */
    public <T extends Segment> Collection<T> selectByArea(final Collection<T> lines) {
        return lines.stream()
                .filter(this::intersects)
                .toList();
    }

    /**
     * Tells us whether the given line {@code line} intersects the rectangle.
     * The test is inclusive.
     *
     * @param line
     *            to be tested.
     *
     * @return {@code true} if {@code line} is included in or crosses the
     *         clipping rectangle.
     */
    public boolean intersects(final Segment line) {
        return clip(line).isPresent();
    }

    /**
     * OriLine version of {@link #clip(Segment)}.
     *
     * @param line
     *            to be clipped.
     * @return a clipped line. It can be zero-length line if the given line
     *         touches the rectangle. empty if no intersection.
     */
    public Optional<OriLine> clip(final OriLine line) {
        return clip((Segment) line)
                .map(clipped -> new OriLine(clipped, line.getType()));
    }

    /**
     * Returns a new segment that is the result of intersection with the
     * rectangle. The test is inclusive.
     *
     * @param segment
     *            to be clipped.
     * @return clipped segment. It can be zero-length segment if the given line
     *         touches the rectangle. empty if no intersection.
     */
    public Optional<Segment> clip(final Segment segment) {
        var p0 = segment.getP0();
        var p1 = segment.getP1();

        // first to avoid parameter modification
        final int p0Code = calcCode(p0.getX(), p0.getY());
        final int p1Code = calcCode(p1.getX(), p1.getY());

        // the line is in the rectangle
        if ((p0Code == 0) && (p1Code == 0)) {
            return Optional.of(segment);
        }

        // the line is in the {left, right, top, bottom} area.
        if ((p0Code & p1Code) != 0) {
            return Optional.empty();
        }

        var cp0Opt = createClippedPointOptional(p0Code, segment);
        var cp1Opt = createClippedPointOptional(p1Code, segment);

        Optional<Segment> clippedOpt = Optional.empty();

        if (p0Code != 0 && p1Code != 0) {
            // p0 and p1 are in the outside of the rectangle and
            // the line may cross the two edges of the rectangle.
            clippedOpt = cp0Opt.map(cp0 -> cp1Opt.map(cp1 -> new Segment(cp0, cp1)).orElse(null));
        } else if (p0Code != 0) {
            // p0 is in the outside of the rectangle and p1 is inside of the
            // rectangle.
            // The line may cross the {left, right, top, bottom} edge of the
            // rectangle.
            clippedOpt = cp0Opt.map(cp0 -> new Segment(cp0, p1));
        } else if (p1Code != 0) {
            // p1 is in the outside of the rectangle and p0 is inside the
            // rectangle.
            // The line may cross the {left, right, top, bottom} edge of the
            // rectangle.
            clippedOpt = cp1Opt.map(cp1 -> new Segment(p0, cp1));
        }

        return clippedOpt;

        // very short line is not preferable but such test disables to detect a
        // diagonal line touching the corner of the rectangle.
        // return clippedOpt.filter(clipped -> clipped.length() >= eps);
    }

    /**
     * Calculate quadrant in which Point {@code x, y} lies
     *
     * @param x
     * @param y
     * @return
     */
    private int calcCode(final double x, final double y) {
        int code = 0;
        if (x < relaxedDomain.getLeft()) {
            code += LEFT;
        }
        if (x > relaxedDomain.getRight()) {
            code += RIGHT;
        }
        if (y < relaxedDomain.getTop()) {
            code += TOP;
        }
        if (y > relaxedDomain.getBottom()) {
            code += BOTTOM;
        }

        return code;
    }

    /**
     * finds the coordinates after clipping.
     *
     * @param code
     *            flag bits of an end point of the given line
     * @param l
     *            line to be clipped
     * @return clipped point. Empty if The line doesn't intersect the rectangle.
     */
    private Optional<Vector2d> createClippedPointOptional(final int code, final Segment l) {

        // Outside from the left edge of the window
        if ((code & LEFT) != 0) {
            var cpOpt = createPointClippedByX(l, domain.getLeft());
            if (cpOpt.isPresent()) {
                return cpOpt;
            }
        }

        // Outside the right edge of the window
        if ((code & RIGHT) != 0) {
            var cpOpt = createPointClippedByX(l, domain.getRight());
            if (cpOpt.isPresent()) {
                return cpOpt;
            }
        }

        // Outside from the top of the window
        if ((code & TOP) != 0) {
            var cpOpt = createPointClippedByY(l, domain.getTop());
            if (cpOpt.isPresent()) {
                return cpOpt;
            }
        }

        // Outside from the bottom of the window
        if ((code & BOTTOM) != 0) {
            var cpOpt = createPointClippedByY(l, domain.getBottom());
            if (cpOpt.isPresent()) {
                return cpOpt;
            }
        }

        return Optional.empty();
    }

    private Optional<Vector2d> createPointClippedByX(final Segment l, final double clipX) {
        var yRange = relaxedDomain.getYRange();

        var cy = l.getAffineYValueAt(clipX);
        if (yRange.includes(cy)) {
            double px = clipX;
            double py = cy;
            return Optional.of(new Vector2d(px, py));
        }

        return Optional.empty();
    }

    private Optional<Vector2d> createPointClippedByY(final Segment l, final double clipY) {
        var xRange = relaxedDomain.getXRange();

        var cx = l.getAffineXValueAt(clipY);
        if (xRange.includes(cx)) {
            double px = cx;
            double py = clipY;
            return Optional.of(new Vector2d(px, py));
        }

        return Optional.empty();
    }

}
