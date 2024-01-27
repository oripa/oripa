package oripa.domain.cptool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.cptool.compgeom.PointAndLine;
import oripa.domain.cptool.compgeom.SharedPointsMap;
import oripa.domain.cptool.compgeom.SharedPointsMapFactory;
import oripa.geom.GeomUtil;
import oripa.util.StopWatch;
import oripa.value.OriLine;
import oripa.value.OriPoint;
import oripa.vecmath.Vector2d;

/**
 * This class defines how to remove line/vertex from crease pattern.
 *
 * @author Koji
 *
 */
public class ElementRemover {
	private static final Logger logger = LoggerFactory.getLogger(ElementRemover.class);

	/**
	 * remove line from crease pattern
	 *
	 * @param l
	 * @param creasePattern
	 */
	public void removeLine(
			final OriLine l, final Collection<OriLine> creasePattern, final double pointEps) {

		creasePattern.remove(l);

		// merge the lines if possible, to prevent unnecessary vertices
		var sharedLines = createSharedLines(l.getP0(), creasePattern, pointEps);
		merge2LinesAt(l.getP0(), sharedLines, creasePattern, pointEps);
		sharedLines = createSharedLines(l.getP1(), creasePattern, pointEps);
		merge2LinesAt(l.getP1(), sharedLines, creasePattern, pointEps);
	}

	private List<OriLine> createSharedLines(final Vector2d p,
			final Collection<OriLine> creasePattern, final double pointEps) {
		return creasePattern.parallelStream()
				.filter(line -> isConnectionPoint(line.getP0(), p, pointEps)
						|| isConnectionPoint(line.getP1(), p, pointEps))
				.collect(Collectors.toList());
	}

	/**
	 * remove vertex from crease pattern
	 *
	 * @param v
	 * @param creasePattern
	 */
	public void removeVertex(
			final Vector2d v, final Collection<OriLine> creasePattern, final double pointEps) {

		List<OriLine> sharedLines = createSharedLines(v, creasePattern, pointEps);

		merge2LinesAt(v, sharedLines, creasePattern, pointEps);
	}

	private Optional<OriLine> merge2LinesAt(
			final Vector2d connectionPoint, final List<OriLine> sharedLines,
			final Collection<OriLine> creasePattern, final double pointEps) {

		if (sharedLines.size() != 2) {
			return Optional.empty();
		}

		OriLine l0 = sharedLines.get(0);
		OriLine l1 = sharedLines.get(1);

		if (!isMergePossible(l0, l1)) {
			return Optional.empty();
		}

		// Merge possibility found
		OriLine line = merge(connectionPoint, l0, l1, pointEps);

		creasePattern.remove(l0);
		creasePattern.remove(l1);
		creasePattern.add(line);

		return Optional.of(line);
	}

	private Optional<OriLine> merge2LinesAt(
			final Vector2d connectionPoint, final ArrayList<PointAndLine> sharedPoints,
			final Collection<OriLine> creasePattern, final double pointEps) {

		return merge2LinesAt(
				connectionPoint,
				sharedPoints.stream()
						.map(point -> point.getLine())
						.collect(Collectors.toList()),
				creasePattern, pointEps);
	}

	private boolean isConnectionPoint(final Vector2d p, final Vector2d q, final double pointEps) {
		return GeomUtil.areEqual(p, q, pointEps);
	}

	private boolean isMergePossible(final OriLine l0, final OriLine l1) {
		if (l0.getType() != l1.getType()) {
			return false;
		}

		// Check if the lines have the same angle
		var dir0 = l0.getLine().getDirection();
		var dir1 = l1.getLine().getDirection();

		if (!GeomUtil.isParallel(dir0, dir1)) {
			return false;
		}

		return true;
	}

	private OriLine merge(final Vector2d connectionPoint, final OriLine l0, final OriLine l1, final double pointEps) {
		Vector2d p0;
		Vector2d p1;

		if (GeomUtil.areEqual(l0.getP0(), connectionPoint, pointEps)) {
			p0 = l0.getP1();
		} else {
			p0 = l0.getP0();
		}
		if (GeomUtil.areEqual(l1.getP0(), connectionPoint, pointEps)) {
			p1 = l1.getP1();
		} else {
			p1 = l1.getP0();
		}

		return (new OriLine(p0, p1, l0.getType())).createCanonical();
	}

	private void trace(final String msg, final Collection<OriLine> lines) {
		logger.trace(msg + String.join("|",
				lines.stream()
						.map(l -> l.toString())
						.collect(Collectors.toList())));
	}

	private void removeBothSidesFromMap(final PointAndLine point,
			final TreeMap<OriPoint, ArrayList<PointAndLine>> sharedPointsMap) {
		sharedPointsMap.get(point.getKeyPoint()).remove(point);
		sharedPointsMap.get(point.getOppositeKeyPoint()).remove(point);
	}

	private void addBothSidesOfLineToMap(
			final OriLine line,
			final SharedPointsMap<PointAndLine> sharedPointsMap, final double pointEps) {
		var keyPoints = List.of(
				sharedPointsMap.findKeyPoint(line.getOriPoint0(), pointEps),
				sharedPointsMap.findKeyPoint(line.getOriPoint1(), pointEps));

		var endPoints = keyPoints.stream()
				.map(keyPoint -> new PointAndLine(keyPoint, line))
				.collect(Collectors.toList());

		endPoints.get(0).setKeyPoint(keyPoints.get(0));
		endPoints.get(0).setOppositeKeyPoint(keyPoints.get(1));
		endPoints.get(1).setKeyPoint(keyPoints.get(1));
		endPoints.get(1).setOppositeKeyPoint(keyPoints.get(0));

		IntStream.range(0, endPoints.size()).forEach(i -> {
			sharedPointsMap.get(keyPoints.get(i)).add(endPoints.get(i));
		});

	}

	private void removeMeaninglessVertices(final Collection<OriLine> creasePattern,
			final TreeSet<OriPoint> removedLinePoints, final double pointEps) {
		// Sweep-line approach
		// (sweep along x axis)

		// this map keeps the both sides of each line as an object holding the
		// end point and the line.
		var mapFactory = new SharedPointsMapFactory<PointAndLine>();
		var sharedPointsMap = mapFactory.create(creasePattern,
				(point, line) -> new PointAndLine(point, line), pointEps);

		// try merge for each line group connected at the key of the map
		sharedPointsMap.forEach((shared, sharedPoints) -> {
			trace("sharedLines@" + shared + ": " + "#=" + sharedPoints.size(),
					sharedPoints.stream()
							.map(s -> s.getLine())
							.collect(Collectors.toList()));

			if (removedLinePoints != null) {
				var boundRemovedPoints = removedLinePoints
						.headSet(new OriPoint(shared.getX() + pointEps, shared.getY() + pointEps), true)
						.tailSet(new OriPoint(shared.getX() - pointEps, shared.getY() - pointEps));
				if (boundRemovedPoints.contains(shared)) {
					logger.trace("exists in boundRemovedPoints: " + shared);
				} else if (!boundRemovedPoints.stream()
						.anyMatch(p -> GeomUtil.distance(p, shared) < pointEps)) {
					logger.trace("not to be merged: " + shared);
					return;
				}

				logger.trace("can merge at: " + shared);
			}
			var mergedLineOpt = merge2LinesAt(shared, sharedPoints, creasePattern, pointEps);

			mergedLineOpt.ifPresent(mergedLine -> {
				// if the lines are merged, the consumed old lines have to be
				// deleted from the map and the new merged line has to be added
				// to the map.

				var points = List.of(sharedPoints.get(0), sharedPoints.get(1));

				// remove old lines
				points.forEach(point -> {
					removeBothSidesFromMap(point, sharedPointsMap);
				});

				// add merged line
				addBothSidesOfLineToMap(mergedLine, sharedPointsMap, pointEps);
			});
		});

	}

	public void removeMeaninglessVertices(final Collection<OriLine> creasePattern, final double pointEps) {
		removeMeaninglessVertices(creasePattern, null, pointEps);
	}

	/**
	 * remove all lines in {@code linesToBeRemoved} from {@code creasePattern}.
	 *
	 * @param linesToBeRemoved
	 * @param creasePattern
	 */
	public void removeLines(final Collection<OriLine> linesToBeRemoved,
			final Collection<OriLine> creasePattern, final double pointEps) {
		var watch = new StopWatch(true);

		linesToBeRemoved.forEach(creasePattern::remove);

		var removedPoints = linesToBeRemoved.stream()
				.flatMap(line -> line.oriPointStream())
				.collect(Collectors.toCollection(() -> new TreeSet<>()));

		// merge lines after removing all lines to be removed.
		// merging while removing makes some lines not to be removed.

		removeMeaninglessVertices(creasePattern, removedPoints, pointEps);

		logger.debug("removeLines(): " + watch.getMilliSec() + "[ms]");
	}

	/**
	 * remove lines which are marked "selected" from given collection.
	 *
	 * @param creasePattern
	 *            collection of lines
	 */
	public void removeSelectedLines(
			final Collection<OriLine> creasePattern, final double pointEps) {

		List<OriLine> selectedLines = creasePattern.parallelStream()
				.filter(line -> line.selected)
				.collect(Collectors.toList());

		removeLines(selectedLines, creasePattern, pointEps);
	}

}
