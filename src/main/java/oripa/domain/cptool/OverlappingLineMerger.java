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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.geom.GeomUtil;
import oripa.util.MathUtil;
import oripa.util.collection.CollectionUtil;
import oripa.value.OriLine;
import oripa.value.OriPoint;

/**
 * @author OUCHI Koji
 *
 */
public class OverlappingLineMerger {

	private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private static class MyPointAndOriLine implements Comparable<MyPointAndOriLine> {
		private final OriPoint point;
		private final OriLine line;
		private final boolean isLeft;

		private final double angle;
		private final double intercept;

		private MyPointAndOriLine opposite;

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
				return getOppositePoint().compareTo(o.getOppositePoint());
			}

			return comp;
		}

		@Override
		public int hashCode() {
			return line.hashCode();
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
				if (line == null) {
					if (other.line != null) {
						return false;
					}
				} else if (!line.equals(other.line)) {
					return false;
				}

			}
			return true;
		}

		@Override
		public String toString() {
			return (isLeft ? "left " : "right ") + line.toString();
		}
	}

	private static record MergingKey(double angle, double intercept, double coord) implements Comparable<MergingKey> {

		@Override
		public int compareTo(final MergingKey other) {
			if (angle != other.angle) {
				return (int) Math.signum(angle - other.angle);
			}
			if (intercept != other.intercept) {
				return (int) Math.signum(intercept - other.intercept);
			}
			return (int) Math.signum(coord - other.coord);
		}
	}

	private MergingKey toMergingKey(final MyPointAndOriLine p) {
		return new MergingKey(p.angle, p.intercept, p.isVertical() ? p.getY() : p.getX());
	}

	private HashMap<MyPointAndOriLine, MergingKey> createKeyTable(final Collection<MyPointAndOriLine> points,
			final double eps) {
		var sorted = new TreeSet<MergingKey>();
		points.forEach(p -> sorted.add(toMergingKey(p)));

		var keys = new HashMap<MyPointAndOriLine, MergingKey>();

		for (var p : points) {
			var key = toMergingKey(p);

			var coord = p.isVertical() ? p.getY() : p.getX();

			var range = CollectionUtil.rangeSetInclusive(sorted,
					new MergingKey(key.angle - MathUtil.angleRadianEps(), key.intercept - eps, coord - eps),
					new MergingKey(key.angle + MathUtil.angleRadianEps(), key.intercept + eps, coord + eps));

			var filtered = range.stream()
					.filter(k -> MathUtil.areEqual(k.intercept, key.intercept, eps));
			keys.put(p, filtered.findFirst().get());
		}

		return keys;
	}

	public Collection<OriLine> mergeIgnoringType(final Collection<OriLine> inputLines, final double eps) {
		// sweep line along x
		// for a pair of angle and intercept, only one mergeable line appears
		// when the sweep encounter a point.

		var result = new HashSet<OriLine>();

		var points = new ArrayList<MyPointAndOriLine>();
		for (var line : inputLines) {
			var canonical = line.createCanonical();
			points.addAll(MyPointAndOriLine.create(canonical));
		}

		var keyTable = createKeyTable(points, eps);

		var events = new PriorityQueue<MyPointAndOriLine>(points);

		var mergings = new TreeMap<MergingKey, MyPointAndOriLine>();

		var p = events.peek();
		var key = keyTable.get(p);
		mergings.put(key, p);

		var eventPoint = p;
		while (!events.isEmpty()) {
			eventPoint = events.poll();
			key = keyTable.get(eventPoint);
			var merging = mergings.get(key);

			logger.trace("event : {}, remain: {}", eventPoint, events);

			if (!eventPoint.isLeft) {
				var merged = mergings.remove(key);
				if (merged != null) {
					result.add(merged.getLine());
				}
				continue;
			}

			logger.trace("merging key:{} merging value: {}", key, merging);

			if (merging == null) {
				mergings.put(key, eventPoint);
				merging = eventPoint;
			}

			var mergingLine = merging.getLine();
			var eventLine = eventPoint.getLine();
			var overlapType = GeomUtil.distinguishSegmentsOverlap(mergingLine, eventLine, eps);
			switch (overlapType) {
			case 2, 3, 4:
				// can be merged

				// remove points to be old.
				events.remove(eventPoint.opposite);
				keyTable.remove(eventPoint.opposite);
				events.remove(merging);

				logger.trace("after remove, remain: {}", events);

				// merge
				OriPoint right;

				if (merging.isVertical()) {
					right = mergingLine.getOriPoint1().getY() > eventLine.getOriPoint1().getY()
							? mergingLine.getOriPoint1()
							: eventLine.getOriPoint1();
				} else {
					right = mergingLine.getOriPoint1().getX() > eventLine.getOriPoint1().getX()
							? mergingLine.getOriPoint1()
							: eventLine.getOriPoint1();
				}

				var merged = MyPointAndOriLine.create(
						new OriLine(mergingLine.getOriPoint0(), right, mergingLine.getType()));

				events.add(merged.get(1));
				keyTable.put(merged.get(1), key);
				mergings.put(key, merged.get(1));

				logger.trace("after add, remain: {}", events);
				break;
			case 0, 1:
				// not overlap
				break;
			}

		}

		return result;
	}
}
