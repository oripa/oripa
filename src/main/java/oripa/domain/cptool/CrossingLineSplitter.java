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

		private CrossPointAndOriLine opposite;

		public static List<CrossPointAndOriLine> createForInitialization(final OriLine line) {
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
		}

		public boolean isVertical() {
			return line.isVertical();
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

		public static Comparator<CrossPointAndOriLine> getTieBreakComparator() {
			return (a, b) -> {
				// only far right can make cross.
				var comp = switch (a.getRight().point.compareTo(b.getRight().point)) {
				case -1 -> 1;
				case 1 -> -1;
				default -> 0;
				};

				if (comp == 0) {
					comp = a.getLeft().point.compareTo(b.getLeft().point);
				}

				if (comp == 0) {
					if (a.isLeft && b.isRight()) {
						return -1;
					} else if (a.isRight() && b.isLeft) {
						return 1;
					}
					return 0;
				}

				return comp;
			};
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

	private OriPoint getAndAdd(
			final OriPoint point,
			final TreeSet<OriPoint> foundPoints, final double eps) {

		var range = CollectionUtil.rangeSetInclusive(foundPoints,
				new OriPoint(point.getX() - eps, point.getY() - eps),
				new OriPoint(point.getX() + eps, point.getY() + eps));

		var filtered = range.stream().filter(p -> point.equals(p, eps));

		var firstOpt = filtered.findFirst();
		if (firstOpt.isEmpty()) {
			foundPoints.add(point);
			return point;
		}

		return firstOpt.get();
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
		var foundPoints = createFoundPoints(points);

		for (var line : inputLines) {
			var fixedLine = new OriLine(getAndAdd(line.getOriPoint0(), foundPoints, eps),
					getAndAdd(line.getOriPoint1(), foundPoints, eps),
					Type.MOUNTAIN);
			points.addAll(CrossPointAndOriLine.createForInitialization(fixedLine));
		}

		var onSweepLine = new TreeMap<Double, TreeSet<CrossPointAndOriLine>>();

		var events = new PriorityQueue<CrossPointAndOriLine>(points);

		var count = 0;

		while (!events.isEmpty() && count++ <= 3 * inputLines.size() * inputLines.size()) {
			var event = events.poll();

			logger.debug("event : {}, remain: {}", event, events);

			var lowerOpt = getLower(event, onSweepLine, eps);
			var higherOpt = getHigher(event, onSweepLine, eps);

			logger.debug("lower:{}", lowerOpt);
			logger.debug("higher:{}", higherOpt);

			if (event.isLeft()) {

				if (lowerOpt.isEmpty() && higherOpt.isEmpty()) {
					if (onSweepLine.isEmpty()) {
						add(event, onSweepLine);
						add(event.opposite, onSweepLine);
					}
				} else {

					var lower = lowerOpt.orElse(null);
					var split = addLeftFutureEvents(
							events, onSweepLine, event, lower, foundPoints, eps);
					if (!split) {
						var higher = higherOpt.orElse(null);
						split = addLeftFutureEvents(
								events, onSweepLine, event, higher, foundPoints, eps);
					}

					if (!split) {
						add(event, onSweepLine);
					}

				}
			}
			// is right: collect split line
			else {

				splits.remove(event.getLine());

				if (lowerOpt.isEmpty() && higherOpt.isEmpty()) {
					splits.add(event.getLine());
					logger.debug("split : {}", splits);
					if (onSweepLine.isEmpty()) {
						add(event, onSweepLine);
					}
				} else {
					var split = addRightFutureEvents(events, onSweepLine, event, lowerOpt.orElse(null), foundPoints,
							eps);

					if (split) {
						var lower = lowerOpt.orElse(null);
						events.remove(lower);
						events.remove(lower.opposite);
					}
					if (!split) {
						split = addRightFutureEvents(events, onSweepLine, event, higherOpt.orElse(null), foundPoints,
								eps);
						if (split) {
							var higher = higherOpt.orElse(null);
							events.remove(higher);
							events.remove(higher.opposite);
						}
					}

					if (!split && event.getLine().length() > eps) {
						splits.add(event.getLine());
						logger.debug("split : {}", splits);
					}

					remove(event, onSweepLine);
					remove(event.opposite, onSweepLine);
				}
			}

			logger.debug("on sweep line {}", onSweepLine);

		}

		logger.info("loop count: {}", count);

		return splits;

	}

	private boolean addLeftFutureEvents(
			final PriorityQueue<CrossPointAndOriLine> events,
			final TreeMap<Double, TreeSet<CrossPointAndOriLine>> onSweepLine,
			final CrossPointAndOriLine event,
			final CrossPointAndOriLine onSweep,
			final TreeSet<OriPoint> foundPoints,
			final double eps) {
		if (onSweep == null) {
			return false;
		}

		var eventLine = event.getLine();
		var lineOnSweep = onSweep.getLine();

		var eventLeft = event.getLeft();
		var eventRight = event.getRight();
		var onSweepLeft = onSweep.getLeft();
		var onSweepRight = onSweep.getRight();

		Vector2d touchPoint = null;
		if (GeomUtil.distancePointToSegment(event.getPoint(), lineOnSweep) < eps) {
			touchPoint = event.getPoint();
		}

		var crossPointOpt = GeomUtil.getCrossPoint(eventLine, lineOnSweep);

		if (crossPointOpt.isEmpty() && touchPoint == null) {
			return false;
		}

		var crossPoint = getAndAdd(
				new OriPoint(touchPoint == null ? crossPointOpt.orElse(null) : touchPoint),
				foundPoints,
				eps);

		if (crossPoint == null) {
			return false;
		}

		boolean splitDone = false;
		// if left of swept line is crossing then
		// we don't have to split the line.
		if (onSweepLeft.getPoint().equals(crossPoint, eps)) {
			add(onSweepLeft, onSweepLine);
		} else {
			addLeftFutureEvents(events, onSweepLine, crossPoint, onSweep, foundPoints, eps);
			splitDone = true;
		}

		if (eventLeft.getPoint().equals(crossPoint, eps)) {
			add(eventLeft, onSweepLine);
		} else {
			addLeftFutureEvents(events, onSweepLine, crossPoint, event, foundPoints, eps);
			splitDone = true;
		}

		return splitDone;
	}

	private void addLeftFutureEvents(
			final PriorityQueue<CrossPointAndOriLine> events,
			final TreeMap<Double, TreeSet<CrossPointAndOriLine>> onSweepLine,
			final Vector2d crossPoint,
			final CrossPointAndOriLine p,
			final TreeSet<OriPoint> foundPoints,
			final double eps) {

		var pLeft = p.getLeft();
		var pRight = p.getRight();

		// left side of split
		var splitLeft = create(getAndAdd(pLeft.getPoint(), foundPoints, eps), crossPoint);

		// the new event is right
		var crossLeft = splitLeft.get(0);
		var crossRight = splitLeft.get(1);

		if (crossRight.getLine().length() > eps) {
			// to replace with new points
			remove(pLeft, onSweepLine);
			events.remove(pLeft);

			events.add(crossRight);
			add(crossLeft, onSweepLine);
			add(crossRight, onSweepLine);
			logger.debug("split at {}, {}", crossPoint, crossRight.getLine());
		}

		// right side of split
		var splitRight = create(crossPoint, getAndAdd(pRight.getPoint(), foundPoints, eps));

		crossLeft = splitRight.get(0);
		crossRight = splitRight.get(1);

		if (crossRight.getLine().length() > eps) {
			// to replace with new points
			remove(pRight, onSweepLine);
			events.remove(pRight);

			// the new event is left and right
			events.add(crossLeft);
			add(crossLeft, onSweepLine);

			events.add(crossRight);
			add(crossRight, onSweepLine);

			logger.debug("split at {}, {}", crossPoint, crossRight.getLine());
		}

	}

	private boolean addRightFutureEvents(
			final PriorityQueue<CrossPointAndOriLine> events,
			final TreeMap<Double, TreeSet<CrossPointAndOriLine>> onSweepLine,
			final CrossPointAndOriLine event,
			final CrossPointAndOriLine onSweep,
			final TreeSet<OriPoint> foundPoints,
			final double eps) {
		if (onSweep == null) {
			return false;
		}
		var onSweepLeft = onSweep.getLeft();
		var onSweepRight = onSweep.getRight();

		var eventLine = event.getLine();
		var lineOnSweep = onSweep.getLine();

		Vector2d touchPoint = null;
		if (GeomUtil.distancePointToSegment(event.getPoint(), lineOnSweep) < eps) {
			touchPoint = event.getPoint();
		}

		var crossPointOpt = GeomUtil.getCrossPoint(eventLine, lineOnSweep);

		if (crossPointOpt.isEmpty() && touchPoint == null) {
			return false;
		}

		var crossPoint = getAndAdd(
				new OriPoint(touchPoint == null ? crossPointOpt.orElse(null) : touchPoint),
				foundPoints,
				eps);

		if (crossPoint == null) {
			return false;
		}

		if (onSweepLeft.getPoint().equals(crossPoint, eps)) {
			add(onSweepRight, onSweepLine);
			return false;
		}
		if (onSweepRight.getPoint().equals(crossPoint, eps)) {
			add(onSweepRight, onSweepLine);
			return false;
		}

		addRightFutureEvents(events, onSweepLine, crossPoint, event, foundPoints, eps);
		addRightFutureEvents(events, onSweepLine, crossPoint, onSweep, foundPoints, eps);

		// event is popped from events and checked that it is right side so
		// there is no remain.
		// events.remove(event.opposite);

		// swept point has been removed from events so we need to remove
		// right only.
		// events.remove(onSweepRight);

		return true;
	}

	private void addRightFutureEvents(
			final PriorityQueue<CrossPointAndOriLine> events,
			final TreeMap<Double, TreeSet<CrossPointAndOriLine>> onSweepLine,
			final OriPoint crossPoint,
			final CrossPointAndOriLine p,
			final TreeSet<OriPoint> foundPoints,
			final double eps) {

		var pLeft = p.getLeft();
		var pRight = p.getRight();

		// left side of split
		var splitLeft = create(getAndAdd(pLeft.getPoint(), foundPoints, eps), crossPoint);

		// the new event is right
		var crossLeft = splitLeft.get(0);
		var crossRight = splitLeft.get(1);

		if (crossRight.getLine().length() > eps) {
			// to replace with new right
			remove(pLeft, onSweepLine);

			events.add(crossRight);
			add(crossLeft, onSweepLine);
			add(crossRight, onSweepLine);
			logger.debug("split at {}, {}", crossPoint, crossRight.getLine());
		}

		// right side of split
		var splitRight = create(crossPoint, getAndAdd(pRight.getPoint(), foundPoints, eps));

		crossLeft = splitRight.get(0);
		crossRight = splitRight.get(1);

		if (crossRight.getLine().length() > eps) {
			// to replace with new right
			remove(pRight, onSweepLine);

			// the new event is left and right
			events.add(crossLeft);
			add(crossLeft, onSweepLine);

			events.add(crossRight);
			add(crossRight, onSweepLine);

			logger.debug("split at {},{}", crossPoint, crossRight.getLine());
		}
	}

	private Optional<CrossPointAndOriLine> getLower(
			final CrossPointAndOriLine event,
			final TreeMap<Double, TreeSet<CrossPointAndOriLine>> onSweepLine,
			final double eps) {

		var lowers = onSweepLine.lowerEntry(event.getY());
		Optional<CrossPointAndOriLine> lowerOpt = lowers == null ? Optional.empty()
				: Optional.ofNullable(lowers.getValue().first());

		if (event.isVertical()) {
			var lowerEntries = onSweepLine.headMap(event.getY());

			var found = false;
			for (var points : lowerEntries.values()) {
				for (var p : points) {
					lowerOpt = GeomUtil.getCrossPoint(
							event.getLine(),
							p.getLine()).isPresent() ? Optional.of(p) : Optional.empty();
					if (lowerOpt.isPresent()) {
						found = true;
						break;
					}
				}
				if (found) {
					break;
				}
			}
		}

		logger.debug("lowers:{}", lowers);

		if (lowerOpt.isPresent()) {
			return lowerOpt;
		}

		var sames = onSweepLine.get(event.getY());

		if (sames != null && !sames.isEmpty()) {
			logger.debug("same y {}", sames);
			return Optional.ofNullable(sames.lower(event));
		}

		return Optional.empty();
	}

	private Optional<CrossPointAndOriLine> getHigher(
			final CrossPointAndOriLine event,
			final TreeMap<Double, TreeSet<CrossPointAndOriLine>> onSweepLine,
			final double eps) {

		var highers = onSweepLine.higherEntry(event.getY());
		Optional<CrossPointAndOriLine> higherOpt = highers == null ? Optional.empty()
				: Optional.ofNullable(highers.getValue().first());

		if (event.isVertical()) {
			var higherEtries = onSweepLine.tailMap(event.getY());

			var found = false;
			for (var points : higherEtries.values()) {
				for (var p : points) {
					higherOpt = GeomUtil.getCrossPoint(
							event.getLine(),
							p.getLine()).isPresent() ? Optional.of(p) : Optional.empty();
					if (higherOpt.isPresent()) {
						found = true;
						break;
					}
				}
				if (found) {
					break;
				}
			}
		}

		logger.debug("highers:{}", highers);

		if (higherOpt.isPresent()) {
			return higherOpt;
		}

		var sames = onSweepLine.get(event.getY());

		if (sames != null && !sames.isEmpty()) {
			logger.debug("same y {}", sames);
			return Optional.ofNullable(sames.higher(event));
		}

		return Optional.empty();
	}

	private void add(final CrossPointAndOriLine p,
			final TreeMap<Double, TreeSet<CrossPointAndOriLine>> onSweepLine) {
		onSweepLine.putIfAbsent(p.getY(), new TreeSet<>(CrossPointAndOriLine.getTieBreakComparator()));
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

	private List<CrossPointAndOriLine> create(
			final Vector2d left, final Vector2d right) {
		return CrossPointAndOriLine.create(new OriLine(left, right, Type.MOUNTAIN));
	}
}
