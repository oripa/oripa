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
import java.util.function.BiConsumer;
import java.util.function.Function;

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

	private static class EventPoint implements Comparable<EventPoint> {
		private final OriPoint point;
		private final TreeSet<OriLine> lines = new TreeSet<>();

		private EventPoint opposite;

		public static List<EventPoint> createForInitialization(final OriLine line) {

			if (line.isVertical()) {
				var p0 = line.getP0();
				var p1 = line.getP1();
				var lower = p0.getY() < p1.getY() ? p0 : p1;
				var higher = p0.getY() >= p1.getY() ? p0 : p1;

				return create(new OriLine(lower, higher, Type.MOUNTAIN));
			}

			var canonical = new OriLine(line.getP0(), line.getP1(), Type.MOUNTAIN).createCanonical();
			return create(canonical);
		}

		public static List<EventPoint> create(final OriLine line) {
			var left = new EventPoint(line.getOriPoint0(), line);
			var right = new EventPoint(line.getOriPoint1(), line);

			left.opposite = right;
			right.opposite = left;

			return List.of(left, right);
		}

		private EventPoint(final OriPoint point, final OriLine line) {
			this.point = point;
			lines.add(line);
		}

//		public boolean isVertical() {
//			return line.isVertical();
//		}

		/**
		 * @return point
		 */
		public OriPoint getPoint() {
			return point;
		}

		/**
		 * @return line
		 */
		public TreeSet<OriLine> getLines() {
			return lines;
		}

		public void addLine(final OriLine line) {
			lines.add(line);
		}

		public double getX() {
			return point.getX();
		}

		public double getY() {
			return point.getY();
		}

		@Override
		public int compareTo(final EventPoint o) {
			var comp = point.compareTo(o.point);

//			if (comp == 0) {
//				comp = opposite.point.compareTo(o.opposite.point);
//			}

//			if (comp == 0) {
//				comp = line.compareTo(o.line);
//			}

			return comp;
		}

		public static Comparator<EventPoint> getTieBreakComparator() {
			return (a, b) -> {
				var comp = Double.compare(a.point.getY(), b.point.getY());
				if (comp == 0) {
					comp = Double.compare(a.point.getX(), b.point.getX());
				}
				return comp;
			};
		}

		@Override
		public int hashCode() {
			return Objects.hash(point);
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

			if (obj instanceof EventPoint other) {
				return this.compareTo(other) == 0;
			}
			return true;
		}

		@Override
		public String toString() {
			return point + " " + lines.toString();
		}
	}

	private record StatusElementSegment(Double yAtSweep, OriLine line) implements Comparable<StatusElementSegment> {

		@Override
		public int compareTo(final StatusElementSegment o) {
			if (line.isVertical() && !o.line.isVertical()) {
				return 1;
			}
			if (!line.isVertical() && o.line.isVertical()) {
				return -1;
			}

			var comp = Double.compare(yAtSweep, o.yAtSweep);

			if (comp == 0) {
				var x = line.getAffineXValueAt(yAtSweep);
				comp = Double.compare(line.getAffineYValueAt(x + 1e-8), o.line.getAffineYValueAt(x + 1e-8));
			}

			return comp;
		}

		@Override
		public final boolean equals(final Object arg0) {
			if (arg0 instanceof StatusElementSegment s) {
				return compareTo(s) == 0;
			}
			return false;
		}

		@Override
		public final int hashCode() {
			return Objects.hash(yAtSweep, line);
		}
	}

	private TreeSet<OriPoint> createFoundPoints(
			final Collection<EventPoint> points, final double eps) {
		var foundPoints = new TreeSet<OriPoint>();

		for (var point : points) {
			getAndAdd(point.getPoint(), foundPoints, eps);
		}

		return foundPoints;
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
	 * sweep line algorithm from de Berg, Mark; van Kreveld, Marc; Overmars,
	 * Mark; Schwarzkopf, Otfried, "Chapter 2: Line segment intersection",
	 * Computational Geometry (3rd ed.).
	 *
	 * @param inputLines
	 * @param eps
	 * @return
	 */
	public Collection<OriLine> splitIgnoringType(
			final Collection<OriLine> inputLines, final double eps) {

		var points = new ArrayList<EventPoint>();
		var foundPoints = createFoundPoints(points, eps);

		var fixedLines = new ArrayList<OriLine>();
		for (var line : inputLines) {
			var fixedLine = new OriLine(getAndAdd(line.getOriPoint0(), foundPoints, eps),
					getAndAdd(line.getOriPoint1(), foundPoints, eps),
					Type.MOUNTAIN);

			fixedLines.add(fixedLine);
			points.addAll(EventPoint.createForInitialization(fixedLine));
		}

		var sweepStatus = new TreeSet<StatusElementSegment>();

		var events = new TreeSet<EventPoint>(points);
		var allLefts = new TreeSet<OriLine>(
				points.stream()
						.flatMap(p -> p.getLines().stream()
								.filter(line -> p.getPoint().equals(line.getP0(), eps)))
						.toList());

		var count = 0;

		var leftMap = buildLeftMap(allLefts);

		var crossInfos = new HashMap<OriPoint, Collection<OriLine>>();

		BiConsumer<EventPoint, Collection<OriLine>> reportReceiver = (event, crossLines) -> {
			crossInfos.put(event.getPoint(), crossLines);
		};

		while (!events.isEmpty() && count++ <= 3 * inputLines.size() * inputLines.size()) {
			var event = events.removeFirst();

			logger.debug("event {}", event);

			handleEventPoint(event, events, sweepStatus, leftMap, foundPoints, reportReceiver, eps);

			logger.debug("sweep status x = {} {}", event.getX(), sweepStatus);
		}

		var splits = split(fixedLines, crossInfos, eps);
		logger.debug("splits {}", splits);

		return splits;
	}

	private class MutableSegment {
		public Vector2d p0, p1;

		public MutableSegment(final Vector2d p0, final Vector2d p1) {
			this.p0 = p0;
			this.p1 = p1;
		}

		@Override
		public boolean equals(final Object obj) {
			if (obj instanceof MutableSegment s) {
				return p0.equals(s.p0) && p1.equals(s.p1);
			}

			return false;
		}

		@Override
		public int hashCode() {
			return Objects.hash(p0, p1);
		}
	}

	private Collection<OriLine> split(final Collection<OriLine> originalLines,
			final Map<OriPoint, Collection<OriLine>> crossInfos,
			final double eps) {

		var segmentConvert = new HashMap<OriLine, MutableSegment>();

		originalLines.forEach(line -> segmentConvert.put(line, new MutableSegment(line.getP0(), line.getP1())));

		var mutableCrossInfos = new TreeMap<OriPoint, Collection<MutableSegment>>();

		crossInfos.forEach((crossPoint, lines) -> {
			mutableCrossInfos.put(crossPoint,
					lines.stream()
							.map(line -> segmentConvert.get(line))
							.toList());
		});

		var splits = new HashSet<OriLine>();

		BiConsumer<Vector2d, Vector2d> add = (p0, p1) -> {
			var line = new OriLine(p0, p1, Type.MOUNTAIN);
			if (line.length() > eps) {
				splits.add(line);
			}
		};

		mutableCrossInfos.forEach((crossPoint, segments) -> {
			for (var segment : segments) {
				add.accept(segment.p0, crossPoint);
				segment.p0 = crossPoint;
			}
		});
		mutableCrossInfos.forEach((crossPoint, segments) -> {
			for (var segment : segments) {
				add.accept(segment.p0, segment.p1);
			}
		});

		return splits;
	}

	private HashMap<OriPoint, Set<OriLine>> buildLeftMap(final Collection<OriLine> allLefts) {
		var map = new HashMap<OriPoint, Set<OriLine>>();

		allLefts.stream().forEach(left -> {
			map.putIfAbsent(left.getOriPoint0(), new HashSet<>());
			map.get(left.getOriPoint0()).add(left);
		});

		return map;
	}

	private void handleEventPoint(final EventPoint event,
			final TreeSet<EventPoint> events,
			final TreeSet<StatusElementSegment> sweepStatus,
			final HashMap<OriPoint, Set<OriLine>> leftMap,
			final TreeSet<OriPoint> foundPoints,
			final BiConsumer<EventPoint, Collection<OriLine>> reportReceiver,
			final double eps) {
		var eventPosition = event.getPoint();

		// left end points that correspond to event point
		var lefts = getLefts(event, leftMap);

		logger.debug("lefts {}", lefts);
		lefts.forEach(p -> addToSweepStatus(p, eventPosition, sweepStatus, eps));

		// right end points that correspond to event point
		var rights = getRights(event, sweepStatus, foundPoints, eps);
		logger.debug("rights {}", rights);

		// interior points that correspond to event point
		var interiors = getInteriors(event, sweepStatus, foundPoints, eps);
		logger.debug("interiors {}", interiors);

		var crossings = new HashSet<OriLine>();

		crossings.addAll(rights);
		crossings.addAll(lefts);
		crossings.addAll(interiors);

		if (crossings.size() > 1) {
			report(event, crossings, reportReceiver);
			/*
						lefts = getLefts(event, leftMap);
						rights = getRights(event, sweepStatus, foundPoints, eps);
						interiors = getInteriors(event, sweepStatus, foundPoints, eps);
			*/
		}

		lefts.forEach(p -> removeFromSweepStatus(p, eventPosition, sweepStatus, eps));
		interiors.forEach(p -> removeFromSweepStatus(p, eventPosition, sweepStatus, eps));

		rights.forEach(p -> addToSweepStatus(p, eventPosition, sweepStatus, eps));
		interiors.forEach(p -> addToSweepStatus(p, eventPosition, sweepStatus, eps));

		var leftsAndinteriors = computeLeftsAndInteriors(event, lefts, sweepStatus, foundPoints, eps);

		if (leftsAndinteriors.isEmpty()) {
			var lowerOpt = getLower(event, event.getLines().first(), sweepStatus, foundPoints, eps);
			var higherOpt = getHigher(event, event.getLines().last(), sweepStatus, foundPoints, eps);

			var lower = lowerOpt.orElse(null);
			var higher = higherOpt.orElse(null);

			var crossPoint = computeCrossPoint(lower, higher, foundPoints, eps);

			logger.debug("no lefts and interiors");
			findNewEvent(lower, higher, event, crossPoint, events, foundPoints, eps);
		} else {
			logger.debug("lower");
			logger.debug("leftsAndInterior {}", leftsAndinteriors);

			var localLowest = leftsAndinteriors.first().line;
			var lowerOpt = getLower(event, localLowest, sweepStatus, foundPoints, eps);

			var lower = lowerOpt.orElse(null);

			var crossPoint = computeCrossPoint(lower, localLowest, foundPoints, eps);
			findNewEvent(lower, localLowest, event, crossPoint, events, foundPoints, eps);

			var localHighest = leftsAndinteriors.last().line;

			logger.debug("higher");

			var higherOpt = getHigher(event, localHighest, sweepStatus, foundPoints, eps);
			var higher = higherOpt.orElse(null);

			crossPoint = computeCrossPoint(higher, localHighest, foundPoints, eps);
			findNewEvent(higher, localHighest, event, crossPoint, events, foundPoints, eps);

		}

	}

	private void report(final EventPoint event, final Collection<OriLine> crossings,
			final BiConsumer<EventPoint, Collection<OriLine>> reportReceiver) {
		logger.debug("report {} {}", event, crossings);
		reportReceiver.accept(event, crossings);
	}

	private OriPoint computeCrossPoint(final OriLine event, final OriLine onSweep,
			final TreeSet<OriPoint> foundPoints,
			final double eps) {

		if (event == null || onSweep == null) {
			return null;
		}

		var crossPointOpt = GeomUtil.getCrossPoint(event.getLine(), onSweep.getLine());

		if (crossPointOpt.isEmpty()) {
			return null;
		}

		var crossPoint = getAndAdd(
				new OriPoint(crossPointOpt.get()),
				foundPoints,
				eps);

		return crossPoint;
	}

	private Set<OriLine> getLefts(final EventPoint event, final HashMap<OriPoint, Set<OriLine>> leftMap) {
		var lefts = leftMap.get(event.getPoint());
		if (lefts == null) {
			lefts = Set.of();
		}

		return lefts;
	}

	private Set<OriLine> getRights(
			final EventPoint event,
			final TreeSet<StatusElementSegment> sweepStatus,
			final TreeSet<OriPoint> foundPoints,
			final double eps) {

		var rights = getAffinePoints(event, sweepStatus, foundPoints, eps).stream()
				.filter(line -> line.getP1().equals(event.getPoint(), eps))
				.toList();

		return new HashSet<>(rights);
	}

	private Set<OriLine> getInteriors(
			final EventPoint event,
			final TreeSet<StatusElementSegment> sweepStatus,
			final TreeSet<OriPoint> foundPoints,
			final double eps) {

		var interiors = getAffinePoints(event, sweepStatus, foundPoints, eps).stream()
		// should be interior
//				.filter(line -> !line.getP0().equals(event.getPoint(), eps))
//				.filter(line -> !line.getP1().equals(event.getPoint(), eps))
				.toList();

		return new HashSet<>(interiors);
	}

	private TreeSet<OriLine> getAffinePoints(final EventPoint event,
			final TreeSet<StatusElementSegment> sweepStatus,
			final TreeSet<OriPoint> foundPoints,
			final double eps) {
		var points = sweepStatus
				.stream()
				// event point should be on the line
				.filter(s -> GeomUtil.distancePointToSegment(event.getPoint(), s.line) < eps)
				.map(StatusElementSegment::line)
				.toList();

		return new TreeSet<>(points);

	}

	private EventPoint toEventPoint(final StatusElementSegment s,
			final EventPoint event,
			final TreeSet<OriPoint> foundPoints, final double eps) {
		return new EventPoint(
				s.line.isVertical()
						? getAndAdd(new OriPoint(s.line.getAffineXValueAt(event.getY()), event.getY()),
								foundPoints, eps)
						: getAndAdd(new OriPoint(event.getX(), s.line.getAffineYValueAt(event.getX())),
								foundPoints, eps),
				s.line);
	}

	private Optional<OriLine> getLower(
			final EventPoint event,
			final OriLine targetLine,
			final TreeSet<StatusElementSegment> sweepStatus,
			final TreeSet<OriPoint> foundPoints,
			final double eps) {

		var eventStatus = new StatusElementSegment(event.getY(), targetLine);
		var lower = sweepStatus.lower(eventStatus);
		Optional<StatusElementSegment> lowerOpt = Optional.ofNullable(lower);

//logger.debug("lowers:{}", lowers);

		return lowerOpt.map(s -> s.line);

	}

	private Optional<OriLine> getHigher(
			final EventPoint event,
			final OriLine target,
			final TreeSet<StatusElementSegment> sweepStatus,
			final TreeSet<OriPoint> foundPoints,
			final double eps) {

		var eventStatus = new StatusElementSegment(event.getY(), target);
		var higher = sweepStatus.higher(eventStatus);
		Optional<StatusElementSegment> higherOpt = Optional.ofNullable(higher);

//logger.debug("lowers:{}", lowers);

		return higherOpt.map(s -> s.line);
	}

	private TreeSet<StatusElementSegment> computeLeftsAndInteriors(final EventPoint event,
			final Collection<OriLine> lefts,
			final TreeSet<StatusElementSegment> sweepStatus,
			final TreeSet<OriPoint> foundPoints,
			final double eps) {

		var interiors = getInteriors(event, sweepStatus, foundPoints, eps);

		var leftsAndInteriors = new TreeSet<StatusElementSegment>();
		lefts.forEach(line -> leftsAndInteriors.add(new StatusElementSegment(event.getY(), line)));
		interiors.forEach(line -> leftsAndInteriors.add(new StatusElementSegment(event.getY(), line)));

		return leftsAndInteriors;
	}

	private void findNewEvent(final OriLine lower, final OriLine higher,
			final EventPoint event,
			final OriPoint crossPoint,
			final TreeSet<EventPoint> events,
			final TreeSet<OriPoint> foundPoints,
			final double eps) {

		logger.debug("find new events for {} {} {}", lower, higher, event);

		if (lower == null || higher == null || crossPoint == null) {
			return;
		}

		Function<OriLine, EventPoint> create = (line) -> {
			return new EventPoint(crossPoint, line);
		};

		if (crossPoint.getX() > event.getX()) {
			logger.debug("cross on right.");

			events.add(create.apply(lower));
			events.add(create.apply(higher));

			return;
		}

		if (MathUtil.areEqual(crossPoint.getX(), event.getX(), eps)) {
			if (crossPoint.getY() >= event.getY()) {
				logger.debug("same x, cross on higher.");
				events.add(create.apply(lower));
				events.add(create.apply(higher));
			}
		}
	}

	private void addToSweepStatus(final OriLine line,
			final OriPoint xy,
			final TreeSet<StatusElementSegment> sweepStatus,
			final double eps) {
		sweepStatus.add(new StatusElementSegment(xy.getY(), line));
	}

	private void removeFromSweepStatus(final OriLine line,
			final OriPoint xy,
			final TreeSet<StatusElementSegment> sweepStatus,
			final double eps) {
		sweepStatus.removeIf(s -> s.line.equals(line, eps));
	}

//	public Collection<OriLine> splitIgnoringType(
//			final Collection<OriLine> inputLines, final double eps) {
//		var splits = new HashMap<OriPoint, TreeMap<Double, OriLine>>();
//
//		var points = new ArrayList<CrossPointAndOriLine>();
//		var foundPoints = createFoundPoints(points, eps);
//
//		var reports = new HashMap<OriPoint, Collection<OriLine>>();
//
//		for (var line : inputLines) {
//			var fixedLine = new OriLine(getAndAdd(line.getOriPoint0(), foundPoints, eps),
//					getAndAdd(line.getOriPoint1(), foundPoints, eps),
//					Type.MOUNTAIN);
//			points.addAll(CrossPointAndOriLine.createForInitialization(fixedLine));
//		}
//
//		var onSweepLine = new TreeMap<Double, TreeSet<CrossPointAndOriLine>>();
//
//		var events = new TreeSet<CrossPointAndOriLine>(points);
//
//		var count = 0;
//
//		while (!events.isEmpty() && count++ <= 3 * inputLines.size() * inputLines.size()) {
//			var event = events.removeFirst();
//
//			logger.debug("event : {}, remain: {}", event, events);
//
//			var lowerOpt = getLower(event, onSweepLine, eps);
//			var higherOpt = getHigher(event, onSweepLine, eps);
//
//			var lower = lowerOpt.orElse(null);
//			var higher = higherOpt.orElse(null);
//
//			logger.debug("lowerOpt:{}", lowerOpt);
//			logger.debug("higherOpt:{}", higherOpt);
//
//			if (event.isLeft()) {
//
//				if (lower == null && higher == null) {
//					if (onSweepLine.isEmpty()) {
//						add(event, onSweepLine);
//						add(event.opposite, onSweepLine);
//					}
//				} else {
//					while (lower != null && lower.getLine().sharesEndPoint(event.getLine(), eps)) {
//						lower = getLower(lower, onSweepLine, eps).orElse(null);
//					}
//					while (higher != null && higher.getLine().sharesEndPoint(event.getLine(), eps)) {
//						higher = getHigher(higher, onSweepLine, eps).orElse(null);
//					}
//					logger.debug("lower:{}", lower);
//					logger.debug("higher:{}", higher);
//
//					var lowerSplitType = addLeftFutureEvents(
//							events, onSweepLine, event, lower, foundPoints, eps);
//
//					var higherSplitType = SplitType.NONE;
//					if (lowerSplitType == SplitType.NONE || lowerSplitType == SplitType.TOUCH) {
//						higherSplitType = addLeftFutureEvents(
//								events, onSweepLine, event, higher, foundPoints, eps);
//					}
//
//					if (lowerSplitType == SplitType.CROSS || higherSplitType == SplitType.CROSS) {
//					} else {
//						add(event, onSweepLine);
//					}
//
//				}
//			}
//			// is right: collect split line
//			else {
//				var eventLine = event.getLine();
//				splits.putIfAbsent(event.getPoint(), new TreeMap<>());
//				var splitMap = splits.get(event.getPoint());
//				var olds = CollectionUtil.rangeMapInclusive(splitMap,
//						eventLine.getAngle() - MathUtil.angleRadianEps(),
//						eventLine.getAngle() + MathUtil.angleRadianEps());
//
//				var oldEntryOpt = olds.entrySet().stream()
//						.filter(e -> e.getValue().length() >= eventLine.length() - eps)
//						.findAny();
//
//				splitMap.get(eventLine.getAngle());
//
//				if (oldEntryOpt.isPresent()) {
//					splitMap.remove(oldEntryOpt.get().getKey());
//				}
//				if (lower == null && higher == null) {
//					splitMap.put(eventLine.getAngle(), eventLine);
//					logger.debug("split : {}", splits);
//
//				} else {
//					while (lower != null && lower.getLine().sharesEndPoint(event.getLine(), eps)) {
//						lower = getLower(lower, onSweepLine, eps).orElse(null);
//					}
//					while (higher != null && higher.getLine().sharesEndPoint(event.getLine(), eps)) {
//						higher = getHigher(higher, onSweepLine, eps).orElse(null);
//					}
//					logger.debug("lower:{}", lower);
//					logger.debug("higher:{}", higher);
//
//					var lowerSplitType = addRightFutureEvents(events, onSweepLine, event, lower, foundPoints,
//							eps);
//
//					if (lowerSplitType != SplitType.NONE) {
//						events.remove(lower);
//						events.remove(lower.opposite);
//					}
//
//					var higherSplitType = SplitType.NONE;
//					if (lowerSplitType == SplitType.NONE || lowerSplitType == SplitType.TOUCH) {
//						higherSplitType = addRightFutureEvents(events, onSweepLine, event, higher, foundPoints,
//								eps);
//						if (higherSplitType != SplitType.NONE) {
//							events.remove(higher);
//							events.remove(higher.opposite);
//						}
//					}
//
//					if (lowerSplitType == SplitType.NONE &&
//							higherSplitType == SplitType.NONE &&
//							event.getLine().length() > eps) {
//						splitMap.put(eventLine.getAngle(), eventLine);
//						logger.debug("split : {}", splits);
//					}
//
//					if (lowerSplitType == SplitType.CROSS || higherSplitType == SplitType.CROSS) {
//						remove(event, onSweepLine);
////						remove(event.opposite, onSweepLine);
//					} else if (lowerSplitType == SplitType.TOUCH || higherSplitType == SplitType.TOUCH) {
//						add(event, onSweepLine);
//					}
//				}
//			}
//
//			logger.debug("on sweep line {}", onSweepLine);
//
//		}
//
//		logger.info("loop count: {}", count);
//
//		return splits.values().stream()
//				.flatMap(m -> m.values().stream())
//				.toList();
//
//	}
//
//	private SplitType addLeftFutureEvents(
//			final TreeSet<CrossPointAndOriLine> events,
//			final TreeMap<Double, TreeSet<CrossPointAndOriLine>> onSweepLine,
//			final CrossPointAndOriLine event,
//			final CrossPointAndOriLine onSweep,
//			final TreeSet<OriPoint> foundPoints,
//			final double eps) {
//		if (onSweep == null) {
//			return SplitType.NONE;
//		}
//
//		var eventLine = event.getLine();
//		var lineOnSweep = onSweep.getLine();
//
//		var eventLeft = event.getLeft();
//		var eventRight = event.getRight();
//		var onSweepLeft = onSweep.getLeft();
//		var onSweepRight = onSweep.getRight();
//
//		Vector2d touchPoint = null;
//		if (GeomUtil.distancePointToSegment(event.getPoint(), lineOnSweep) < eps) {
//			touchPoint = event.getPoint();
//		}
//
//		var crossPointOpt = GeomUtil.getCrossPoint(eventLine, lineOnSweep);
//
//		if (crossPointOpt.isEmpty() && touchPoint == null) {
//			return SplitType.NONE;
//		}
//
//		var crossPoint = getAndAdd(
//				new OriPoint(touchPoint == null ? crossPointOpt.orElse(null) : touchPoint),
//				foundPoints,
//				eps);
//
//		if (crossPoint == null) {
//			return SplitType.NONE;
//		}
//
//		var splitType = SplitType.NONE;
//		if (crossPoint == touchPoint) {
//			splitType = SplitType.TOUCH;
//		} else {
//			splitType = SplitType.CROSS;
//		}
//
//		boolean splitDone = false;
//		// if left of swept line is crossing then
//		// we don't have to split the line.
//		if (onSweepLeft.getPoint().equals(crossPoint, eps)) {
//			add(onSweepLeft, onSweepLine);
//		} else {
//			addLeftFutureEvents(events, onSweepLine, crossPoint, onSweep, foundPoints, eps);
//			splitDone = true;
//		}
//
//		if (eventLeft.getPoint().equals(crossPoint, eps)) {
//			add(eventLeft, onSweepLine);
//		} else {
//			addLeftFutureEvents(events, onSweepLine, crossPoint, event, foundPoints, eps);
//			splitDone = true;
//		}
//
//		if (!splitDone) {
//			return SplitType.NONE;
//		}
//
//		return splitType;
//	}
//
//	private void addLeftFutureEvents(
//			final TreeSet<CrossPointAndOriLine> events,
//			final TreeMap<Double, TreeSet<CrossPointAndOriLine>> onSweepLine,
//			final Vector2d crossPoint,
//			final CrossPointAndOriLine p,
//			final TreeSet<OriPoint> foundPoints,
//			final double eps) {
//
//		var pLeft = p.getLeft();
//		var pRight = p.getRight();
//
//		// left side of split
//		var splitLeft = create(getAndAdd(pLeft.getPoint(), foundPoints, eps), crossPoint);
//
//		// the new event is right
//		var crossLeft = splitLeft.get(0);
//		var crossRight = splitLeft.get(1);
//
//		if (crossRight.getLine().length() > eps) {
//			// to replace with new points
//			remove(pLeft, onSweepLine);
//			events.remove(pLeft);
//
//			events.add(crossRight);
//			add(crossLeft, onSweepLine);
//			add(crossRight, onSweepLine);
//			logger.debug("split at {}, {}", crossPoint, crossRight.getLine());
//		}
//
//		// right side of split
//		var splitRight = create(crossPoint, getAndAdd(pRight.getPoint(), foundPoints, eps));
//
//		crossLeft = splitRight.get(0);
//		crossRight = splitRight.get(1);
//
//		if (crossRight.getLine().length() > eps) {
//			// to replace with new points
//			remove(pRight, onSweepLine);
//			events.remove(pRight);
//
//			// the new event is left and right
//			events.add(crossLeft);
//			add(crossLeft, onSweepLine);
//
//			events.add(crossRight);
//			add(crossRight, onSweepLine);
//
//			logger.debug("split at {}, {}", crossPoint, crossRight.getLine());
//		}
//
//	}
//
//	private SplitType addRightFutureEvents(
//			final TreeSet<CrossPointAndOriLine> events,
//			final TreeMap<Double, TreeSet<CrossPointAndOriLine>> onSweepLine,
//			final CrossPointAndOriLine event,
//			final CrossPointAndOriLine onSweep,
//			final TreeSet<OriPoint> foundPoints,
//			final double eps) {
//		if (onSweep == null) {
//			return SplitType.NONE;
//		}
//		var onSweepLeft = onSweep.getLeft();
//		var onSweepRight = onSweep.getRight();
//
//		var eventLine = event.getLine();
//		var lineOnSweep = onSweep.getLine();
//
//		Vector2d touchPoint = null;
//		if (GeomUtil.distancePointToSegment(event.getPoint(), lineOnSweep) < eps) {
//			touchPoint = event.getPoint();
//		}
//
//		var crossPointOpt = GeomUtil.getCrossPoint(eventLine, lineOnSweep);
//
//		if (crossPointOpt.isEmpty() && touchPoint == null) {
//			return SplitType.NONE;
//		}
//
//		var crossPoint = getAndAdd(
//				new OriPoint(touchPoint == null ? crossPointOpt.orElse(null) : touchPoint),
//				foundPoints,
//				eps);
//
//		if (crossPoint == null) {
//			return SplitType.NONE;
//		}
//
//		if (onSweepLeft.getPoint().equals(crossPoint, eps)) {
//			add(onSweepRight, onSweepLine);
//			return SplitType.NONE;
//		}
//		if (onSweepRight.getPoint().equals(crossPoint, eps)) {
//			add(onSweepRight, onSweepLine);
//			return SplitType.NONE;
//		}
//
//		var splitType = SplitType.NONE;
//		if (crossPoint == touchPoint) {
//			splitType = SplitType.TOUCH;
//		} else {
//			splitType = SplitType.CROSS;
//		}
//
//		addRightFutureEvents(events, onSweepLine, crossPoint, event, foundPoints, eps);
//		addRightFutureEvents(events, onSweepLine, crossPoint, onSweep, foundPoints, eps);
//
//		// event is popped from events and checked that it is right side so
//		// there is no remain.
//		// events.remove(event.opposite);
//
//		// swept point has been removed from events so we need to remove
//		// right only.
//		// events.remove(onSweepRight);
//
//		return splitType;
//	}
//
//	private void addRightFutureEvents(
//			final TreeSet<CrossPointAndOriLine> events,
//			final TreeMap<Double, TreeSet<CrossPointAndOriLine>> onSweepLine,
//			final OriPoint crossPoint,
//			final CrossPointAndOriLine p,
//			final TreeSet<OriPoint> foundPoints,
//			final double eps) {
//
//		var pLeft = p.getLeft();
//		var pRight = p.getRight();
//
//		// left side of split
//		var splitLeft = create(getAndAdd(pLeft.getPoint(), foundPoints, eps), crossPoint);
//
//		// the new event is right
//		var crossLeft = splitLeft.get(0);
//		var crossRight = splitLeft.get(1);
//
//		if (crossRight.getLine().length() > eps) {
//			// to replace with new right
//			remove(pLeft, onSweepLine);
//
//			events.add(crossRight);
//			add(crossLeft, onSweepLine);
//			add(crossRight, onSweepLine);
//			logger.debug("split at {}, {}", crossPoint, crossRight.getLine());
//		}
//
//		// right side of split
//		var splitRight = create(crossPoint, getAndAdd(pRight.getPoint(), foundPoints, eps));
//
//		crossLeft = splitRight.get(0);
//		crossRight = splitRight.get(1);
//
//		if (crossRight.getLine().length() > eps) {
//			// to replace with new right
//			remove(pRight, onSweepLine);
//
//			// the new event is left and right
//			events.add(crossLeft);
//			add(crossLeft, onSweepLine);
//
//			events.add(crossRight);
//			add(crossRight, onSweepLine);
//
//			logger.debug("split at {},{}", crossPoint, crossRight.getLine());
//		}
//	}
//
////	private Optional<CrossPointAndOriLine> getLower(
////			final CrossPointAndOriLine event,
////			final TreeMap<Double, TreeSet<CrossPointAndOriLine>> onSweepLine,
////			final double eps) {
////
//////		var lowers = onSweepLine.lowerEntry(event.getY());
//////		Optional<CrossPointAndOriLine> lowerOpt = lowers == null ? Optional.empty()
//////				: Optional.ofNullable(lowers.getValue().first());
////		Optional<CrossPointAndOriLine> lowerOpt = Optional.empty();
////
//////		if (event.isVertical()) {
////		var lowerEntries = onSweepLine.headMap(event.getY()).reversed();
////
////		var found = false;
////		for (var points : lowerEntries.values()) {
////			for (var p : points) {
////				lowerOpt = GeomUtil.getCrossPoint(
////						event.getLine(),
////						p.getLine()).isPresent() ? Optional.of(p) : Optional.empty();
////				if (lowerOpt.isPresent()) {
////					found = true;
////					break;
////				}
////			}
////			if (found) {
////				break;
////			}
////		}
//////		}
////
//////		logger.debug("lowers:{}", lowers);
////
////		return lowerOpt;
////
////	}
//
//	private Optional<CrossPointAndOriLine> getLower(
//			final CrossPointAndOriLine event,
//			final TreeMap<Double, TreeSet<CrossPointAndOriLine>> onSweepLine,
//			final double eps) {
//
//		var lowers = onSweepLine.lowerEntry(event.getY());
//		Optional<CrossPointAndOriLine> lowerOpt = lowers == null ? Optional.empty()
//				: Optional.ofNullable(lowers.getValue().first());
////		Optional<CrossPointAndOriLine> lowerOpt = Optional.empty();
//
//		if (event.isVertical()) {
//			var lowerEntries = onSweepLine.headMap(event.getY());
//
//			var found = false;
//			for (var points : lowerEntries.values()) {
//				for (var p : points) {
//					lowerOpt = GeomUtil.getCrossPoint(
//							event.getLine(),
//							p.getLine()).isPresent() ? Optional.of(p) : Optional.empty();
//					if (lowerOpt.isPresent()) {
//						found = true;
//						break;
//					}
//				}
//				if (found) {
//					break;
//				}
//			}
//		}
//
////		logger.debug("lowers:{}", lowers);
//
//		if (lowerOpt.isPresent()) {
//			return lowerOpt;
//		}
//
////		var sames = onSweepLine.get(event.getY());
////
////		if (sames != null && !sames.isEmpty()) {
////			logger.debug("same y {}", sames);
////			return Optional.ofNullable(sames.lower(event));
////		}
//
//		return Optional.empty();
//	}
//
////	private Optional<CrossPointAndOriLine> getHigher(
////			final CrossPointAndOriLine event,
////			final TreeMap<Double, TreeSet<CrossPointAndOriLine>> onSweepLine,
////			final double eps) {
////
//////		var highers = onSweepLine.higherEntry(event.getY());
////		Optional<CrossPointAndOriLine> higherOpt = Optional.empty();
////
//////		if (event.isVertical()) {
////		var higherEntries = onSweepLine.tailMap(event.getY());
////
////		var found = false;
////		for (var points : higherEntries.values()) {
////			for (var p : points) {
////				higherOpt = GeomUtil.getCrossPoint(
////						event.getLine(),
////						p.getLine()).isPresent() ? Optional.of(p) : Optional.empty();
////				if (higherOpt.isPresent()) {
////					found = true;
////					break;
////				}
////			}
////			if (found) {
////				break;
////			}
////		}
//////		}
//////
//////		logger.debug("highers:{}", highers);
////
////		return higherOpt;
////	}
//
//	private Optional<CrossPointAndOriLine> getHigher(
//			final CrossPointAndOriLine event,
//			final TreeMap<Double, TreeSet<CrossPointAndOriLine>> onSweepLine,
//			final double eps) {
//
//		var highers = onSweepLine.higherEntry(event.getY());
//		Optional<CrossPointAndOriLine> higherOpt = highers == null ? Optional.empty()
//				: Optional.ofNullable(highers.getValue().first());
//
//		if (event.isVertical()) {
//			var higherEntries = onSweepLine.tailMap(event.getY());
//
//			var found = false;
//			for (var points : higherEntries.values()) {
//				for (var p : points) {
//					higherOpt = GeomUtil.getCrossPoint(
//							event.getLine(),
//							p.getLine()).isPresent() ? Optional.of(p) : Optional.empty();
//					if (higherOpt.isPresent()) {
//						found = true;
//						break;
//					}
//				}
//				if (found) {
//					break;
//				}
//			}
//		}
//
//		logger.debug("highers:{}", highers);
//
//		if (higherOpt.isPresent()) {
//			return higherOpt;
//		}
//
////		var sames = onSweepLine.get(event.getY());
////
////		if (sames != null && !sames.isEmpty()) {
////			logger.debug("same y {}", sames);
////			return Optional.ofNullable(sames.higher(event));
////		}
//
//		return Optional.empty();
//	}

}
