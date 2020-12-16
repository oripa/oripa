package oripa.domain.cptool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.vecmath.Vector2d;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.geom.GeomUtil;
import oripa.value.OriLine;

/**
 * This class defines how to remove line/vertex from crease pattern.
 *
 * @author Koji
 *
 */
public class ElementRemover {
	private static final Logger logger = LoggerFactory.getLogger(ElementRemover.class);
	private static final double EPS = 1e-4;

	/**
	 * For efficient computation
	 *
	 * @author OUCHI Koji
	 *
	 */
	private static class PointAndLine {
		private final Vector2d point;
		private Vector2d keyPoint0;
		private Vector2d keyPoint1;
		private final OriLine line;

		public PointAndLine(final Vector2d point, final OriLine line) {
			this.point = point;
			this.line = line;
		}

		/**
		 * @return point
		 */
		public Vector2d getPoint() {
			return point;
		}

		/**
		 * @return line
		 */
		public OriLine getLine() {
			return line;
		}

		public double getX() {
			return point.x;
		}

		public double getY() {
			return point.y;
		}

		/**
		 * @return keyPoint0
		 */
		public Vector2d getKeyPoint0() {
			return keyPoint0;
		}

		/**
		 * @param keyPoint
		 *            Sets keyPoint
		 */
		public void setKeyPoint0(final Vector2d keyPoint) {
			this.keyPoint0 = keyPoint;
		}

		/**
		 * @return keyPoint
		 */
		public Vector2d getKeyPoint1() {
			return keyPoint1;
		}

		/**
		 * @param keyPoint
		 *            Sets keyPoint
		 */
		public void setKeyPoint1(final Vector2d keyPoint) {
			this.keyPoint1 = keyPoint;
		}

		/*
		 * (non Javadoc)
		 *
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((line == null) ? 0 : line.hashCode());
			result = prime * result + ((point == null) ? 0 : point.hashCode());
			return result;
		}

		/*
		 * (non Javadoc)
		 *
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			PointAndLine other = (PointAndLine) obj;
			if (line == null) {
				if (other.line != null) {
					return false;
				}
			} else if (!line.equals(other.line)) {
				return false;
			}
//			if (point == null) {
//				if (other.point != null) {
//					return false;
//				}
//			} else if (!point.equals(other.point)) {
//				return false;
//			}
			return true;
		}

	}

	/**
	 * remove line from crease pattern
	 *
	 * @param l
	 * @param creasePattern
	 */
	public void removeLine(
			final OriLine l, final Collection<OriLine> creasePattern) {

		creasePattern.remove(l);

		// merge the lines if possible, to prevent unnecessary vertexes
		var sharedLines = createSharedLines(l.p0, creasePattern);
		merge2LinesAt(l.p0, sharedLines, creasePattern);
		sharedLines = createSharedLines(l.p1, creasePattern);
		merge2LinesAt(l.p1, sharedLines, creasePattern);
	}

	private List<OriLine> createSharedLines(final Vector2d p,
			final Collection<OriLine> creasePattern) {
		List<OriLine> sharedLines = Collections.synchronizedList(new ArrayList<OriLine>());

		creasePattern.parallelStream()
				.filter(line -> isConnectionPoint(line.p0, p)
						|| isConnectionPoint(line.p1, p))
				.forEach(line -> sharedLines.add(line));

		return sharedLines;
	}

	/**
	 * remove vertex from crease pattern
	 *
	 * @param v
	 * @param creasePattern
	 */
	public void removeVertex(
			final Vector2d v, final Collection<OriLine> creasePattern) {

		List<OriLine> sharedLines = createSharedLines(v, creasePattern);

		merge2LinesAt(v, sharedLines, creasePattern);
	}

	private OriLine merge2LinesAt(
			final Vector2d connectionPoint, final List<OriLine> sharedLines,
			final Collection<OriLine> creasePattern) {

		if (sharedLines.size() != 2) {
			return null;
		}

		OriLine l0 = sharedLines.get(0);
		OriLine l1 = sharedLines.get(1);

		if (!isMergePossible(l0, l1)) {
			return null;
		}

		// Merge possibility found
		OriLine line = merge(connectionPoint, l0, l1);

		creasePattern.remove(l0);
		creasePattern.remove(l1);
		creasePattern.add(line);

		return line;
	}

	private OriLine merge2LinesAt(
			final Vector2d connectionPoint, final ArrayList<PointAndLine> sharedPoints,
			final Collection<OriLine> creasePattern) {

		return merge2LinesAt(
				connectionPoint,
				sharedPoints.stream()
						.map(point -> point.getLine())
						.collect(Collectors.toList()),
				creasePattern);
	}

	private OriLine makeCanonical(final OriLine line) {
		return line.p0.compareTo(line.p1) > 0
				? new OriLine(line.p1, line.p0, line.getType())
				: line;
	}

	private boolean isConnectionPoint(final Vector2d p, final Vector2d q) {
		return GeomUtil.distance(p, q) < EPS;
	}

	private boolean isMergePossible(final OriLine l0, final OriLine l1) {
		if (l0.getType() != l1.getType()) {
			return false;
		}

		// Check if the lines have the same angle
		Vector2d dir0 = new Vector2d(l0.p1.x - l0.p0.x, l0.p1.y - l0.p0.y);
		Vector2d dir1 = new Vector2d(l1.p1.x - l1.p0.x, l1.p1.y - l1.p0.y);

		dir0.normalize();
		dir1.normalize();

		if (!GeomUtil.isParallel(dir0, dir1)) {
			return false;
		}

		return true;
	}

	private OriLine merge(final Vector2d connectionPoint, final OriLine l0, final OriLine l1) {
		Vector2d p0 = new Vector2d();
		Vector2d p1 = new Vector2d();

		if (GeomUtil.distance(l0.p0, connectionPoint) < EPS) {
			p0.set(l0.p1);
		} else {
			p0.set(l0.p0);
		}
		if (GeomUtil.distance(l1.p0, connectionPoint) < EPS) {
			p1.set(l1.p1);
		} else {
			p1.set(l1.p0);
		}

		return makeCanonical(new OriLine(p0, p1, l0.getType()));
	}

	private void logDebug(final String msg, final Collection<OriLine> lines) {
		logger.debug(msg + String.join("|",
				lines.stream()
						.map(l -> l.toString())
						.collect(Collectors.toList())));
	}

	private ArrayList<PointAndLine> createXOrderPoints(final ArrayList<OriLine> lines) {
		var points = new ArrayList<PointAndLine>(lines.size() * 2);

		for (int i = 0; i < lines.size(); i++) {
			var line = lines.get(i);
			points.add(new PointAndLine(line.p0, line));
			points.add(new PointAndLine(line.p1, line));
		}

		points.sort(Comparator.comparing(PointAndLine::getX));

		return points;
	}

	/**
	 * remove all lines in {@code linesToBeRemoved} from {@code creasePattern}.
	 *
	 * @param linesToBeRemoved
	 * @param creasePattern
	 */
	public void removeLines(final Collection<OriLine> linesToBeRemoved,
			final Collection<OriLine> creasePattern) {

		linesToBeRemoved.forEach(line -> creasePattern.remove(line));
		// logDebug("creasePattern after removing: ", creasePattern);

		// merge lines after removing all lines to be removed.
		// merging while removing makes some lines not to be removed.

		// naive implementation
//		creasePattern.forEach(line -> {
//			merge2LinesAt(line.p0, creasePattern);
//			merge2LinesAt(line.p1, creasePattern);
//		});

		// Sweep-line approach
		// (sweep along x axis)

		var sortedLines = creasePattern.stream()
				.map(line -> makeCanonical(line))
				.sorted()
				.collect(Collectors.toCollection(() -> new ArrayList<>()));

		var xOrderPoints = createXOrderPoints(sortedLines);
		var hashFactory = new HashFactory();
		var xOrderHash = hashFactory.create(xOrderPoints, PointAndLine::getX, EPS);

		for (var byX : xOrderHash) {
			byX.sort(Comparator.comparing(PointAndLine::getY));
		}

		// this map keeps the both side of each line as an object holding the
		// end point and the line object.
		var sharedPointsMap = new TreeMap<Vector2d, ArrayList<PointAndLine>>();

		// build a map and set keyPoint0
		for (var byX : xOrderHash) {
			var yHash = hashFactory.create(byX, PointAndLine::getY, 1e-5);
			for (var xyPoints : yHash) {
				var point0 = xyPoints.get(0);
				sharedPointsMap.put(point0.getPoint(), xyPoints);
				xyPoints.forEach(p -> p.setKeyPoint0(point0.getPoint()));
			}
		}

		// set keyPoint1(opposite end point for map's key)
		for (var keyPoint : sharedPointsMap.keySet()) {
			for (var point : sharedPointsMap.get(keyPoint)) {
				var line = point.getLine();
				var keyPoint1 = GeomUtil.distance(line.p0, keyPoint) < EPS
						? sharedPointsMap.floorKey(line.p1)
						: sharedPointsMap.floorKey(line.p0);
				point.setKeyPoint1(keyPoint1);
			}
		}

		// try merge for each line group connected at the key of the map
		sharedPointsMap.forEach((shared, sharedPoints) -> {
			logDebug("sharedLines@" + shared + ": " + "#=" + sharedPoints.size(),
					sharedPoints.stream()
							.map(s -> s.getLine())
							.collect(Collectors.toList()));

			var mergedLine = merge2LinesAt(shared, sharedPoints, creasePattern);

			if (mergedLine == null) {
				return;
			}

			// if the lines are merged, the consumed old lines have to be
			// deleted from the map and the new merged line has to be added
			// to the map.

			var points = List.of(sharedPoints.get(0), sharedPoints.get(1));

			// remove old lines
			points.forEach(point -> {
				sharedPointsMap.get(point.getKeyPoint0()).remove(point);
				sharedPointsMap.get(point.getKeyPoint1()).remove(point);

			});

			// extract floor point as key points
			var keyPoints = List.of(
					sharedPointsMap.floorKey(mergedLine.p0),
					sharedPointsMap.floorKey(mergedLine.p1));

			var merged = keyPoints.stream()
					.map(keyPoint -> new PointAndLine(keyPoint, mergedLine))
					.collect(Collectors.toList());

			merged.get(0).setKeyPoint0(keyPoints.get(0));
			merged.get(0).setKeyPoint1(keyPoints.get(1));
			merged.get(1).setKeyPoint0(keyPoints.get(1));
			merged.get(1).setKeyPoint1(keyPoints.get(0));

			IntStream.range(0, merged.size()).forEach(i -> {
				sharedPointsMap.get(keyPoints.get(i)).add(merged.get(i));
			});
		});

	}

	/**
	 * remove lines which are marked "selected" from given collection.
	 *
	 * @param creasePattern
	 *            collection of lines
	 */
	public void removeSelectedLines(
			final Collection<OriLine> creasePattern) {

		List<OriLine> selectedLines = creasePattern.parallelStream()
				.filter(line -> line.selected)
				.collect(Collectors.toList());

		removeLines(selectedLines, creasePattern);
	}

}
