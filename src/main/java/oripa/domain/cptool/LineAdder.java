package oripa.domain.cptool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
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

		toBeRemoved.forEach(currentLines::remove);
		currentLines.addAll(toBeAdded);

		return crossMap;
	}

	private void divideIfOverlap(final Collection<OriLine> dividerLines, final Collection<OriLine> lines) {
		var extractor = new OverlappingLineExtractor();

		var allLines = new HashSet<OriLine>(dividerLines);
		allLines.addAll(lines);

		var overlapGroups = extractor.extractOverlapsGroupedBySupport(allLines);

		Set<OriLine> dividerLineSet = new HashSet<>(dividerLines);
		Set<OriLine> lineSet = ConcurrentHashMap.newKeySet();

		lineSet.addAll(lines);

		overlapGroups.parallelStream().forEach(overlaps -> {
			var dividerOverlaps = overlaps.stream()
					.filter(ov -> dividerLineSet.contains(ov))
					.collect(Collectors.toSet());

			var lineOverlaps = overlaps.stream()
					.filter(ov -> !dividerOverlaps.contains(ov))
					.collect(Collectors.toSet());

			lineSet.removeAll(lineOverlaps);

			dividerOverlaps.forEach(divider -> divideLinesIfOverlap(divider, lineOverlaps));

			lineSet.addAll(lineOverlaps);
		});

		lines.clear();
		lines.addAll(lineSet);
	}

	private void divideLinesIfOverlap(final OriLine dividerLine, final Collection<OriLine> lines) {

		Set<OriLine> targettedLines = ConcurrentHashMap.newKeySet();
		Set<OriLine> splitLines = ConcurrentHashMap.newKeySet();

		BiFunction<OriLine, Vector2d, List<Vector2d>> createSplitPoints = (line, p) -> {
			var points = new ArrayList<Vector2d>(List.of(line.getP0(), line.getP1()));

			// is close to segment?
			if (GeomUtil.distancePointToSegment(p, line.getP0(),
					line.getP1()) > CalculationResource.POINT_EPS) {
				return points;
			}

			if (GeomUtil.distance(p, line.getP0()) >= CalculationResource.POINT_EPS) {
				points.add(p);
			} else if (GeomUtil.distance(p, line.getP1()) >= CalculationResource.POINT_EPS) {
				points.add(p);
			}

			return points;
		};

		lines.parallelStream()
				.forEach(line -> {
					var splitPoints = new ArrayList<Vector2d>();

					int overlapCount = GeomUtil.distinguishLineSegmentsOverlap(dividerLine.getP0(), dividerLine.getP1(),
							line.getP0(), line.getP1());

					switch (overlapCount) {
					case 2:
					case 3:
						splitPoints.addAll(createSplitPoints.apply(line, dividerLine.getP0()));
						splitPoints.addAll(createSplitPoints.apply(line, dividerLine.getP1()));
						break;
					default:
						return;
					}

					sortPointsOnLine(splitPoints, line);

					targettedLines.add(line);
					splitLines.addAll(
							sequentialLineFactory.createSequentialLines(splitPoints, line.getType()));
				});

		lines.removeAll(targettedLines);
		lines.addAll(splitLines);
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
		sortPointsOnLine(points, inputLine);

		return points;
	}

	private void sortPointsOnLine(final ArrayList<Vector2d> points, final OriLine line) {
		boolean sortByX = Math.abs(line.p0.x - line.p1.x) > Math.abs(line.p0.y - line.p1.y);
		if (sortByX) {
			points.sort(Comparator.comparing(Vector2d::getX));
		} else {
			points.sort(Comparator.comparing(Vector2d::getY));
		}
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
	public void addLine(final OriLine inputLine, final Collection<OriLine> currentLines) {
		addAll(List.of(inputLine), currentLines);
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
			final Collection<OriLine> currentLines) {

		StopWatch watch = new StopWatch(true);

		List<OriLine> nonExistingNewLines = new ArrayList<OriLine>(inputLines);

		// input domain can limit the current lines to be divided.
		RectangleClipper inputDomainClipper = new RectangleClipper(
				new RectangleDomain(nonExistingNewLines),
				CalculationResource.POINT_EPS);
		// use a hash set for avoiding worst case of computation time. (list
		// takes O(n) time for deletion while hash set takes O(1) time.)
		HashSet<OriLine> crossingCurrentLines = new HashSet<>(
				inputDomainClipper.selectByArea(currentLines));
		currentLines.removeAll(crossingCurrentLines);

		logger.debug("addAll() divideCurrentLines() start: {}[ms]", watch.getMilliSec());

		// a map from an input line to a map from a cross point to a line
		// crossing with the input line.
		Map<OriLine, Map<OriPoint, OriLine>> crossMaps = new ConcurrentHashMap<>();

		nonExistingNewLines
				.forEach(inputLine -> crossMaps.put(inputLine, divideCurrentLines(inputLine, crossingCurrentLines)));

		divideIfOverlap(nonExistingNewLines, crossingCurrentLines);

		// feed back the result of line divisions
		currentLines.addAll(crossingCurrentLines);

		logger.debug("addAll() createInputLinePoints() start: {}[ms]", watch.getMilliSec());

		ArrayList<List<Vector2d>> pointLists = new ArrayList<>();

		nonExistingNewLines
				.forEach(inputLine -> pointLists.add(createInputLinePoints(inputLine, crossMaps.get(inputLine))));

		logger.debug("addAll() adding new lines start: {}[ms]", watch.getMilliSec());

		List<OriLine> splitNewLines = getSplitNewLines(nonExistingNewLines, pointLists);
		divideIfOverlap(crossingCurrentLines, splitNewLines);

		currentLines.addAll(splitNewLines);

		var lineTypeOverwriter = new LineTypeOverwriter();
		lineTypeOverwriter.overwriteLineTypes(splitNewLines, currentLines);

		logger.debug("addAll(): {}[ms]", watch.getMilliSec());
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
			final ArrayList<List<Vector2d>> pointLists) {
		return IntStream.range(0, nonExistingNewLines.size()).parallel()
				.mapToObj(j -> sequentialLineFactory.createSequentialLines(pointLists.get(j),
						nonExistingNewLines.get(j).getType()))
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
	}
}
