package oripa.domain.cptool;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.creasepattern.CreasePattern;
import oripa.geom.GeomUtil;
import oripa.geom.RectangleDomain;
import oripa.util.StopWatch;
import oripa.value.OriLine;
import oripa.value.OriPoint;
import oripa.vecmath.Vector2d;

public class LineAdder {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

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
            final Collection<OriLine> currentLines, final double pointEps) {

        var toBeAdded = new ConcurrentLinkedQueue<OriLine>();
        var toBeRemoved = new ConcurrentLinkedQueue<OriLine>();

        var crossMap = new ConcurrentHashMap<OriPoint, OriLine>();

        // could be parallelized.
        currentLines.parallelStream()
                .forEach(line -> {
                    logger.trace("current line: {}", line);

                    var crossPointOpt = GeomUtil.getCrossPoint(inputLine, line);

                    crossPointOpt.ifPresent(crossPoint -> {
                        logger.trace("cross point: {}", crossPoint);

                        crossMap.put(new OriPoint(crossPoint), line);

                        toBeRemoved.add(line);

                        Consumer<Vector2d> addIfLineCanBeSplit = v -> {
                            if (!v.equals(crossPoint, pointEps)) {
                                var l = new OriLine(v, crossPoint, line.getType());
                                // keep selection not to change the target of
                                // copy.
                                l.setSelected(line.isSelected());
                                toBeAdded.add(l);
                            }
                        };

                        addIfLineCanBeSplit.accept(line.getP0());
                        addIfLineCanBeSplit.accept(line.getP1());
                    });

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
            final Map<OriPoint, OriLine> crossMap,
            final double pointEps) {
        var points = new ArrayList<Vector2d>();
        points.add(inputLine.getP0());
        points.add(inputLine.getP1());

        // divide input line by already known points and lines
        crossMap.forEach((crossPoint, line) -> {
            if (line == null) {
                return;
            }
            // If the intersection is on the end of the line, skip
            if (inputLine.sharesEndPoint(line, pointEps)) {
                return;
            }

            // use end points on inputLine
            if (GeomUtil.distancePointToSegment(line.getP0(), inputLine) < pointEps) {
                points.add(line.getP0());
            }
            if (GeomUtil.distancePointToSegment(line.getP1(), inputLine) < pointEps) {
                points.add(line.getP1());
            }
            points.add(crossPoint);
        });

        // sort in order to make points sequential
        return pointSorter.sortPointsOnLine(points, inputLine);
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
//		addAll(List.of(inputLine), currentLines, pointEps);
        StopWatch watch = new StopWatch(true);

        List<OriLine> nonExistingNewLines = new ArrayList<OriLine>(List.of(inputLine));

        var inputDomain = RectangleDomain.createFromSegments(nonExistingNewLines);

        // use a hash set for avoiding worst case of computation time. (list
        // takes O(n) time for deletion while hash set takes O(1) time.)
        HashSet<OriLine> crossingCurrentLines;

        // input domain can limit the current lines to be divided.
        RectangleClipper inputDomainClipper = new RectangleClipper(
                inputDomain,
                pointEps);

        if (currentLines instanceof CreasePattern cp) {
            crossingCurrentLines = new HashSet<>(inputDomainClipper.selectByArea(cp.clip(inputDomain, pointEps)));
            logger.trace("clipped {}", crossingCurrentLines);
            currentLines.removeAll(crossingCurrentLines);
        } else {
            crossingCurrentLines = new HashSet<>(inputDomainClipper.selectByArea(currentLines));
        }

        Collection<OriLine> insideLines = new HashSet<OriLine>();
        Collection<OriLine> outsideLines = new HashSet<OriLine>();
        if (!(currentLines instanceof Set<OriLine> || currentLines instanceof CreasePattern)) {
            outsideLines.addAll(currentLines);
            outsideLines.removeAll(crossingCurrentLines);
        }

        logger.trace("addAll() divideCurrentLines() start: {}[ms]", watch.getMilliSec());

        // a map from an input line to a map from a cross point to a line
        // crossing with the input line.
        Map<OriLine, Map<OriPoint, OriLine>> crossMaps = new ConcurrentHashMap<>();

        // build crossMaps and crossingCurrentLines. Cannot be in parallel since
        // a line might be divided by multiple input lines.
        nonExistingNewLines
                .forEach(line -> crossMaps.put(line,
                        divideCurrentLines(line, crossingCurrentLines, pointEps)));

        var dividedCrossingCurrentLines = divider.divideIfOverlap(nonExistingNewLines, crossingCurrentLines, pointEps);
        logger.trace("divided current {}", dividedCrossingCurrentLines);

        // feed back the result of line divisions
        insideLines.addAll(dividedCrossingCurrentLines);

        // could be parallelized.
        var pointLists = nonExistingNewLines.parallelStream()
                .map(line -> createInputLinePoints(inputLine, crossMaps.get(inputLine), pointEps))
                .toList();

        var splitNewLines = getSplitNewLines(nonExistingNewLines, pointLists, pointEps);
        var dividedSplitNewLines = divider.divideIfOverlap(crossingCurrentLines, splitNewLines, pointEps);
        logger.trace("divided input {}", dividedSplitNewLines);

        // feed back the result of line divisions allowing overlaps
        insideLines.addAll(dividedSplitNewLines);

        // reduce overlaps by overwriting types
        var insideOverwrittens = new LineTypeOverwriter().overwriteLineTypes(
                dividedSplitNewLines, insideLines, pointEps);
        logger.trace("type overwritten {}", insideOverwrittens);

        if (currentLines instanceof Set<OriLine> || currentLines instanceof CreasePattern) {
            currentLines.addAll(insideOverwrittens);
        } else {
            currentLines.clear();
            currentLines.addAll(outsideLines);
            currentLines.addAll(insideOverwrittens);
        }
    }

    public void addLineAssumingNoOverlap(final OriLine inputLine, final Collection<OriLine> currentLines,
            final double pointEps) {
//		addAllAssumingNoOverlap(List.of(inputLine), currentLines, pointEps);
        StopWatch watch = new StopWatch(true);

        List<OriLine> nonExistingNewLines = new ArrayList<OriLine>(List.of(inputLine));

        var inputDomain = RectangleDomain.createFromSegments(nonExistingNewLines);

        // use a hash set for avoiding worst case of computation time. (list
        // takes O(n) time for deletion while hash set takes O(1) time.)
        HashSet<OriLine> crossingCurrentLines;

        // input domain can limit the current lines to be divided.
        RectangleClipper inputDomainClipper = new RectangleClipper(
                inputDomain,
                pointEps);

        if (currentLines instanceof CreasePattern cp) {
            crossingCurrentLines = new HashSet<>(inputDomainClipper.selectByArea(cp.clip(inputDomain, pointEps)));
            logger.trace("clipped {}", crossingCurrentLines);
            currentLines.removeAll(crossingCurrentLines);
        } else {
            crossingCurrentLines = new HashSet<>(inputDomainClipper.selectByArea(currentLines));
        }

        Collection<OriLine> insideLines = new HashSet<OriLine>();
        Collection<OriLine> outsideLines = new HashSet<OriLine>();
        if (!(currentLines instanceof Set<OriLine>)) {
            outsideLines.addAll(currentLines);
            outsideLines.removeAll(crossingCurrentLines);
        }

        // a map from an input line to a map from a cross point to a line
        // crossing with the input line.
        Map<OriLine, Map<OriPoint, OriLine>> crossMaps = new ConcurrentHashMap<>();

        // build crossMaps and crossingCurrentLines. Cannot be in parallel since
        // a line might be divided by multiple input lines.
        nonExistingNewLines
                .forEach(line -> crossMaps.put(line,
                        divideCurrentLines(line, crossingCurrentLines, pointEps)));

        var dividedCrossingCurrentLines = crossingCurrentLines;

        // feed back the result of line divisions
        insideLines.addAll(dividedCrossingCurrentLines);

        // could be parallelized.
        var pointLists = nonExistingNewLines.parallelStream()
                .map(line -> createInputLinePoints(line, crossMaps.get(line), pointEps))
                .toList();

        var splitNewLines = getSplitNewLines(nonExistingNewLines, pointLists, pointEps);
        var dividedSplitNewLines = divider.divideIfOverlap(crossingCurrentLines, splitNewLines, pointEps);

        // feed back the result of line divisions
        insideLines.addAll(dividedSplitNewLines);

        if (currentLines instanceof Set<OriLine>) {
            currentLines.removeAll(crossingCurrentLines);
            currentLines.addAll(insideLines);
        } else {
            currentLines.clear();
            currentLines.addAll(outsideLines);
            currentLines.addAll(insideLines);
        }
        logger.trace("addAll(): {}[ms]", watch.getMilliSec());
    }

    public void addAllAssumingNoOverlap(final Collection<OriLine> inputLines,
            final Collection<OriLine> currentLines, final double pointEps) {
        inputLines.forEach(line -> addLineAssumingNoOverlap(line, currentLines, pointEps));
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
        inputLines.forEach(line -> addLine(line, currentLines, pointEps));
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
            final List<List<Vector2d>> pointLists, final double pointEps) {
        return IntStream.range(0, nonExistingNewLines.size()).parallel()
                .mapToObj(j -> sequentialLineFactory.createSequentialLines(pointLists.get(j),
                        nonExistingNewLines.get(j).getType(), pointEps))
                .flatMap(Collection::stream)
                .toList();
    }
}
