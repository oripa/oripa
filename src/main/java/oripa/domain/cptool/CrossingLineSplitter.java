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
import java.util.function.Predicate;

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

	private double eps;

	private static class EventPoint implements Comparable<EventPoint> {
		private final OriPoint point;
		private final OriLine line;

		public static List<EventPoint> create(final OriLine line) {
			var left = new EventPoint(line.getOriPoint0(), line);
			var right = new EventPoint(line.getOriPoint1(), line);

			return List.of(left, right);
		}

		private EventPoint(final OriPoint point, final OriLine line) {
			this.point = point;
			this.line = line;
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
		public OriLine getLine() {
			return line;
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

			if (comp == 0) {
				comp = line.compareTo(o.line);
			}

			return comp;
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
			return point + " " + line.toString();
		}
	}

	private record StatusElementSegment(Double yAtSweep, Double x, OriLine line, boolean containsEvent)
			implements Comparable<StatusElementSegment> {

		private static final double X_DIFF = 1e-8;

		public static StatusElementSegment create(final OriPoint eventPosition, final OriLine line,
				final TreeSet<Double> foundYs, final double eps) {
			var x = eventPosition.getX();
			var y = computeYPlus(line, x, 0, line.getP0().getY());

			if (line.isVertical()) {
				y = eventPosition.getY();
			}

			y = getAndAdd(y, foundYs, eps);

			var containsPoint = containsPoint(line, eventPosition, eps);
			return new StatusElementSegment(containsPoint ? eventPosition.getY() : y, x, line, containsPoint);
		}

		@Override
		public int compareTo(final StatusElementSegment o) {
			var comp = Double.compare(yAtSweep, o.yAtSweep);

			if (comp == 0) {
				if (containsEvent && o.containsEvent && line.isVertical() && !o.line.isVertical()) {
					return 1;
				}
				if (containsEvent && o.containsEvent && !line.isVertical() && o.line.isVertical()) {
					return -1;
				}

				var dx = X_DIFF;
				var x = this.x;
				var y = computeYPlus(line, x, dx, yAtSweep);

				var ox = x;
				var oy = computeYPlus(o.line, ox, dx, yAtSweep);

				if (Double.isNaN(y)) {
					y = yAtSweep;
				}
				if (Double.isNaN(oy)) {
					oy = o.yAtSweep;
				}

				comp = Double.compare(y, oy);

				// logger.debug("compare {}->{} {}->{} {}", line, y, o.line, oy,
				// comp);
			}
			if (comp == 0) {
				comp = line.compareTo(o.line);
			}

			return comp;
		}

		private static double computeX(final OriLine l, final double yAtSweep) {
			if (l.isHorizontal()) {
				return Double.NaN;
			}

			if (l.isVertical()) {
				return l.getP0().getX();
			}

			var x = l.getAffineXValueAt(yAtSweep);

			return x;
		}

		private static double computeYPlus(final OriLine l, final double x, final double dx, final double horizontalY) {
			if (l.isHorizontal()) {
				return horizontalY;

			}

			if (l.isVertical()) {
				return Double.NaN;
			}

			var x1 = l.getP1().getX();
			if (x > x1 + dx) {
				return l.getP1().getY();
			}

			var y = l.getAffineYValueAt(x + dx);

			return y;
		}

		@Override
		public final boolean equals(final Object arg0) {
			if (arg0 instanceof StatusElementSegment s) {
				return yAtSweep.equals(s.yAtSweep) && line.equals(s.line);
			}
			return false;
		}

		@Override
		public final int hashCode() {
			return Objects.hash(yAtSweep, line);
		}
	}

	private void initializePoints(final Collection<OriLine> inputLines, final List<OriLine> fixedLines,
			final List<EventPoint> points) {

		var verticals = new HashSet<>(inputLines.stream().filter(OriLine::isVertical).toList());
		for (var line : verticals) {
			var p0 = line.getOriPoint0();
			var p1 = line.getOriPoint1();
			var lower = p0.getY() < p1.getY() ? p0 : p1;
			var higher = p0.getY() >= p1.getY() ? p0 : p1;

			var lx = lower.getX();
			var ly = getAndAdd(lower.getY(), foundYs, eps);
			var hx = lx;
			var hy = getAndAdd(higher.getY(), foundYs, eps);

			var fixedLine = new OriLine(
					getAndAdd(new OriPoint(lx, ly), foundPoints, foundYs, eps),
					getAndAdd(new OriPoint(hx, hy), foundPoints, foundYs, eps),
					Type.MOUNTAIN);
			fixedLines.add(fixedLine);

			points.addAll(EventPoint.create(fixedLine));
		}

		var nonVerticals = inputLines.stream().filter(Predicate.not(verticals::contains)).toList();
		for (var line : nonVerticals) {
			var x0 = line.getOriPoint0().getX();
			var y0 = getAndAdd(line.getOriPoint0().getY(), foundYs, eps);
			var x1 = line.getOriPoint1().getX();
			var y1 = getAndAdd(line.getOriPoint1().getY(), foundYs, eps);

			var fixedLine = new OriLine(
					getAndAdd(new OriPoint(x0, y0), foundPoints, foundYs, eps),
					getAndAdd(new OriPoint(x1, y1), foundPoints, foundYs, eps),
					Type.MOUNTAIN);

			fixedLines.add(fixedLine);

			var canonical = new OriLine(fixedLine.getP0(), fixedLine.getP1(), Type.MOUNTAIN).createCanonical();

			points.addAll(EventPoint.create(canonical));
		}
	}

	private static boolean containsPoint(final OriLine line, final Vector2d p, final double eps) {
		return GeomUtil.distancePointToSegment(p, line) < eps;
	}

	private static OriPoint getAndAdd(
			final OriPoint point, final TreeSet<OriPoint> foundPoints, final TreeSet<Double> foundYs,
			final double eps) {

		var range = CollectionUtil.rangeSetInclusive(foundPoints,
				new OriPoint(point.getX() - eps, point.getY() - eps),
				new OriPoint(point.getX() + eps, point.getY() + eps));
		var y = getAndAdd(point.getY(), foundYs, eps);

		var filtered = range.stream().filter(p -> point.equals(p, eps));
		var firstOpt = filtered.findFirst().map(p -> new OriPoint(p.getX(), y));

		if (firstOpt.isEmpty()) {
			foundPoints.add(point);
			return point;
		}

		return firstOpt.get();
	}

	private static Double getAndAdd(
			final double y, final TreeSet<Double> foundYs, final double eps) {

		var range = CollectionUtil.rangeSetInclusive(foundYs, y - eps, y + eps);

		var firstOpt = range.stream().findFirst();

		if (firstOpt.isEmpty()) {
			foundYs.add(y);
			return y;
		}

		return firstOpt.get();
	}

	private TreeSet<OriPoint> foundPoints;
	private TreeSet<Double> foundYs;
	private TreeSet<StatusElementSegment> sweepStatus;
	private TreeSet<EventPoint> events;

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
		this.eps = eps;
		foundPoints = new TreeSet<>();
		foundYs = new TreeSet<>();

		var points = new ArrayList<EventPoint>();
		var fixedLines = new ArrayList<OriLine>();

		initializePoints(inputLines, fixedLines, points);

		sweepStatus = new TreeSet<StatusElementSegment>();

		events = new TreeSet<EventPoint>(points);

		var count = 0;

		var leftMap = buildLeftMap(
				points.stream()
						.filter(p -> p.getPoint().equals(p.getLine().getP0()))
						.map(EventPoint::getLine)
						.toList());

		logger.debug("left map {}", leftMap);

		var crossInfos = new HashMap<OriPoint, Collection<OriLine>>();

		BiConsumer<EventPoint, Collection<OriLine>> reportReceiver = (event, crossLines) -> {
			crossInfos.put(event.getPoint(), crossLines);
		};

		while (!events.isEmpty() && count++ <= 4 * inputLines.size() * inputLines.size()) {
			var event = events.removeFirst();

			logger.debug("event {}", event);

			handleEventPoint(event, leftMap, reportReceiver);

			logger.debug("sweep status x = {} {}", event.getX(), sweepStatus);
		}

		var splits = split(fixedLines, crossInfos);
		logger.debug("splits {}", splits);
		logger.debug("loop count {}", count);

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
			final Map<OriPoint, Collection<OriLine>> crossInfos) {

		var segmentConvert = new HashMap<OriLine, MutableSegment>();
		var usedOriLine = new HashSet<OriLine>();

		originalLines.forEach(line -> segmentConvert.put(line, new MutableSegment(line.getP0(), line.getP1())));

		var mutableCrossInfos = new TreeMap<OriPoint, Collection<MutableSegment>>();

		crossInfos.forEach((crossPoint, lines) -> {
			mutableCrossInfos.put(crossPoint,
					lines.stream()
							.map(line -> segmentConvert.get(line))
							.toList());
			usedOriLine.addAll(lines);
		});

		var unused = new HashSet<>(originalLines);
		unused.removeAll(usedOriLine);

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

		splits.addAll(unused);

		return splits;
	}

	private HashMap<OriPoint, Set<OriLine>> buildLeftMap(final Collection<OriLine> sortedAllLines) {
		var map = new HashMap<OriPoint, Set<OriLine>>();

		sortedAllLines.stream().forEach(line -> {
			map.putIfAbsent(getAndAdd(line.getOriPoint0(), foundPoints, foundYs, eps), new HashSet<>());
			map.get(line.getOriPoint0()).add(line);
		});

		return map;
	}

	private void handleEventPoint(final EventPoint event,
			final HashMap<OriPoint, Set<OriLine>> leftMap,
			final BiConsumer<EventPoint, Collection<OriLine>> reportReceiver) {
		var eventPosition = event.getPoint();

		// left end points corresponding to event point (= points to be swept)
		var lefts = getLefts(event, leftMap);

		logger.debug("lefts {}", lefts);
		logger.debug("add lefts to sweep status {}", sweepStatus);

		// right end points corresponding to event point (= swept lines'
		// points)
		var rights = getRights(event);
		logger.debug("rights {}", rights);

		// interior points correspond to event point
		var interiors = getInteriors(event);
		logger.debug("interiors {} from {}", interiors, sweepStatus);

		var crossings = new HashSet<OriLine>();

		crossings.addAll(rights.stream().map(StatusElementSegment::line).toList());
		crossings.addAll(lefts.stream().map(StatusElementSegment::line).toList());
		crossings.addAll(interiors.stream().map(StatusElementSegment::line).toList());

		if (crossings.size() > 1) {
			report(event, crossings, reportReceiver);
		}

		var oldRights = getOldRights(event);
		oldRights.forEach(s -> sweepStatus.remove(s));
		rights.forEach(s -> sweepStatus.remove(s));
		interiors.forEach(s -> sweepStatus.remove(s));

		// old lefts can turn to interior
		var oldLefts = getOldLeftsOnTreat(event);
		oldLefts.forEach(s -> sweepStatus.remove(s));
		oldLefts.forEach(s -> {
			sweepStatus.add(StatusElementSegment.create(eventPosition, s.line, foundYs, eps));
		});
		interiors.forEach(s -> {
			sweepStatus.add(
					StatusElementSegment.create(
							eventPosition,
							s.line,
							foundYs,
							eps));
		});

		lefts.forEach(s -> sweepStatus.add(s));

		logger.debug("updated sweep status {}", sweepStatus);

		var leftsAndInteriors = computeLeftsAndInteriors(event, lefts);

		if (leftsAndInteriors.isEmpty()) {
			var lowerOpt = getLower(event, event.getLine());
			var higherOpt = getHigher(event, event.getLine());

			var lower = lowerOpt.orElse(null);
			var higher = higherOpt.orElse(null);

			var crossPoint = computeCrossPoint(lower, higher);

			logger.debug("no lefts and interiors");
			findNewEvent(lower, higher, event, crossPoint);
		} else {
			logger.debug("lower");
			// logger.debug("leftsAndInterior {}", leftsAndinteriors);

			var localLowest = leftsAndInteriors.first().line;
			var lowerOpt = getLower(event, localLowest);

			var lower = lowerOpt.orElse(null);

			var crossPoint = computeCrossPoint(lower, localLowest);
			findNewEvent(lower, localLowest, event, crossPoint);

			leftsAndInteriors = computeLeftsAndInteriors(event, lefts);
			var localHighest = leftsAndInteriors.last().line;

			logger.debug("higher");

			var higherOpt = getHigher(event, localHighest);
			var higher = higherOpt.orElse(null);

			crossPoint = computeCrossPoint(higher, localHighest);
			findNewEvent(higher, localHighest, event, crossPoint);

		}

	}

	private void report(final EventPoint event, final Collection<OriLine> crossings,
			final BiConsumer<EventPoint, Collection<OriLine>> reportReceiver) {
		logger.debug("report {} {}", event, crossings);
		reportReceiver.accept(event, crossings);
	}

	private OriPoint computeCrossPoint(final OriLine line0, final OriLine line1) {

		if (line0 == null || line1 == null) {
			return null;
		}

		var crossPointOpt = GeomUtil.getCrossPoint(line0, line1);

		var endPoint0Opt = line0.pointStream().filter(p -> GeomUtil.distancePointToSegment(p, line1) < eps)
				.findFirst();
		var endPoint1Opt = line1.pointStream().filter(p -> GeomUtil.distancePointToSegment(p, line0) < eps)
				.findFirst();

		crossPointOpt = endPoint0Opt.isPresent()
				? endPoint0Opt
				: endPoint1Opt.isPresent()
						? endPoint1Opt
						: crossPointOpt;
		if (crossPointOpt.isEmpty()) {
			return null;
		}

		var cp = crossPointOpt.get();

		var x = line0.isVertical() ? line0.getP0().getX() : line1.isVertical() ? line1.getP0().getX() : cp.getX();
		var y = getAndAdd(cp.getY(), foundYs, eps);

		var crossPoint = getAndAdd(new OriPoint(x, y), foundPoints, foundYs, eps);

		return crossPoint;
	}

	/**
	 * returns lines as status element whose left end point is the given event
	 * point.
	 *
	 * @param event
	 * @param eps
	 * @return
	 */
	private Set<StatusElementSegment> getLefts(final EventPoint event, final HashMap<OriPoint, Set<OriLine>> leftMap) {
		var lefts = leftMap.get(event.getPoint());
		if (lefts == null) {
			lefts = Set.of();
		}

		return new HashSet<>(lefts.stream()
				.map(line -> StatusElementSegment.create(
						event.getPoint(),
						line,
						foundYs,
						eps))
				.toList());
	}

	/**
	 * @param event
	 * @param eps
	 * @return
	 */
	private Set<StatusElementSegment> getOldLeftsOnTreat(
			final EventPoint event) {

		var oldLefts = sweepStatus.stream()
				.filter(s ->
				// usual
				!s.line.isVertical()
						&& s.line.getP0().getX() <= event.getX() + eps
						&& s.line.getP1().getX() > event.getX() + eps
						// vertical
						|| s.line.isVertical()
								&& MathUtil.areEqual(s.line.getP0().getX(), s.line.getP1().getX(), eps)
								&& MathUtil.areEqual(s.line.getP0().getX(), event.getX(), eps)
								&& s.line.getP0().getY() <= event.getY() - eps
								&& s.line.getP1().getY() > event.getY() + eps)

				.toList();

		return new HashSet<>(oldLefts);
	}

	/**
	 *
	 * @param event
	 * @param eps
	 * @return
	 */
	private Set<StatusElementSegment> getOldRights(
			final EventPoint event) {

		var oldRights = sweepStatus.stream().filter(s ->
		// usual
		!s.line.isVertical()
				&& s.line.getP1().getX() < event.getX() - eps
				// vertical
				|| s.line.isVertical()
						&& MathUtil.areEqual(s.line.getP0().getX(), s.line.getP1().getX(), eps)
						&& MathUtil.areEqual(s.line.getP0().getX(), event.getX(), eps)
						&& s.line.getP1().getY() < event.getY() - eps)
				.toList();

		return new HashSet<>(oldRights);
	}

	/**
	 * returns current status element whose right end point is the given event
	 * point.
	 *
	 * @param event
	 * @param eps
	 * @return
	 */
	private Set<StatusElementSegment> getRights(
			final EventPoint event) {

		var rights = getAffinePoints(event).stream()
				.filter(s -> s.line.getP1().equals(event.getPoint(), eps))
				.toList();

		return new HashSet<>(rights);
	}

	/**
	 * returns current status element that contains the given event point.
	 *
	 * @param event
	 * @param eps
	 * @return
	 */
	private Set<StatusElementSegment> getInteriors(
			final EventPoint event) {

		var statusSegments = getAffinePoints(event);
		return new HashSet<>(statusSegments);
	}

	/**
	 * returns current status element that contains the given event point.
	 *
	 * @param event
	 * @param eps
	 * @return
	 */
	private Set<StatusElementSegment> getAffinePoints(final EventPoint event) {
		var statusSegments = sweepStatus
				.stream()
				// event point should be on the line
				.filter(s -> GeomUtil.distancePointToSegment(event.getPoint(), s.line) < eps).toList();

		return new HashSet<>(statusSegments);
	}

	private Optional<OriLine> getLower(
			final EventPoint event,
			final OriLine target) {

		var eventStatusSegment = StatusElementSegment.create(
				event.getPoint(),
				target,
				foundYs,
				eps);
		var lower = sweepStatus.lower(eventStatusSegment);
		Optional<StatusElementSegment> lowerOpt = Optional.ofNullable(lower);

//logger.debug("lowers:{}", lowers);

		return lowerOpt.map(s -> s.line);

	}

	private Optional<OriLine> getHigher(
			final EventPoint event,
			final OriLine target) {

		var eventStatusSegment = StatusElementSegment.create(
				event.getPoint(),
				target,
				foundYs,
				eps);
		var higher = sweepStatus.higher(eventStatusSegment);
		Optional<StatusElementSegment> higherOpt = Optional.ofNullable(higher);

//logger.debug("lowers:{}", lowers);

		return higherOpt.map(s -> s.line);
	}

	private TreeSet<StatusElementSegment> computeLeftsAndInteriors(final EventPoint event,
			final Collection<StatusElementSegment> lefts) {

		var interiors = getInteriors(event);

		var leftsAndInteriors = new TreeSet<StatusElementSegment>();
		lefts.forEach(s -> leftsAndInteriors.add(
				StatusElementSegment.create(event.getPoint(), s.line, foundYs, eps)));
		interiors.forEach(s -> leftsAndInteriors.add(
				StatusElementSegment.create(event.getPoint(), s.line, foundYs, eps)));

		return leftsAndInteriors;
	}

	private void findNewEvent(final OriLine lower, final OriLine higher,
			final EventPoint event,
			final OriPoint crossPoint) {

		logger.debug("find new events for {} {} {}", lower, higher, event);

		if (lower == null || higher == null || crossPoint == null) {
			return;
		}

		Function<OriLine, EventPoint> create = (line) -> {
			return new EventPoint(crossPoint, line);
		};

		if (crossPoint.getX() > event.getX() + eps) {
			logger.debug("cross on right. {}", crossPoint);

			events.add(create.apply(lower));
			events.add(create.apply(higher));

			return;
		}

		if (MathUtil.areEqual(crossPoint.getX(), event.getX(), eps)) {
			if (crossPoint.getY() > event.getY()) {
				logger.debug("same x, cross on higher. {}", crossPoint);
				events.add(create.apply(lower));
				events.add(create.apply(higher));
			}
		}
	}

}
