package oripa.domain.cptool;

import static oripa.domain.cptool.OverlappingLineSplitter.splitOverlappingLines;
import static oripa.geom.GeomUtil.detectOverlap;

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
import oripa.util.StopWatch;
import oripa.value.CalculationResource;
import oripa.value.OriLine;
import oripa.value.OriPoint;

public class LineAdder {
	private static final Logger logger = LoggerFactory.getLogger(LineAdder.class);

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

		var crossMap = Collections.synchronizedMap(new HashMap<OriPoint, OriLine>());

		currentLines.parallelStream()
				.forEach(line -> {
					logger.trace("current line: {}", line);

					Vector2d crossPoint = GeomUtil.getCrossPoint(inputLine, line);
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
	 * @param points
	 *            on input line sequentially.
	 * @param lineType
	 *            of new lines.
	 *
	 * @return lines created by connecting points in {@code points} one by one.
	 */
	private List<OriLine> createSequentialLines(final List<Vector2d> points,
			final OriLine.Type lineType) {
		var newLines = new ArrayList<OriLine>();

		Vector2d prePoint = points.get(0);

		// add new lines sequentially
		for (int i = 1; i < points.size(); i++) {
			Vector2d p = points.get(i);
			// remove very short line
			if (GeomUtil.distance(prePoint, p) < CalculationResource.POINT_EPS) {
				continue;
			}

			newLines.add(new OriLine(prePoint, p, lineType));

			prePoint = p;
		}

		return newLines;
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
		Map<OriLine, Map<OriPoint, OriLine>> crossMaps = Collections.synchronizedMap(new HashMap<>());

		nonExistingNewLines
				.forEach(inputLine -> crossMaps.put(inputLine, divideCurrentLines(inputLine, crossingCurrentLines)));

		// feed back the result of line divisions
		currentLines.addAll(crossingCurrentLines);

		logger.debug("addAll() createInputLinePoints() start: {}[ms]", watch.getMilliSec());

		ArrayList<List<Vector2d>> pointLists = new ArrayList<>();

		nonExistingNewLines
				.forEach(inputLine -> pointLists.add(createInputLinePoints(inputLine, crossMaps.get(inputLine))));

		logger.debug("addAll() adding new lines start: {}[ms]", watch.getMilliSec());

		List<OriLine> splitNewLines = getSplitNewLines(nonExistingNewLines, pointLists);

		splitNewLines.forEach(splitNewLine -> addNewLineOrSplitAndAddIfOverlapping(currentLines, splitNewLine));

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
				.mapToObj(j -> createSequentialLines(pointLists.get(j), nonExistingNewLines.get(j).getType()))
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
	}

	/**
	 * Calculate new split lines if some {@code currentLines} are Overlapping
	 * with {@code splitNewLine}
	 *
	 * @param currentLines
	 * @param splitNewLine
	 */
	private void addNewLineOrSplitAndAddIfOverlapping(final Collection<OriLine> currentLines,
			final OriLine splitNewLine) {
		List<OriLine> overlappingLines = getOverlappingLines(currentLines, splitNewLine);

		if (overlappingLines.isEmpty()) {
			currentLines.add(splitNewLine);
		} else {
			replaceExistingLineWithSplitOverlappingLines(currentLines, splitNewLine, overlappingLines);
		}
	}

	/**
	 * Split {@code splitNewLine} on any {@code overlappingLines} and call until
	 * all of them were removed
	 *
	 * @param currentLines
	 *            This Collection changed after the function finished
	 * @param splitNewLine
	 * @param overlappingLines
	 */
	private void replaceExistingLineWithSplitOverlappingLines(final Collection<OriLine> currentLines,
			final OriLine splitNewLine, final List<OriLine> overlappingLines) {
		// recursion endpoint
		if (overlappingLines.size() == 0) {
			currentLines.add(splitNewLine);
			return;
		}

		// just start with any OverlappingLine that comes first
		var overlappingLine = overlappingLines.remove(0);

		// will be split into two or three Parts by one overlappingLine
		List<OriLine> oriLines = splitOverlappingLines(overlappingLine, splitNewLine);
		// remove it from the current Line Set
		currentLines.remove(overlappingLine);
		for (var newLine : oriLines) {
			// calculate remaining overlappingLines with new Segment
			var newLineOverlappingLines = getOverlappingLines(overlappingLines, newLine);
			// recursion
			replaceExistingLineWithSplitOverlappingLines(currentLines, newLine, newLineOverlappingLines);
		}
	}

	/**
	 * Calculate Collection with lines overlapping with {@code splitNewLine}
	 * from {@code currentLines}
	 *
	 * @param currentLines
	 * @param splitNewLine
	 * @return Collection can be empty if no overlapping lines are found
	 */
	private List<OriLine> getOverlappingLines(final Collection<OriLine> currentLines, final OriLine splitNewLine) {
		return currentLines.stream()
				.filter(oriLine -> !oriLine.isBoundary())
				.filter(currentLine -> detectOverlap(currentLine, splitNewLine))
				.collect(Collectors.toList());
	}
}
