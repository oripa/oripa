package oripa.domain.cptool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.vecmath.Vector2d;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.geom.GeomUtil;
import oripa.value.CalculationResource;
import oripa.value.OriLine;

public class LineAdder {
	private static final Logger logger = LoggerFactory.getLogger(LineAdder.class);

	private class PointComparatorX implements Comparator<Vector2d> {

		@Override
		public int compare(final Vector2d v1, final Vector2d v2) {
			if (v1.x == v2.x) {
				return 0;
			}
			return v1.x > v2.x ? 1 : -1;
		}
	}

	private class PointComparatorY implements Comparator<Vector2d> {

		@Override
		public int compare(final Vector2d v1, final Vector2d v2) {
			if (v1.y == v2.y) {
				return 0;
			}
			return v1.y > v2.y ? 1 : -1;
		}
	}

	/**
	 *
	 * @param inputLine
	 * @param currentLines
	 * @return true.
	 */
	private boolean divideCurrentLines(final OriLine inputLine,
			final Collection<OriLine> currentLines) {

		var toBeAdded = Collections.synchronizedList(new LinkedList<OriLine>());
		var toBeRemoved = Collections.synchronizedList(new LinkedList<OriLine>());

		currentLines.parallelStream().forEach(line -> {

			// intersection of (aux input, other type lines) are rejected // It
			// is by MITANI jun.
//			if (inputLine.getType() == OriLine.Type.NONE && line.getType() != OriLine.Type.NONE) {
//				continue;
//			}

			Vector2d crossPoint = GeomUtil.getCrossPoint(inputLine, line);
			if (crossPoint == null) {
				return;
			}

			toBeRemoved.add(line);

			Consumer<Vector2d> addIfLineCanBeSplit = v -> {
				if (GeomUtil.distance(v, crossPoint) > CalculationResource.POINT_EPS) {
					var l = new OriLine(v, crossPoint, line.getType());
					// keep selection not to change the target of copy.
					l.selected = line.selected;
					toBeAdded.add(l);
				}
			};

			addIfLineCanBeSplit.accept(line.p0);
			addIfLineCanBeSplit.accept(line.p1);
		});

		toBeRemoved.forEach(line -> currentLines.remove(line));
		toBeAdded.forEach(line -> currentLines.add(line));

		return true;
	}

	/**
	 * Input line should be divided by other lines. This function returns end
	 * points of such new small lines.
	 *
	 * @param inputLine
	 * @param currentLines
	 * @return points on input line divided by currentLines
	 */
	private List<Vector2d> createInputLinePoints(final OriLine inputLine,
			final Collection<OriLine> currentLines) {
		var points = Collections.synchronizedList(new ArrayList<Vector2d>());
		points.add(inputLine.p0);
		points.add(inputLine.p1);

		// divide input line by existing lines
		currentLines.parallelStream().forEach(line -> {
			// If the intersection is on the end of the line, skip
			if (GeomUtil.distance(inputLine.p0, line.p0) < CalculationResource.POINT_EPS ||
					GeomUtil.distance(inputLine.p0, line.p1) < CalculationResource.POINT_EPS ||
					GeomUtil.distance(inputLine.p1, line.p0) < CalculationResource.POINT_EPS ||
					GeomUtil.distance(inputLine.p1, line.p1) < CalculationResource.POINT_EPS) {
				return;
			}

			if (GeomUtil.distancePointToSegment(line.p0, inputLine.p0,
					inputLine.p1) < CalculationResource.POINT_EPS) {
				points.add(line.p0);
			}
			if (GeomUtil.distancePointToSegment(line.p1, inputLine.p0,
					inputLine.p1) < CalculationResource.POINT_EPS) {
				points.add(line.p1);
			}

			// Calculates the intersection
			Vector2d crossPoint = GeomUtil.getCrossPoint(inputLine, line);
			if (crossPoint != null) {
				points.add(crossPoint);
			}
		});

		return points;
	}

	/**
	 * Adds a new OriLine, also searching for intersections with others that
	 * would cause their mutual division
	 *
	 * @param inputLine
	 * @param currentLines
	 *            current line list. it will be affected as new lines are added
	 *            and unnecessary lines are removed.
	 */

	public void addLine(final OriLine inputLine, final Collection<OriLine> currentLines) {
		addAll(List.of(inputLine), currentLines);
	}

	/**
	 *
	 * @param inputLines
	 *            lines to be added
	 * @param currentLines
	 *            collection as a destination. This is assumed to be adapted to
	 *            concurrency.
	 */
	public void addAll(final Collection<OriLine> inputLines,
			final Collection<OriLine> currentLines) {

		var startTime = System.currentTimeMillis();

		var linesToBeAdded = inputLines.parallelStream()
				.filter(inputLine -> !currentLines.parallelStream()
						.anyMatch(line -> GeomUtil.isSameLineSegment(line, inputLine)))
				.collect(Collectors.toList());

		var pointLists = new ArrayList<List<Vector2d>>();

		logger.debug("addAll() divideCurrentLines() start: "
				+ (System.currentTimeMillis() - startTime) + "[ms]");

		linesToBeAdded.forEach(inputLine -> {
			divideCurrentLines(inputLine, currentLines);
			pointLists.add(createInputLinePoints(inputLine, currentLines));
		});

		logger.debug("addAll() divideCurrentLines() finished: "
				+ (System.currentTimeMillis() - startTime) + "[ms]");

		var newLines = Collections.synchronizedList(new ArrayList<OriLine>());
		IntStream.range(0, linesToBeAdded.size()).parallel()
				.forEach(j -> {
					var inputLine = linesToBeAdded.get(j);
					var points = pointLists.get(j);

					// sort in order to make points sequential
					boolean sortByX = Math.abs(inputLine.p0.x - inputLine.p1.x) > Math
							.abs(inputLine.p0.y - inputLine.p1.y);
					if (sortByX) {
						Collections.sort(points, new PointComparatorX());
					} else {
						Collections.sort(points, new PointComparatorY());
					}

					Vector2d prePoint = points.get(0);

					// add new lines sequentially
					for (int i = 1; i < points.size(); i++) {
						Vector2d p = points.get(i);
						// remove very short line
						if (GeomUtil.distance(prePoint, p) < CalculationResource.POINT_EPS) {
							continue;
						}

						newLines.add(new OriLine(prePoint, p, inputLine.getType()));

						prePoint = p;
					}
				});

		newLines.forEach(line -> currentLines.add(line));

		var endTime = System.currentTimeMillis();

		logger.debug("addAll(): " + (endTime - startTime) + "[ms]");
	}
}
