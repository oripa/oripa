package oripa.domain.cptool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.cptool.compgeom.PointAndOriLine;
import oripa.domain.cptool.compgeom.SharedPointsMapFactory;
import oripa.util.StopWatch;
import oripa.util.collection.CollectionUtil;
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
				.toList();
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

		// consider eps since consecutive merges might produce a point drift.
		if (creasePattern.removeIf(l -> l0.equals(l, pointEps))) {
			logger.trace("remove {}", l0);
		}
		if (creasePattern.removeIf(l -> l1.equals(l, pointEps))) {
			logger.trace("remove {}", l1);
		}
		creasePattern.add(line);

		return Optional.of(line);
	}

	private Optional<OriLine> merge2LinesAt(
			final Vector2d connectionPoint, final ArrayList<PointAndOriLine> sharedPoints,
			final Collection<OriLine> creasePattern, final double pointEps) {

		return merge2LinesAt(
				connectionPoint,
				sharedPoints.stream()
						.map(PointAndOriLine::getLine)
						.toList(),
				creasePattern, pointEps);
	}

	private boolean isConnectionPoint(final Vector2d p, final Vector2d q, final double pointEps) {
		return p.equals(q, pointEps);
	}

	private boolean isMergePossible(final OriLine l0, final OriLine l1) {
		if (l0.getType() != l1.getType()) {
			return false;
		}

		return l0.getLine().isParallel(l1.getLine());
	}

	private OriLine merge(final Vector2d connectionPoint, final OriLine l0, final OriLine l1, final double pointEps) {
		Vector2d p0;
		Vector2d p1;

		if (l0.getP0().equals(connectionPoint, pointEps)) {
			p0 = l0.getP1();
		} else {
			p0 = l0.getP0();
		}
		if (l1.getP0().equals(connectionPoint, pointEps)) {
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
						.toList()));
	}

	private void removeMeaninglessVertices(final Collection<OriLine> creasePattern,
			final TreeSet<OriPoint> removedLinePoints, final double pointEps) {
		// Sweep-line approach
		// (sweep along x axis)

		// this map keeps the both sides of each line as an object holding the
		// end point and the line.
		var mapFactory = new SharedPointsMapFactory<PointAndOriLine>();
		var sharedPointsMap = mapFactory.create(creasePattern,
				PointAndOriLine::new, pointEps);

		// try merge for each line group connected at the key of the map
		sharedPointsMap.forEach((shared, sharedPoints) -> {
			trace("sharedLines@" + shared + ": " + "#=" + sharedPoints.size(),
					sharedPoints.stream()
							.map(PointAndOriLine::getLine)
							.toList());

			if (removedLinePoints != null) {
				var boundRemovedPoints = CollectionUtil.rangeSetInclusive(removedLinePoints,
						new OriPoint(shared.getX() - pointEps, shared.getY() - pointEps),
						new OriPoint(shared.getX() + pointEps, shared.getY() + pointEps));
				if (boundRemovedPoints.contains(shared)) {
					logger.trace("exists in boundRemovedPoints: " + shared);
				} else if (boundRemovedPoints.stream()
						.noneMatch(p -> p.equals(shared, pointEps))) {
					logger.trace("not to be merged: " + shared);
					return;
				}

				logger.trace("can merge at: " + shared);
			}
			var mergeResultOpt = merge2LinesAt(shared, sharedPoints, creasePattern, pointEps);

			mergeResultOpt.ifPresent(mergeResult -> {
				// if the lines are merged, the consumed old lines have to be
				// deleted from the map and the new merged line has to be added
				// to the map.

				if (sharedPoints.size() != 2) {
					throw new IllegalStateException(
							"sharedPoints should contain exactly two elements if merge happens.");
				}

				var points = List.of(sharedPoints.get(0), sharedPoints.get(1));

				// remove old lines
				points.forEach(sharedPointsMap::removeBothSides);

				// add merged line
				sharedPointsMap.addBothSidesOfLine(mergeResult, pointEps);
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

		creasePattern.removeAll(linesToBeRemoved);

		var removedPoints = linesToBeRemoved.stream()
				.flatMap(OriLine::oriPointStream)
				.collect(Collectors.toCollection(TreeSet::new));

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
				.filter(OriLine::isSelected)
				.toList();

		removeLines(selectedLines, creasePattern, pointEps);
	}

}
