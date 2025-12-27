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
import java.util.function.BiFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.cptool.compgeom.HashFactory;
import oripa.geom.GeomUtil;
import oripa.util.MathUtil;
import oripa.util.Pair;
import oripa.util.collection.CollectionUtil;
import oripa.value.OriLine;
import oripa.value.OriLine.Type;
import oripa.value.OriPoint;

/**
 * @author OUCHI Koji
 *
 */
public class AnalyticOverlappingLineMerger implements OverlappingLineMerger {

	private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private static class MyPointAndOriLineFactory {
		public static List<MyPointAndOriLine> createWithCanoincalization(final OriLine line) {

			var list = create(line).stream().sorted().toList();

			list.get(0).isLeft = true;
			list.get(1).isLeft = false;

			return list;
		}

		public static List<MyPointAndOriLine> create(final OriLine line) {
			var left = asLeft(line.getOriPoint0(), line);
			var right = asRight(line.getOriPoint1(), line);

			left.opposite = right;
			right.opposite = left;

			left.oppositeKey = right.getKey();
			right.oppositeKey = left.getKey();

			return List.of(left, right);
		}

		public static MyPointAndOriLine asLeft(final OriPoint point, final OriLine line) {
			var left = new MyPointAndOriLine(point, line, true);
			left.key = toSweepKey(left);

			return left;
		}

		public static MyPointAndOriLine asRight(final OriPoint point, final OriLine line) {
			var right = new MyPointAndOriLine(point, line, false);
			right.key = toSweepKey(right);

			return right;
		}

	}

	private static class MyPointAndOriLine implements Comparable<MyPointAndOriLine> {
		private final OriPoint point;
		private final OriLine line;
		private boolean isLeft;

		private final double intercept;
		private final double coord;

		private SweepKey key;
		private SweepKey oppositeKey;

		private MyPointAndOriLine opposite;

		private MyPointAndOriLine(final OriPoint point, final OriLine line, final boolean isLeft) {
			this.point = point;
			this.line = line;
			this.isLeft = isLeft;

			if (line.pointStream().noneMatch(point::equals)) {
				throw new IllegalArgumentException(
						"point " + point + " should be equal to the one of the segment " + line + " end point.");
			}

			var p0 = line.getP0();

			if (line.isVertical()) {
				// use x-intercept
				intercept = p0.getX();
				coord = point.getY();
			} else {
				// use y-intercept
				intercept = line.getAffineYValueAt(0);
				coord = point.getX();
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

		public double getAngle() {
			return line.getAngle();
		}

		public boolean isVertical() {
			return line.isVertical();
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

		public double getCoord() {
			return coord;
		}

		public double getIntercept() {
			return intercept;
		}

		public SweepKey getKey() {
			return key;
		}

		@Override
		public int compareTo(final MyPointAndOriLine o) {

			var comp = key.compareTo(o.key);

			if (comp == 0) {
				return oppositeKey.compareTo(o.oppositeKey);
			}

			return comp;
		}

		@Override
		public int hashCode() {
			return Objects.hash(point, opposite.point);
		}

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

	private static record SweepKey(double intercept, double coord) implements Comparable<SweepKey> {

		@Override
		public int compareTo(final SweepKey other) {
//			if (angle != other.angle) {
//				return (int) Math.signum(angle - other.angle);
//			}
			if (coord != other.coord) {
				return (int) Math.signum(coord - other.coord);

			}

			return (int) Math.signum(intercept - other.intercept);
		}

		private static Comparator<SweepKey> getComparatorOnSweep() {
			return (a, b) -> {
				if (a.intercept != b.intercept) {
					return (int) Math.signum(a.intercept - b.intercept);

				}

				return (int) Math.signum(a.coord - b.coord);
			};
		}
	}

	private static SweepKey toSweepKey(final MyPointAndOriLine p) {
		return new SweepKey(p.intercept, p.getCoord());
	}

	private SweepKey toSweepKey(final MyPointAndOriLine p, final TreeSet<SweepKey> keys, final double eps) {
		var key = findKey(p, keys, eps);
		if (key != null) {
			return key;
		}

		return p.getKey();
	}

	private SweepKey findKey(final MyPointAndOriLine p, final TreeSet<SweepKey> sortedKeys, final double eps) {
		var key = p.getKey();

		var coord = p.getCoord();

		var range = CollectionUtil.rangeSetInclusive(sortedKeys,
				new SweepKey(key.intercept - eps, coord - eps),
				new SweepKey(key.intercept + eps, coord + eps));

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

	@Override
	public Collection<OriLine> mergeIgnoringType(final Collection<OriLine> inputLines, final double eps) {

		return runNaive(inputLines, eps);
	}

	private Collection<OriLine> runNaive(final Collection<OriLine> inputLines, final double eps) {
		var results = new HashSet<OriLine>();

		var points = new ArrayList<MyPointAndOriLine>();
		for (var line : inputLines) {
			if (line.length() < eps) {
				continue;
			}
			points.add(MyPointAndOriLineFactory.createWithCanoincalization(line).get(0));
		}

		points.sort(Comparator.comparing(MyPointAndOriLine::getAngle));

		var hashFactory = new HashFactory();
		var byAngles = hashFactory.create(points, MyPointAndOriLine::getAngle, MathUtil.angleRadianEps());

		for (var byAngle : byAngles) {
			var byIntercepts = hashFactory.create(byAngle, MyPointAndOriLine::getIntercept, eps);
			for (var byIntercept : byIntercepts) {
				results.addAll(executeNaive(byIntercept, eps));
			}
		}

		return results;

	}

	private Collection<OriLine> executeNaive(final Collection<MyPointAndOriLine> byIntercept, final double eps) {

		var sortedPoints = byIntercept.stream().sorted((a, b) -> Double.compare(a.coord, b.coord)).toList();

		var indexToPointPairs = new HashMap<Integer, Pair<MyPointAndOriLine, MyPointAndOriLine>>();

		int i = 0;

		while (i < sortedPoints.size()) {
			var left = sortedPoints.get(i);
			// overlap with itself
			indexToPointPairs.put(i, new Pair<MyPointAndOriLine, MyPointAndOriLine>(left, left.opposite));

			BiFunction<Integer, Integer, MyPointAndOriLine> getRight = (a, b) -> {
				var newRight = sortedPoints.get(b).getRight();

				var oldRight = indexToPointPairs.get(a).v2();

				if (oldRight.coord + eps >= newRight.coord) {
					return oldRight;
				}

				return newRight;
			};

			// update
			int j = i + 1;
			while (j < sortedPoints.size()) {

				var oldPair = indexToPointPairs.get(i);
				var oldLine = new OriLine(oldPair.v1().point, oldPair.v2().point, OriLine.Type.MOUNTAIN);

				var p = sortedPoints.get(j);

				if (GeomUtil.isOverlap(oldLine, p.line, eps)) {
					var right = getRight.apply(i, j);
					indexToPointPairs.put(i, new Pair<MyPointAndOriLine, MyPointAndOriLine>(left, right));
					j++;
				} else {
					break;
				}
			}
			i = j;
		}

		// restore from index pairs
		var results = new ArrayList<OriLine>();
		for (var leftIndex : indexToPointPairs.keySet()) {
			var points = indexToPointPairs.get(leftIndex);
			var line = new OriLine(points.v1().point, points.v2().point, OriLine.Type.MOUNTAIN);

			results.add(line);
		}

		return results;
	}

	private Collection<OriLine> run(final Collection<OriLine> inputLines, final double eps) {
		var results = new HashSet<OriLine>();

		var points = new ArrayList<MyPointAndOriLine>();
		for (var line : inputLines) {
			if (line.length() < eps) {
				continue;
			}
			points.addAll(MyPointAndOriLineFactory.createWithCanoincalization(line));
		}

		points.sort(Comparator.comparing(MyPointAndOriLine::getAngle));

		var hashFactory = new HashFactory();
		var byAngles = hashFactory.create(points, MyPointAndOriLine::getAngle, MathUtil.angleRadianEps());

		for (var byAngle : byAngles) {
			results.addAll(execute(byAngle, eps));
		}

		return results;

	}

	// FIXME: a little buggy
	private Collection<OriLine> execute(final Collection<MyPointAndOriLine> points, final double eps) {
		// sweep line on coord-intercept space, along coord.
		// for a pair of angle and intercept, only one mergeable line appears
		// when the sweep encounter a point.

		var results = new HashMap<SweepKey, OriLine>();

		var keyTable = createKeyTable(points, eps);
		var keys = new TreeSet<>(keyTable.values());

		var events = new PriorityQueue<MyPointAndOriLine>(points);

		var onSweepLine = new TreeMap<SweepKey, Set<MyPointAndOriLine>>(SweepKey.getComparatorOnSweep());

		var p = events.peek();
		var eventLeftKey = keyTable.get(p);

		var eventPoint = p;
		var count = 0;
		while (!events.isEmpty() && count++ <= 2 * points.size() * points.size()) {
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
				if (getComparatorOnSweep().compare(merging, eventPoint) == 1) {
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
					.sorted(getComparatorOnSweep())
					.toList();

			return MyPointAndOriLineFactory.create(
					new OriLine(list.getFirst().point, list.getLast().point, Type.MOUNTAIN));
		}

		var list = List.of(merging, merging.opposite, eventPoint, eventPoint.opposite)
				.stream()
				.sorted(getComparatorOnSweep())
				.toList();

		var merged = MyPointAndOriLineFactory.create(
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

		var group = CollectionUtil.rangeMapInclusive(onSweepLine,
				new SweepKey(key.intercept - eps, key.coord - eps),
				new SweepKey(key.intercept + eps, key.coord + eps));

		var candidates = new TreeSet<MyPointAndOriLine>(getComparatorOnSweep());

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

		logger.trace("overlap type {}, {} {}", overlapType, p.getLine(), merging.getLine());

		return canMerge ? merging : null;
	}

	private Comparator<MyPointAndOriLine> getComparatorOnSweep() {
		return Comparator.comparing(MyPointAndOriLine::getCoord);
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
