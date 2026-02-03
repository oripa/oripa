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

import java.lang.invoke.MethodHandles;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.geom.GeomUtil;
import oripa.util.MathUtil;
import oripa.util.collection.CollectionUtil;
import oripa.value.OriLine;
import oripa.value.OriLine.Type;
import oripa.value.OriPoint;

/**
 * @author OUCHI Koji
 *
 */
public class OverlappingLineMergerOld {

    private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static class MyPointAndOriLine implements Comparable<MyPointAndOriLine> {
        private final OriPoint point;
        private final OriLine line;
        private final boolean isLeft;

        private final double angle;
        private final double intercept;

        private MyPointAndOriLine opposite;

        public static List<MyPointAndOriLine> createWithCanoincalization(final OriLine line) {
            var canonical = new OriLine(line.getP0(), line.getP1(), Type.MOUNTAIN).createCanonical();
            return create(canonical);
        }

        public static List<MyPointAndOriLine> create(final OriLine line) {
            var left = asLeft(line.getOriPoint0(), line);
            var right = asRight(line.getOriPoint1(), line);

            left.opposite = right;
            right.opposite = left;

            return List.of(left, right);
        }

        public static MyPointAndOriLine asLeft(final OriPoint point, final OriLine line) {
            return new MyPointAndOriLine(point, line, true);
        }

        public static MyPointAndOriLine asRight(final OriPoint point, final OriLine line) {
            return new MyPointAndOriLine(point, line, false);
        }

        private MyPointAndOriLine(final OriPoint point, final OriLine line, final boolean isLeft) {
            this.point = point;
            this.line = line;
            this.isLeft = isLeft;

            if (line.pointStream().noneMatch(point::equals)) {
                throw new IllegalArgumentException(
                        "point " + point + " should be equal to the one of the segment " + line + " end point.");
            }

            var p0 = line.getP0();
            var p1 = line.getP1();

            var angle = Math.atan2(p1.getY() - p0.getY(), p1.getX() - p0.getX());
            // limit the angle 0 to PI.
            if (angle < 0) {
                angle += Math.PI;
            }
            // a line with angle PI is the same as one with angle 0.
            if (Math.PI - angle < MathUtil.angleRadianEps()) {
                angle = 0;
            }
            this.angle = angle;

            if (isVertical()) {
                // use x-intercept
                intercept = p0.getX();
            } else {
                // use y-intercept
                intercept = line.getAffineYValueAt(0);
            }

        }

        public boolean isLeft() {
            return isLeft;
        }

        public boolean isRight() {
            return !isLeft;
        }

        public MyPointAndOriLine getLeft() {
            return isLeft ? this : opposite;
        }

        public MyPointAndOriLine getRight() {
            return isRight() ? this : opposite;
        }

        public boolean isVertical() {
            return MathUtil.areRadianEqual(Math.PI / 2, angle);
        }

        private OriPoint getOppositePoint() {
            return point == line.getOriPoint0() ? line.getOriPoint1() : line.getOriPoint0();
        }

        /**
         * @return line
         */
        public OriLine getLine() {
            return line;
        }

//		public double getAngle() {
//			return angle;
//		}
//
        public double getX() {
            return point.getX();
        }

        public double getY() {
            return point.getY();
        }

        @Override
        public int compareTo(final MyPointAndOriLine o) {
            var comp = point.compareTo(o.point);

            if (comp == 0) {
                return opposite.point.compareTo(o.getOppositePoint());
            }

            return comp;
        }

        @Override
        public int hashCode() {
            return Objects.hash(point, opposite.point);
        }

        /**
         * This comparison cares the {@link OriLine} equality only.
         */
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }

            if (obj instanceof MyPointAndOriLine other) {
                return compareTo(other) == 0;
            }
            return true;
        }

        @Override
        public String toString() {
            return (isLeft ? "left " : "right ") + line.toString();
        }
    }

    private static record SweepKey(double angle, double intercept, double coord) implements Comparable<SweepKey> {

        @Override
        public int compareTo(final SweepKey other) {
            if (angle != other.angle) {
                return (int) Math.signum(angle - other.angle);
            }
            if (intercept != other.intercept) {
                return (int) Math.signum(intercept - other.intercept);
            }
            return (int) Math.signum(coord - other.coord);
        }
    }

    private SweepKey toSweepKey(final MyPointAndOriLine p) {
        return new SweepKey(p.angle, p.intercept, p.isVertical() ? p.getY() : p.getX());
    }

    private SweepKey toSweepKey(final MyPointAndOriLine p, final TreeSet<SweepKey> keys, final double eps) {
        var key = findKey(p, keys, eps);
        if (key != null) {
            return key;
        }

        return toSweepKey(p);
    }

    private SweepKey findKey(final MyPointAndOriLine p, final TreeSet<SweepKey> sortedKeys, final double eps) {
        var key = toSweepKey(p);

        var coord = p.isVertical() ? p.getY() : p.getX();

        var range = CollectionUtil.rangeSetInclusive(sortedKeys,
                new SweepKey(key.angle - MathUtil.angleRadianEps(), key.intercept - eps, coord - eps),
                new SweepKey(key.angle + MathUtil.angleRadianEps(), key.intercept + eps, coord + eps));

        var filtered = range.stream()
                .filter(k -> MathUtil.areEqual(k.intercept, key.intercept, eps))
                .filter(k -> MathUtil.areEqual(k.coord, key.coord, eps));
        return filtered.findFirst().get();

    }

    private HashMap<MyPointAndOriLine, SweepKey> createKeyTable(final Collection<MyPointAndOriLine> points,
            final double eps) {
        var sorted = new TreeSet<SweepKey>();
        points.forEach(p -> sorted.add(toSweepKey(p)));

        var keyTable = new HashMap<MyPointAndOriLine, SweepKey>();

        for (var p : points) {

            var presentativeKey = findKey(p, sorted, eps);
            keyTable.put(p, presentativeKey);
        }

        return keyTable;
    }

    public Collection<OriLine> mergeIgnoringType(final Collection<OriLine> inputLines, final double eps) {
        // sweep line along x
        // for a pair of angle and intercept, only one mergeable line appears
        // when the sweep encounter a point.

        var results = new HashMap<SweepKey, OriLine>();

        var points = new ArrayList<MyPointAndOriLine>();
        for (var line : inputLines) {
            if (line.length() < eps) {
                continue;
            }
            points.addAll(MyPointAndOriLine.createWithCanoincalization(line));
        }

        var keyTable = createKeyTable(points, eps);
        var keys = new TreeSet<>(keyTable.values());

        var events = new PriorityQueue<MyPointAndOriLine>(points);

        var onSweepLine = new TreeMap<SweepKey, Set<MyPointAndOriLine>>();

        var p = events.peek();
        var eventLeftKey = keyTable.get(p);

        var eventPoint = p;
        var count = 0;
        while (!events.isEmpty() && count++ <= 2 * inputLines.size() * inputLines.size()) {
//		while (!events.isEmpty() && count++ <= 100) {
            eventPoint = events.poll();
            logger.trace("event : {}", eventPoint);
            // logger.trace("event : {}, remain: {}", eventPoint, events);

            eventLeftKey = keyTable.get(eventPoint.getLeft());
            var eventRightKey = keyTable.get(eventPoint.getRight());

            var merging = getMerging(eventLeftKey, eventPoint, onSweepLine, eps);

            var mergingLeft = merging == null ? null : merging.getLeft();
            var mergingRight = merging == null ? null : merging.getRight();
            var mergingLeftKey = merging == null ? null : toSweepKey(mergingLeft, keys, eps);
            var mergingRightKey = merging == null ? null : toSweepKey(mergingRight, keys, eps);

            logger.trace("sweep key:{} merging value: {}", eventLeftKey, merging);

            if (eventPoint.isRight()) {
                events.remove(eventPoint.opposite);
                remove(eventLeftKey, eventPoint, onSweepLine);
                remove(eventRightKey, eventPoint, onSweepLine);

                if (mergingRightKey == null) {
                    results.put(eventRightKey, eventPoint.getLine());
                    continue;
                }

                // merging point is on the right.
                // it should be treated in the future.
                if (getComparator(merging).compare(merging, eventPoint) == 1) {
                    continue;
                }

                var merged = merge(merging, eventPoint, eps);
                logger.trace("merge result: {}", merged);

                events.remove(mergingLeft);
                events.remove(mergingRight);
                remove(mergingLeftKey, mergingLeft, onSweepLine);
                remove(mergingRightKey, mergingRight, onSweepLine);

                var mergedLeft = merged.get(0);
                var mergedRight = merged.get(1);
                var mergedLeftKey = toSweepKey(mergedLeft, keys, eps);
                var mergedRightKey = toSweepKey(mergedRight, keys, eps);

                add(mergedRightKey, mergedRight, onSweepLine);
                keyTable.put(mergedLeft, mergedLeftKey);
                keyTable.put(mergedRight, mergedRightKey);

                results.remove(mergingRightKey);
                results.remove(eventRightKey);
                results.put(mergedRightKey, mergedRight.getLine());

                continue;
            }

            if (merging == null) {
                merging = eventPoint;
                mergingLeft = eventPoint.getLeft();
                mergingRight = eventPoint.getRight();
                mergingLeftKey = eventLeftKey;
                mergingRightKey = eventRightKey;
            }

            // event is left side

            // remove points to be old.
            var rightEventPoint = eventPoint.opposite;
            events.remove(rightEventPoint);
            events.remove(mergingRight);
            remove(eventLeftKey, eventPoint, onSweepLine);
            remove(eventRightKey, rightEventPoint, onSweepLine);
            remove(mergingLeftKey, mergingLeft, onSweepLine);
            remove(mergingRightKey, mergingRight, onSweepLine);

            logger.trace("after remove, remain: {}", events);

            var merged = merge(merging, eventPoint, eps);
            logger.trace("merge result: {}", merged);

            var mergedLeft = merged.get(0);
            var mergedRight = merged.get(1);
            var mergedLeftKey = toSweepKey(mergedLeft, keys, eps);
            var mergedRightKey = toSweepKey(mergedRight, keys, eps);

            events.add(mergedRight);

            keyTable.put(mergedLeft, mergedLeftKey);
            keyTable.put(mergedRight, mergedRightKey);

            // left has been swept and no need to be remembered.
            add(mergedRightKey, mergedRight, onSweepLine);

            results.remove(mergingRightKey);
            results.remove(eventRightKey);

            logger.trace("after add, remain: {}", events);

            logger.trace("on sweep line {}", onSweepLine);
        }

        logger.trace("results: {}", results);

        logger.info("loop count: {}", count);

        return results.values().stream().toList();

    }

    private List<MyPointAndOriLine> merge(final MyPointAndOriLine merging, final MyPointAndOriLine eventPoint,
            final double eps) {

        if (merging.getLine().equals(eventPoint.getLine(), eps)) {
            var list = List.of(merging, merging.opposite)
                    .stream()
                    .sorted(getComparator(eventPoint))
                    .toList();

            return MyPointAndOriLine.create(
                    new OriLine(list.getFirst().point, list.getLast().point, Type.MOUNTAIN));
        }

        var list = List.of(merging, merging.opposite, eventPoint, eventPoint.opposite)
                .stream()
                .sorted(getComparator(eventPoint))
                .toList();

        var merged = MyPointAndOriLine.create(
                new OriLine(list.getFirst().point, list.getLast().point, Type.MOUNTAIN));

        if (merged.get(0).getLine().length() + eps < merging.getLine().length()
                || merged.get(0).getLine().length() + eps < eventPoint.getLine().length()) {
            throw new IllegalStateException(
                    "wrong merge. %s %s result in %s".formatted(merging, eventPoint, merged.get(0)));
        }

        return merged;
    }

    private MyPointAndOriLine getMerging(final SweepKey key, final MyPointAndOriLine p,
            final TreeMap<SweepKey, Set<MyPointAndOriLine>> onSweepLine, final double eps) {

        // note that intercept can be in [-inf, inf].
        var group = CollectionUtil.rangeMapInclusive(onSweepLine,
                new SweepKey(key.angle - eps, key.intercept - eps, Double.MIN_VALUE),
                new SweepKey(key.angle + eps, key.intercept + eps, Double.MAX_VALUE));

        var candidates = new TreeSet<MyPointAndOriLine>(getComparator(p));

        // FIXME: O(n log n)
        group.forEach((key_, points) -> {
            if (MathUtil.areEqual(key.intercept, key_.intercept, eps)) {
                candidates.addAll(points);
            }
        });

        logger.trace("merging's candidates {}", candidates);

        var lower = candidates.lower(p);
        var higher = candidates.higher(p);

        var merging = lower == null ? higher : lower;

        if (merging == null) {
            return null;
        }

        var overlapType = GeomUtil.distinguishSegmentsOverlap(merging.getLine(), p.getLine(), eps);
        var canMerge = switch (overlapType) {
        case 2, 3, 4 -> true;
        default -> false;
        };

        if (!merging.getLine().getLine().isParallel(p.getLine().getLine())) {
            logger.trace("not parallel");
        }
        logger.trace("overlap type {}, {} {}", overlapType, p.getLine(), merging.getLine());

        return canMerge ? merging : null;
    }

    private Comparator<MyPointAndOriLine> getComparator(final MyPointAndOriLine p) {
        return p.isVertical()
                ? Comparator.comparing(MyPointAndOriLine::getY)
                : Comparator.comparing(MyPointAndOriLine::getX);
    }

    private MyPointAndOriLine get(final SweepKey key, final MyPointAndOriLine p,
            final TreeMap<SweepKey, Set<MyPointAndOriLine>> onSweepLine) {

        var set = onSweepLine.get(key);
        if (set == null || set.isEmpty()) {
            return null;
        }

        if (set.contains(p)) {
            return p;
        }

        return onSweepLine.get(key).iterator().next();
    }

    private void add(final SweepKey key, final MyPointAndOriLine p,
            final TreeMap<SweepKey, Set<MyPointAndOriLine>> onSweepLine) {
        onSweepLine.putIfAbsent(key, new HashSet<>());

        onSweepLine.get(key).add(p);
    }

    private MyPointAndOriLine remove(final SweepKey key, final MyPointAndOriLine p,
            final TreeMap<SweepKey, Set<MyPointAndOriLine>> onSweepLine) {
        var set = onSweepLine.get(key);

        if (set == null || set.isEmpty()) {
            return null;
        }

        set.remove(get(key, p, onSweepLine));

        return p;
    }
}
