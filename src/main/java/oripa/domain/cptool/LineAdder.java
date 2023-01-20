package oripa.domain.cptool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.vecmath.Vector2d;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.geom.GeomUtil;
import oripa.geom.RectangleDomain;
import oripa.util.StopWatch;
import oripa.value.CalculationResource;
import oripa.value.OriLine;
import oripa.value.OriPoint;

public class LineAdder {
	private static final Logger logger = LoggerFactory.getLogger(LineAdder.class);

	private final SequentialLineFactory sequentialLineFactory = new SequentialLineFactory();
	private final PointSorter pointSorter = new PointSorter();
	private final OverlappingLineDivider divider = new OverlappingLineDivider();

	/**
	 * divides the current lines by the input line and returns a map of the
	 * cross point information.
	 *
	 * @return a map from a cross point to the crossing current line at the
	 *         point.
	 */
	private Map<OriPoint, OriLine> divideCurrentLines(final OriLine inputLine,
			final Collection<OriLine> currentLines) {

		var toBeAdded = Collections.synchronizedList(new LinkedList<OriLine>());
		var toBeRemoved = Collections.synchronizedList(new LinkedList<OriLine>());

		var crossMap = new ConcurrentHashMap<OriPoint, OriLine>();

		currentLines.parallelStream()
				.forEach(line -> {
					logger.trace("current line: {}", line);

					Vector2d crossPoint = GeomUtil.getCrossPoint(inputLine, line);

					// skip if lines are parallel
					if (crossPoint == null) {
						return;
					}

					logger.trace("cross point: {}", crossPoint);

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

		currentLines.removeAll(toBeRemoved);
		currentLines.addAll(toBeAdded);

		return crossMap;
	}

	/**
	 * Input line should be divided by other lines. This function returns end
	 * points of such new small lines.
	 *
	 * @param crossMap
	 *            what {@link #divideCurrentLines(OriLine, Collection)} returns.
	 * @return sorted points on input line divided by currentLines.
	 */
	private List<Vector2d> createInputLinePoints(
			final OriLine inputLine,
			final Map<OriPoint, OriLine> crossMap) {
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
		pointSorter.sortPointsOnLine(points, inputLine);

		return points;
	}

	/**
	 * Adds {@code inputLine} to {@code currentLines}. The lines will be split
	 * at the intersections of the lines.
	 *
	 * @param inputLine
	 *            Line to be added in the {@code currentLines} list
	 * @param currentLines
	 *            List of lines in which we add {@code inputLine} current line
	 *            list. it will be affected as new lines are added and
	 *            unnecessary lines are removed.
	 */
	public void addLine(final OriLine inputLine, final Collection<OriLine> currentLines, final double pointEps) {
		addAll(List.of(inputLine), currentLines, pointEps);
	}

	/**
	 * Adds all of {@code inputLines} to {@code currentLines}. The lines will be
	 * split at the intersections of the lines.
	 *
	 * TODO: test two other algorithms (OUCHI Koji, and scan line intersections)
	 *
	 * @param inputLines
	 *            lines to be added
	 * @param currentLines
	 *            collection as a destination.
	 */
	public void addAll(final Collection<OriLine> inputLines,
			final Collection<OriLine> currentLines, final double pointEps) {

		StopWatch watch = new StopWatch(true);

		List<OriLine> nonExistingNewLines = new ArrayList<OriLine>(inputLines);

		// input domain can limit the current lines to be divided.
		RectangleClipper inputDomainClipper = new RectangleClipper(
				new RectangleDomain(nonExistingNewLines),
				pointEps);
		// use a hash set for avoiding worst case of computation time. (list
		// takes O(n) time for deletion while hash set takes O(1) time.)
		HashSet<OriLine> crossingCurrentLines = new HashSet<>(
				inputDomainClipper.selectByArea(currentLines));
		currentLines.removeAll(crossingCurrentLines);

		logger.trace("addAll() divideCurrentLines() start: {}[ms]", watch.getMilliSec());

		// a map from an input line to a map from a cross point to a line
		// crossing with the input line.
		Map<OriLine, Map<OriPoint, OriLine>> crossMaps = new ConcurrentHashMap<>();

		nonExistingNewLines
				.forEach(inputLine -> crossMaps.put(inputLine, divideCurrentLines(inputLine, crossingCurrentLines)));

		divider.divideIfOverlap(nonExistingNewLines, crossingCurrentLines, pointEps);

		// feed back the result of line divisions
		currentLines.addAll(crossingCurrentLines);

		logger.trace("addAll() createInputLinePoints() start: {}[ms]", watch.getMilliSec());

		ArrayList<List<Vector2d>> pointLists = new ArrayList<>();

		nonExistingNewLines
				.forEach(inputLine -> pointLists.add(createInputLinePoints(inputLine, crossMaps.get(inputLine))));

		logger.trace("addAll() adding new lines start: {}[ms]", watch.getMilliSec());

		List<OriLine> splitNewLines = getSplitNewLines(nonExistingNewLines, pointLists, pointEps);
		divider.divideIfOverlap(crossingCurrentLines, splitNewLines, pointEps);

		currentLines.addAll(splitNewLines);

		var lineTypeOverwriter = new LineTypeOverwriter();
		lineTypeOverwriter.overwriteLineTypes(splitNewLines, currentLines, pointEps);

		logger.trace("addAll(): {}[ms]", watch.getMilliSec());
	}

	/**
	 * Splitting the {@code nonExistingNewLines} on all the points in
	 * {@code pointLists}
	 *
	 * @param nonExistingNewLines
	 * @param pointLists
	 *            Collection of Points on {@code nonExistingNewLines} from
	 *            crossings or line endpoints
	 * @return collection of all new lists
	 */
	private List<OriLine> getSplitNewLines(final List<OriLine> nonExistingNewLines,
			final ArrayList<List<Vector2d>> pointLists, final double pointEps) {
		return IntStream.range(0, nonExistingNewLines.size()).parallel()
				.mapToObj(j -> sequentialLineFactory.createSequentialLines(pointLists.get(j),
						nonExistingNewLines.get(j).getType(), pointEps))
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
	}
}
