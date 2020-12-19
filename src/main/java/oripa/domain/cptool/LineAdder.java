package oripa.domain.cptool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.vecmath.Vector2d;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.geom.GeomUtil;
import oripa.geom.RectangleDomain;
import oripa.value.CalculationResource;
import oripa.value.OriLine;
import oripa.value.OriPoint;

public class LineAdder {
	private static final Logger logger = LoggerFactory.getLogger(LineAdder.class);

	/**
	 * divides the current lines by the input line and returns a map of the
	 * cross point information.
	 *
	 * @param inputLine
	 * @param currentLines
	 * @return a map from a cross point to the crossing current line at the
	 *         point.
	 */
	private Map<OriPoint, OriLine> divideCurrentLines(final OriLine inputLine,
			final Collection<OriLine> currentLines) {

		var toBeAdded = Collections.synchronizedList(new LinkedList<OriLine>());
		var toBeRemoved = Collections.synchronizedList(new LinkedList<OriLine>());

		var crossMap = Collections.synchronizedMap(new HashMap<OriPoint, OriLine>());

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

			crossMap.put(new OriPoint(crossPoint), line);

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

		return crossMap;
	}

	/**
	 * Input line should be divided by other lines. This function returns end
	 * points of such new small lines.
	 *
	 * @param inputLine
	 * @param crossMap
	 *            what {@link #divideCurrentLines(OriLine, Collection)} returns.
	 * @param currentLines
	 * @return sorted points on input line divided by currentLines.
	 */
	private List<Vector2d> createInputLinePoints(
			final OriLine inputLine,
			final Map<OriPoint, OriLine> crossMap,
			final Collection<OriLine> currentLines) {
		var points = new ArrayList<Vector2d>();
		points.add(inputLine.p0);
		points.add(inputLine.p1);

		// divide input line by already known points and lines
		crossMap.forEach((crossPoint, line) -> {
			if (line == null) {
				return;
			}
			// If the intersection is on the end of the line, skip
			if (GeomUtil.distance(inputLine.p0, line.p0) < CalculationResource.POINT_EPS ||
					GeomUtil.distance(inputLine.p0, line.p1) < CalculationResource.POINT_EPS ||
					GeomUtil.distance(inputLine.p1, line.p0) < CalculationResource.POINT_EPS ||
					GeomUtil.distance(inputLine.p1, line.p1) < CalculationResource.POINT_EPS) {
				return;
			}

			// use end points on inputLine
			if (GeomUtil.distancePointToSegment(line.p0, inputLine.p0,
					inputLine.p1) < CalculationResource.POINT_EPS) {
				points.add(line.p0);
			}
			if (GeomUtil.distancePointToSegment(line.p1, inputLine.p0,
					inputLine.p1) < CalculationResource.POINT_EPS) {
				points.add(line.p1);
			}
			points.add(crossPoint);
		});

		// sort in order to make points sequential
		boolean sortByX = Math.abs(inputLine.p0.x - inputLine.p1.x) > Math
				.abs(inputLine.p0.y - inputLine.p1.y);
		if (sortByX) {
			points.sort(Comparator.comparing(Vector2d::getX));
		} else {
			points.sort(Comparator.comparing(Vector2d::getY));
		}

		return points;
	}

	/**
	 * Returns result of input line divisions by given points.
	 *
	 * @param inputLine
	 *            to be divided.
	 * @param points
	 *            on {@code inputLine} sequentially.
	 * @return lines created by connecting points in {@code points} one by one.
	 */
	private List<OriLine> divideInputLine(final OriLine inputLine, final List<Vector2d> points) {
		var newLines = new ArrayList<OriLine>();

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

		return newLines;
	}

	/**
	 * Adds {@code inputLine} to {@code currentLines}. The lines will be split
	 * at the intersections of the lines.
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
	 * Adds all of {@code inputLines} to {@code currentLines}. The lines will be
	 * split at the intersections of the lines.
	 *
	 * @param inputLines
	 *            lines to be added
	 * @param currentLines
	 *            collection as a destination.
	 */
	public void addAll(final Collection<OriLine> inputLines,
			final Collection<OriLine> currentLines) {

		var startTime = System.currentTimeMillis();

		var linesToBeAdded = inputLines.parallelStream()
				.filter(inputLine -> !currentLines.parallelStream()
						.anyMatch(line -> GeomUtil.isSameLineSegment(line, inputLine)))
				.collect(Collectors.toList());

		var pointLists = Collections.synchronizedList(new ArrayList<List<Vector2d>>());

		// input domain can limit the current lines to be divided.
		var inputDomainClipper = new RectangleClipper(
				new RectangleDomain(linesToBeAdded));
		// use a hash set for avoiding worst case of computation time. (list
		// takes O(n) time for deletion while hash set takes O(1) time.)
		var crossingCurrentLines = new HashSet<OriLine>(
				inputDomainClipper.selectByArea(currentLines));
		currentLines.removeAll(crossingCurrentLines);

		logger.debug("addAll() divideCurrentLines() start: "
				+ (System.currentTimeMillis() - startTime) + "[ms]");

		// a map from an input line to a map from a cross point to a line
		// crossing with the input line.
		var crossMaps = Collections.synchronizedMap(new HashMap<OriLine, Map<OriPoint, OriLine>>());

		linesToBeAdded.forEach(inputLine -> {
			crossMaps.put(inputLine, divideCurrentLines(inputLine, crossingCurrentLines));
		});

		// feed back the result of line divisions
		currentLines.addAll(crossingCurrentLines);

		logger.debug("addAll() createInputLinePoints() start: "
				+ (System.currentTimeMillis() - startTime) + "[ms]");

		linesToBeAdded.parallelStream().forEach(inputLine -> {
			pointLists.add(
					createInputLinePoints(inputLine, crossMaps.get(inputLine), currentLines));
		});

		logger.debug("addAll() adding new lines start: "
				+ (System.currentTimeMillis() - startTime) + "[ms]");

		var newLines = IntStream.range(0, linesToBeAdded.size()).parallel()
				.mapToObj(j -> divideInputLine(linesToBeAdded.get(j), pointLists.get(j)))
				.flatMap(lines -> lines.stream())
				.collect(Collectors.toList());

		newLines.forEach(line -> currentLines.add(line));

		var endTime = System.currentTimeMillis();

		logger.debug("addAll(): " + (endTime - startTime) + "[ms]");
	}
}
