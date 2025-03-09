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
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.geom.GeomUtil;
import oripa.util.MathUtil;
import oripa.util.collection.CollectionUtil;
import oripa.value.OriLine;
import oripa.value.OriLine.Type;
import oripa.value.OriPoint;
import oripa.vecmath.Vector2d;

/**
 * Splits all given lines at the cross points.
 *
 * @author OUCHI Koji
 *
 */
public class CrossingLineSplitter {
	private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private static class CrossPointAndOriLine implements Comparable<CrossPointAndOriLine> {
		private final OriPoint point;
		private final OriLine line;
		private final boolean isLeft;
		private final double angle;

		private CrossPointAndOriLine opposite;

		public static List<CrossPointAndOriLine> createWithCanonicalization(final OriLine line) {
			var canonical = new OriLine(line.getP0(), line.getP1(), Type.MOUNTAIN).createCanonical();
			return create(canonical);
		}

		public static List<CrossPointAndOriLine> create(final OriLine line) {
			var left = asLeft(line.getOriPoint0(), line);
			var right = asRight(line.getOriPoint1(), line);

			left.opposite = right;
			right.opposite = left;

			return List.of(left, right);
		}

		public static CrossPointAndOriLine asLeft(final OriPoint point, final OriLine line) {
			return new CrossPointAndOriLine(point, line, true);
		}

		public static CrossPointAndOriLine asRight(final OriPoint point, final OriLine line) {
			return new CrossPointAndOriLine(point, line, false);
		}

		private CrossPointAndOriLine(final OriPoint point, final OriLine line,
				final boolean isLeft) {
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
			this.angle = angle;

			// a line with angle PI is the same as one with angle 0.
			if (Math.PI - angle < MathUtil.angleRadianEps()) {
				angle = 0;
			}

		}

		public boolean isVertical() {
			return MathUtil.areRadianEqual(Math.PI / 2, angle);
		}

		/**
		 * @return point
		 */
		public OriPoint getPoint() {
			return point;
		}

		/**
		 * @return line
		 */
		public OriLine getLine() {
			return line;
		}

		public boolean isLeft() {
			return isLeft;
		}

		public boolean isRight() {
			return !isLeft;
		}

		public CrossPointAndOriLine getLeft() {
			return isLeft() ? this : opposite;
		}

		public CrossPointAndOriLine getRight() {
			return isRight() ? this : opposite;
		}

		public double getX() {
			return point.getX();
		}

		public double getY() {
			return point.getY();
		}

		@Override
		public int compareTo(final CrossPointAndOriLine o) {
			var comp = point.compareTo(o.point);

			if (comp == 0) {
				comp = opposite.point.compareTo(o.opposite.point);
			}

			if (comp == 0) {
				if (isLeft && o.isRight()) {
					return -1;
				} else if (isRight() && o.isLeft) {
					return 1;
				}
				return 0;
			}

			return comp;
		}

		@Override
		public int hashCode() {
			return Objects.hash(point, opposite.point, isLeft);
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

			if (obj instanceof CrossPointAndOriLine other) {
				return this.compareTo(other) == 0;
			}
			return true;
		}

		@Override
		public String toString() {
			return (isLeft ? "left " : "right ") + line.toString();
		}
	}

	private TreeSet<OriPoint> createFoundPoints(
			final Collection<CrossPointAndOriLine> points) {
		var foundPoints = new TreeSet<OriPoint>();

		for (var point : points) {
			add(point, foundPoints);
		}

		return foundPoints;
	}

	private void add(
			final CrossPointAndOriLine point,
			final TreeSet<OriPoint> foundPoints) {

		foundPoints.add(point.getPoint());
	}

	private OriPoint get(
			final OriPoint point,
			final TreeSet<OriPoint> foundPoints, final double eps) {

		var range = CollectionUtil.rangeSetInclusive(foundPoints,
				new OriPoint(point.getX() - eps, point.getY() - eps),
				new OriPoint(point.getX() + eps, point.getY() + eps));

		var filtered = range.stream().filter(p -> point.equals(p, eps));

		return filtered.findFirst().get();
	}

	/**
	 * sweep line algorithm. basic idea is inspired with Bentley–Ottmann
	 * algorithm.
	 *
	 * @param inputLines
	 * @param eps
	 * @return
	 */

	public Collection<OriLine> splitIgnoringType(
			final Collection<OriLine> inputLines, final double eps) {
		var splits = new HashSet<OriLine>();

		var points = new ArrayList<CrossPointAndOriLine>();
		for (var line : inputLines) {
			points.addAll(CrossPointAndOriLine.createWithCanonicalization(line));
		}
		var foundPoints = createFoundPoints(points);

		var onSweepLine = new TreeMap<Double, TreeSet<CrossPointAndOriLine>>();

		var events = new PriorityQueue<CrossPointAndOriLine>(points);

		var count = 0;

		while (!events.isEmpty() && count++ <= 2 * inputLines.size() * inputLines.size()) {
			var event = events.poll();

			logger.debug("event : {}, remain: {}", event, events);

			if (event.isLeft()) {
				var neighbors = getLowerAndHigher(onSweepLine, event);
				var lower = neighbors[0];
				var higher = neighbors[1];
				if (higher != null && lower != null && higher.getLine() == lower.getLine()) {
					// use left only for the same line
					higher = null;
				}

				logger.debug("higher : {}", higher);
				addLeftFutureEvents(events, onSweepLine, event, higher, foundPoints, eps);

				logger.debug("lower : {}", lower);
				addLeftFutureEvents(events, onSweepLine, event, lower, foundPoints, eps);

			}
			// is right: collect split line
			else {

				var neighbors = getLowerAndHigher(onSweepLine, event);
				var lower = neighbors[0];
				var higher = neighbors[1];

				if (higher != null || lower != null) {
					// to replace with new left
					remove(event.getLeft(), onSweepLine);
				}

				logger.debug("higher : {}", higher);
				var splitLeft = addRightFutureEvents(events, onSweepLine, event, higher, foundPoints, eps);
				if (splitLeft != null && splitLeft.getLine().length() > eps) {
					splits.add(splitLeft.getLine());
				}

				logger.debug("lower : {}", lower);
				splitLeft = addRightFutureEvents(events, onSweepLine, event, lower, foundPoints, eps);
				if (splitLeft != null && splitLeft.getLine().length() > eps) {
					splits.add(splitLeft.getLine());
				}

				if (event.getLine().length() > eps) {
					splits.add(event.getLine());
					logger.debug("split : {}", splits);
				}

				remove(event, onSweepLine);
				remove(event.opposite, onSweepLine);
			}

			logger.debug("on sweep line {}", onSweepLine);

		}

		return splits;
	}

	private void addLeftFutureEvents(
			final PriorityQueue<CrossPointAndOriLine> events,
			final TreeMap<Double, TreeSet<CrossPointAndOriLine>> onSweepLine,
			final CrossPointAndOriLine event,
			final CrossPointAndOriLine onSweep,
			final TreeSet<OriPoint> foundPoints,
			final double eps) {
		if (onSweep == null) {
			return;
		}

		var eventLine = event.getLine();
		var lineOnSweep = onSweep.getLine();

		Vector2d touchPoint = null;
		if (GeomUtil.distancePointToSegment(event.getPoint(), lineOnSweep) < eps) {
			touchPoint = event.getPoint();
		}

		var crossPointOpt = GeomUtil.getCrossPoint(eventLine, lineOnSweep);

		if (crossPointOpt.isEmpty() && touchPoint == null) {
			return;
		}

		var crossPoint = new OriPoint(touchPoint == null ? crossPointOpt.orElse(null) : touchPoint);

		// left of swept line is crossing then we don't have to split.
		if (lineOnSweep.getP0().equals(crossPoint, eps)) {
			add(event, onSweepLine);
			return;
		}

		foundPoints.add(crossPoint);

		addLeftFutureEvents(events, onSweepLine, get(crossPoint, foundPoints, eps), event, eps);
		addLeftFutureEvents(events, onSweepLine, get(crossPoint, foundPoints, eps), onSweep, eps);

		// event is popped from events and checked that it is left so the
		// remain is right only.
		events.remove(event.opposite);

		// swept point has been removed from events so we need to remove
		// right only.
		events.remove(onSweep.isLeft() ? onSweep.opposite : onSweep);
	}

	private void addLeftFutureEvents(
			final PriorityQueue<CrossPointAndOriLine> events,
			final TreeMap<Double, TreeSet<CrossPointAndOriLine>> onSweepLine,
			final Vector2d crossPoint,
			final CrossPointAndOriLine p,
			final double eps) {

		var pLeft = p.getLeft();
		var pRight = p.getRight();

		// left side of split
		var splitLeft = create(pLeft.getPoint(), crossPoint);

		// the new event is right
		var crossRight = splitLeft.get(1);

		if (crossRight.getLine().length() > eps) {
			events.add(crossRight);
			add(crossRight, onSweepLine);
		}

		// right side of split
		var splitRight = create(crossPoint, pRight.getPoint());

		var crossLeft = splitRight.get(0);
		crossRight = splitRight.get(1);

		if (crossRight.getLine().length() > eps) {
			// the new event is left and right
			events.add(crossLeft);
			add(crossLeft, onSweepLine);

			events.add(crossRight);
			add(crossRight, onSweepLine);
		}
		// to replace with new points
		remove(pLeft, onSweepLine);
		remove(pRight, onSweepLine);

	}

	private CrossPointAndOriLine addRightFutureEvents(
			final PriorityQueue<CrossPointAndOriLine> events,
			final TreeMap<Double, TreeSet<CrossPointAndOriLine>> onSweepLine,
			final CrossPointAndOriLine event,
			final CrossPointAndOriLine onSweep,
			final TreeSet<OriPoint> foundPoints,
			final double eps) {
		if (onSweep == null) {
			return null;
		}

		var eventLine = event.getLine();
		var lineOnSweep = onSweep.getLine();

		Vector2d touchPoint = null;
		if (GeomUtil.distancePointToSegment(event.getPoint(), lineOnSweep) < eps) {
			touchPoint = event.getPoint();
		}

		var crossPointOpt = GeomUtil.getCrossPoint(eventLine, lineOnSweep);

		if (crossPointOpt.isEmpty() && touchPoint == null) {
			return null;
		}

		var crossPoint = new OriPoint(touchPoint == null ? crossPointOpt.orElse(null) : touchPoint);

		if (lineOnSweep.getP0().equals(crossPoint, eps)) {
			return null;
		}

		foundPoints.add(crossPoint);

		var splitLeft = addRightFutureEvents(events, onSweepLine, crossPoint, onSweep, eps);

		// event is popped from events and checked that it is right side so
		// there is no remain.
		// events.remove(event.opposite);

		// swept point has been removed from events so we need to remove
		// right only.
		events.remove(onSweep.isLeft() ? onSweep.opposite : onSweep);

		return splitLeft;
	}

	private CrossPointAndOriLine addRightFutureEvents(
			final PriorityQueue<CrossPointAndOriLine> events,
			final TreeMap<Double, TreeSet<CrossPointAndOriLine>> onSweepLine,
			final OriPoint crossPoint,
			final CrossPointAndOriLine p,
			final double eps) {

		if (p.isLeft()) {
			remove(p, onSweepLine);
			return create(p.getPoint(), crossPoint).get(1);
		}

		// right side of split
		var splitRight = create(crossPoint, p.opposite.getPoint());

		var crossLeft = splitRight.get(0);
		var crossRight = splitRight.get(1);

		if (crossRight.getLine().length() > eps) {
			// the new event is left and right
			// events.add(crossLeft);
			add(crossLeft, onSweepLine);

			events.add(crossRight);
			add(crossRight, onSweepLine);
		}
		// to replace with new Right
		remove(p, onSweepLine);

		return null;
	}

	private CrossPointAndOriLine[] getLowerAndHigher(
			final TreeMap<Double, TreeSet<CrossPointAndOriLine>> onSweepLine,
			final CrossPointAndOriLine event) {
		var highers = onSweepLine.higherEntry(event.getY());
		var lowers = onSweepLine.lowerEntry(event.getY());
		var higher = highers == null ? null : highers.getValue().iterator().next();
		var lower = lowers == null ? null : lowers.getValue().iterator().next();

		if (higher == null) {
			var sames = onSweepLine.get(event.getY());
			if (sames != null && !sames.isEmpty()) {
				logger.debug("same y {}", sames);
				higher = sames.higher(event);
			}
		}
		if (lower == null) {
			var sames = onSweepLine.get(event.getY());
			if (sames != null && !sames.isEmpty()) {
				logger.debug("same y {}", sames);
				lower = sames.lower(event);
			}
		}

		if (higher == null && lower == null) {
			if (onSweepLine.isEmpty()) {
				add(event, onSweepLine);
			} else {
				var sames = onSweepLine.get(event.getY()).stream().filter(s -> !s.equals(event)).toList();
				if (sames.isEmpty()) {
					add(event, onSweepLine);
				} else {
					lower = sames.getFirst();
				}
			}
		}

		return new CrossPointAndOriLine[] { lower, higher };
	}

	private void add(final CrossPointAndOriLine p,
			final TreeMap<Double, TreeSet<CrossPointAndOriLine>> onSweepLine) {
		onSweepLine.putIfAbsent(p.getY(), new TreeSet<>());
		onSweepLine.get(p.getY()).add(p);
	}

	private void remove(final CrossPointAndOriLine p,
			final TreeMap<Double, TreeSet<CrossPointAndOriLine>> onSweepLine) {
		var sames = onSweepLine.get(p.getY());
		if (sames != null) {
			sames.remove(p);
			if (sames.isEmpty()) {
				onSweepLine.remove(p.getY());
			}
		}
	}

	private List<CrossPointAndOriLine> create(final Vector2d left, final Vector2d cross) {
		return CrossPointAndOriLine.create(new OriLine(left, cross, Type.MOUNTAIN));
	}
}
