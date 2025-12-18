package oripa.domain.cptool;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import oripa.domain.creasepattern.CreasePattern;
import oripa.geom.GeomUtil;
import oripa.value.OriLine;
import oripa.vecmath.Vector2d;

/**
 * A tool to modify crease pattern (or line collection)
 *
 * @author Koji
 *
 */
public class Painter {
	// FIXME all methods should return success/failure

	private final CreasePattern creasePattern;

	private final double pointEps;

	private final LineSelectionModifier selectionModifier = new LineSelectionModifier();
	private final ElementRemover elementRemover = new ElementRemover();
	private final LineAdder lineAdder = new LineAdder();
	private final LineMirror lineMirror = new LineMirror();
	private final LineDivider lineDivider = new LineDivider();
	private final LineTypeChanger typeChanger = new LineTypeChanger();
	private final AngleBisectorFactory bisectorFactory = new AngleBisectorFactory();
	private final SymmetricLineFactory symmetricFactory = new SymmetricLineFactory();
	private final RotatedLineFactory rotationFactory = new RotatedLineFactory();
	private final TiledLineFactory tileFactory = new TiledLineFactory();

	@SuppressWarnings("unused")
	private Painter() {
		creasePattern = null;
		pointEps = 0;
	}

	public Painter(final CreasePattern aCreasePattern, final double pointEps) {
		creasePattern = aCreasePattern;
		this.pointEps = pointEps;
	}

	/**
	 * @return creasePattern
	 */
	public CreasePattern getCreasePattern() {
		return creasePattern;
	}

	/**
	 * reset selection mark of all lines.
	 *
	 */
	public void resetSelectedOriLines() {
		selectionModifier.resetSelectedOriLines(creasePattern);
	}

	/**
	 * set {@code true} to selection mark of all lines except the lines of paper
	 * boundary.
	 *
	 */
	public void selectAllOriLines() {
		selectionModifier.selectAllOriLines(creasePattern);
	}

	/**
	 * count how many lines are selected.
	 *
	 * @return
	 */
	public int countSelectedLines() {
		return selectionModifier.countSelectedLines(creasePattern);
	}

	/**
	 *
	 */
	public void removeSelectedLines() {
		elementRemover.removeSelectedLines(creasePattern, getPointEps());
	}

	/**
	 * add given line to crease pattern. All lines which cross with given line
	 * are divided at the cross points (given line is also divided).
	 *
	 * @param inputLine
	 *            a line to be added
	 */
	public void addLine(final OriLine inputLine) {
		lineAdder.addLine(inputLine, creasePattern, getPointEps());
		elementRemover.removeMeaninglessVertices(creasePattern, getPointEps());
	}

	/**
	 * Add all given lines to crease pattern. Each line in {@code lines} and
	 * crease pattern is divided at the cross points. However, if the crossing
	 * lines are both in the same {@code lines} or crease pattern, such a
	 * division won't be done.
	 *
	 * @param lines
	 */
	public void addLines(final Collection<OriLine> lines) {
		lineAdder.addAll(lines, creasePattern, getPointEps());
		elementRemover.removeMeaninglessVertices(creasePattern, getPointEps());
	}

	/**
	 * add mirrored lines
	 *
	 * @param baseLine
	 *            a line to be the axis of symmetry
	 * @param lines
	 *            lines to be mirrored
	 */
	public void mirrorCopyBy(final OriLine baseLine,
			final Collection<OriLine> lines) {
		Collection<OriLine> copiedLines = lineMirror.createMirroredLines(baseLine, lines);

		addLines(copiedLines);
	}

	/**
	 * remove given line from the crease pattern.
	 *
	 * @param l
	 *            the line to be removed
	 */
	public void removeLine(final OriLine l) {
		elementRemover.removeLine(l, creasePattern, getPointEps());
	}

	/**
	 * remove all given lines from the crease pattern.
	 *
	 * @param lines
	 *            to be removed
	 */
	public void removeLines(final Collection<OriLine> lines) {
		elementRemover.removeLines(lines, creasePattern, getPointEps());
	}

	/**
	 * remove given vertex from the crease pattern.
	 *
	 * @param v
	 *            the vertex to be removed
	 */
	public void removeVertex(
			final Vector2d v) {
		elementRemover.removeVertex(v, creasePattern, getPointEps());
	}

	/**
	 * add vertex on a line
	 *
	 * @param line
	 *            the line on which the new vertex will be added
	 * @param v
	 *            the vertex to be added
	 * @return true if the vertex is added.
	 */
	public boolean addVertexOnLine(
			final OriLine line, final Vector2d v) {
		Collection<OriLine> dividedLines = lineDivider.divideLine(line, v,
				getPointEps());

		if (dividedLines.isEmpty()) {
			return false;
		}
		elementRemover.removeLine(line, creasePattern, getPointEps());

		lineAdder.addAll(dividedLines, creasePattern, getPointEps());

		return true;
	}

	/**
	 * add three inner lines of rabbit-ear molecule for given triangle
	 *
	 * @param v0
	 *            the vertex of the triangle
	 * @param v1
	 *            the vertex of the triangle
	 * @param v2
	 *            the vertex of the triangle
	 * @param lineType
	 *            the type of the new lines
	 */
	public void addTriangleDivideLines(
			final Vector2d v0, final Vector2d v1, final Vector2d v2, final OriLine.Type lineType) {
		Vector2d c = GeomUtil.getIncenter(v0, v1, v2);
		addLines(List.of(
				new OriLine(c, v0, lineType),
				new OriLine(c, v1, lineType),
				new OriLine(c, v2, lineType)));
	}

	/**
	 * add a bisector line from v1 to given line.
	 *
	 * @param v0
	 *            the end point of a line incident to the angle point
	 * @param v1
	 *            the vertex of the angle
	 * @param v2
	 *            the end point of a line incident to the angle point
	 * @param l
	 *            a line which will cross the bisector line. the cross point
	 *            will be the end point of the bisector line.
	 * @param lineType
	 *            the type of the bisector line
	 */
	public void addBisectorLine(
			final Vector2d v0, final Vector2d v1, final Vector2d v2,
			final OriLine l, final OriLine.Type lineType) {
		OriLine bisector = bisectorFactory.create(v0, v1, v2, l, lineType);

		addLine(bisector);
	}

	/**
	 * change type of given line.
	 *
	 * @param l
	 *            the line to be changed
	 * @param from
	 *            the type before change.
	 * @param to
	 *            the type after change.
	 */
	public void alterLineType(
			final OriLine l, final TypeForChange from, final TypeForChange to) {
		typeChanger.alterLineType(l, creasePattern, from, to, getPointEps());
	}

	/**
	 * change type of given lines.
	 *
	 * @param lines
	 *            the lines to be changed
	 * @param from
	 *            the type before change.
	 * @param to
	 *            the type after change.
	 */
	public void alterLineTypes(
			final Collection<OriLine> lines, final TypeForChange from, final TypeForChange to) {
		typeChanger.alterLineTypes(lines, creasePattern, from, to, getPointEps());
	}

	/**
	 * v1-v2 is the symmetry line, v0-v1 is the subject to be copied.
	 *
	 * @param v0
	 *            the end point of subject line not connected to the symmetry
	 *            line
	 * @param v1
	 *            the connecting point of the subject line and the symmetry line
	 * @param v2
	 *            the end point of symmetry line not connected to the subject
	 *            line
	 * @param lineType
	 *            the type of the symmetric line ({@link OriLine.Type#VALLEY}
	 *            etc.)
	 *
	 * @return true if line is added
	 */
	public boolean addSymmetricLine(
			final Vector2d v0, final Vector2d v1, final Vector2d v2, final OriLine.Type lineType) {
		OriLine symmetricLine;
		try {
			symmetricLine = symmetricFactory.createSymmetricLine(
					v0, v1, v2, creasePattern, lineType, getPointEps());
		} catch (PainterCommandFailedException comEx) {
			return false;
		}

		addLine(symmetricLine);

		return true;
	}

	/**
	 * add possible rebouncing of the fold.
	 *
	 * @param v0
	 *            the end point of subject line not connected to the symmetry
	 *            line
	 * @param v1
	 *            the connecting point of the subject line and the symmetry line
	 * @param v2
	 *            the end point of symmetry line not connected to the subject
	 *            line
	 * @param lineType
	 *            the type of the symmetric lines
	 * @return true if lines are added
	 */
	public boolean addSymmetricLineAutoWalk(
			final Vector2d v0, final Vector2d v1, final Vector2d v2,
			final OriLine.Type lineType) {
		Collection<OriLine> autoWalkLines;
		try {
			autoWalkLines = symmetricFactory.createSymmetricLineAutoWalk(
					v0, v1, v2, creasePattern, lineType, getPointEps());

		} catch (PainterCommandFailedException comEx) {
			return false;
		}
		addLines(autoWalkLines);

		return true;
	}

	/**
	 * add copy of selected lines with a rotation around specified center point.
	 * For a line l, this method creates rotatedLine(l, angleDeg * i) for i = 1
	 * ... repetitionCount.
	 *
	 * @param cx
	 *            x of center
	 * @param cy
	 *            y of center
	 * @param angleDeg
	 *            amount of rotation in degrees
	 * @param repetitionCount
	 * @param selectedLines
	 *            lines to be copied
	 *
	 */
	public void copyWithRotation(
			final double cx, final double cy, final double angleDeg, final int repetitionCount,
			final Collection<OriLine> selectedLines) {
		Collection<OriLine> copiedLines = rotationFactory.createRotatedLines(
				cx, cy, angleDeg, repetitionCount,
				selectedLines, creasePattern, getPointEps());

		addLines(copiedLines);
	}

	/**
	 * add copy of selected lines with tiling.
	 *
	 * @param row
	 *            the count of tiles on x coordinate
	 * @param col
	 *            the count of tiles on y coordinate
	 * @param interX
	 *            interval length of x coordinate
	 * @param interY
	 *            interval length of y coordinate
	 * @param selectedLines
	 *            lines to be copied
	 */
	public void copyWithTiling(
			final int row, final int col, final double interX, final double interY,
			final Collection<OriLine> selectedLines) {
		Collection<OriLine> copiedLines = tileFactory.createTiledLines(
				row, col, interX, interY,
				selectedLines, creasePattern, getPointEps());

		addLines(copiedLines);
	}

	/**
	 * add copy of selected lines as the paper is filled out.
	 *
	 * @param selectedLines
	 */
	public void fillUp(final Collection<OriLine> selectedLines) {
		Collection<OriLine> copiedLines = tileFactory.createFullyTiledLines(
				selectedLines, creasePattern,
				creasePattern.getPaperSize(), getPointEps());

		addLines(copiedLines);
	}

	public double getPointEps() {
		return pointEps;
	}

	public void clear() {
		var toRemove = creasePattern.stream().filter(Predicate.not(OriLine::isBoundary)).toList();

		removeLines(toRemove);
	}
}