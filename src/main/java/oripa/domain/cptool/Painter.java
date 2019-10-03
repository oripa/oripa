package oripa.domain.cptool;

import java.util.Collection;

import javax.vecmath.Vector2d;

import oripa.domain.creasepattern.CreasePatternInterface;
import oripa.domain.paint.core.PaintConfig;
import oripa.geom.GeomUtil;
import oripa.value.OriLine;

/**
 * A tool to modify crease pattern (or line collection)
 * 
 * @author Koji
 * 
 */
public class Painter {
	// FIXME all methods should return success/failure

	private final CreasePatternInterface creasePattern;

	public Painter(final CreasePatternInterface aCreasePattern) {
		creasePattern = aCreasePattern;
	}

	/**
	 * @return creasePattern
	 */
	public CreasePatternInterface getCreasePattern() {
		return creasePattern;
	}

	/**
	 * reset selection mark of all lines in given collection.
	 * 
	 * @param creasePattern
	 */
	public void resetSelectedOriLines() {
		LineSelectionModifier modifier = new LineSelectionModifier();
		modifier.resetSelectedOriLines(creasePattern);
	}

	/**
	 * set {@code true} to selection mark of all lines in given collection.
	 * 
	 */
	public void selectAllOriLines() {
		LineSelectionModifier modifier = new LineSelectionModifier();
		modifier.selectAllOriLines(creasePattern);
	}

	/**
	 * count how many lines are selected.
	 * 
	 * @return
	 */
	public int countSelectedLines() {
		LineSelectionModifier modifier = new LineSelectionModifier();
		return modifier.countSelectedLines(creasePattern);
	}

	/**
	 * 
	 * @param creasePattern
	 */
	public void removeSelectedLines() {

		ElementRemover remover = new ElementRemover();
		remover.removeSelectedLines(creasePattern);
	}

	/**
	 * add given line to crease pattern. All lines which cross with given line
	 * are divided at the cross points (given line is also divided).
	 * 
	 * @param inputLine
	 *            a line to be added
	 * @param creasePattern
	 *            destination of inputLine
	 */
	public void addLine(
			final OriLine inputLine) {

		LineAdder lineAdder = new LineAdder();
		lineAdder.addLine(inputLine, creasePattern);
	}

	public void pasteLines(
			final Collection<OriLine> lines) {

		LinePaster paster = new LinePaster();
		paster.paste(lines, creasePattern);
	}

	/**
	 * add mirrored lines
	 * 
	 * @param baseLine
	 *            a line to be the axis of symmetry
	 * @param lines
	 *            lines to be mirrored
	 * @param creasePattern
	 *            destination of mirrored lines
	 */
	public void mirrorCopyBy(final OriLine baseLine,
			final Collection<OriLine> lines) {

		LineMirror mirror = new LineMirror();
		Collection<OriLine> copiedLines = mirror.createMirroredLines(baseLine, lines);

		LineAdder adder = new LineAdder();
		adder.addAll(copiedLines, creasePattern);
	}

	/**
	 * remove given line from the collection.
	 * 
	 * @param l
	 * @param creasePattern
	 */
	public void removeLine(
			final OriLine l) {

		ElementRemover remover = new ElementRemover();
		remover.removeLine(l, creasePattern);
	}

	/**
	 * remove given vertex from the collection.
	 * 
	 * @param v
	 * @param creasePattern
	 */
	public void removeVertex(
			final Vector2d v) {

		ElementRemover remover = new ElementRemover();
		remover.removeVertex(v, creasePattern);
	}

	/**
	 * add vertex on a line
	 * 
	 * @param line
	 * @param v
	 * @param creasePattern
	 * @param paperSize
	 * @return true if the vertex is added.
	 */
	public boolean addVertexOnLine(
			final OriLine line, final Vector2d v) {

		LineDivider divider = new LineDivider();
		Collection<OriLine> dividedLines =
				divider.divideLineInCollection(line, v, creasePattern, creasePattern.getPaperSize());

		if (dividedLines == null) {
			return false;
		}

		ElementRemover remover = new ElementRemover();
		remover.removeLine(line, creasePattern);

		LineAdder adder = new LineAdder();
		adder.addAll(dividedLines, creasePattern);

		return true;
	}

	/**
	 * add three inner lines of rabbit-ear molecule for given triangle
	 * 
	 * @param v0
	 * @param v1
	 * @param v2
	 * @param creasePattern
	 */
	public void addTriangleDivideLines(
			final Vector2d v0, final Vector2d v1, final Vector2d v2) {

		Vector2d c = GeomUtil.getIncenter(v0, v1, v2);
		if (c == null) {
			System.out.print("Failed to calculate incenter of the triangle");
		}
		LineAdder adder = new LineAdder();
		adder.addLine(new OriLine(c, v0, PaintConfig.inputLineType), creasePattern);
		adder.addLine(new OriLine(c, v1, PaintConfig.inputLineType), creasePattern);
		adder.addLine(new OriLine(c, v2, PaintConfig.inputLineType), creasePattern);
	}

	/**
	 * add perpendicular bisector line between v0 and v1
	 * 
	 * @param v0
	 * @param v1
	 * @param creasePattern
	 * @param paperSize
	 */
	public void addPBisector(
			final Vector2d v0, final Vector2d v1) {

		BisectorFactory factory = new BisectorFactory();
		OriLine bisector =
				factory.createPerpendicularBisector(v0, v1, creasePattern.getPaperSize());

		LineAdder adder = new LineAdder();
		adder.addLine(bisector, creasePattern);
	}

	/**
	 * add a bisector line from v1 to given line.
	 * 
	 * @param v0
	 * @param v1
	 * @param v2
	 * @param l
	 * @param creasePattern
	 */
	public void addBisectorLine(
			final Vector2d v0, final Vector2d v1, final Vector2d v2,
			final OriLine l) {

		BisectorFactory factory = new BisectorFactory();
		OriLine bisector =
				factory.createAngleBisectorLine(v0, v1, v2, l);

		LineAdder adder = new LineAdder();
		adder.addLine(bisector, creasePattern);

	}

	/**
	 * change type of given line.
	 * 
	 * @param l
	 * @param from
	 * @param to
	 * @param creasePattern
	 */
	public void alterLineType(
			final OriLine l, final TypeForChange from, final TypeForChange to) {

		LineTypeChanger changer = new LineTypeChanger();
		changer.alterLineType(l, creasePattern, from, to);
	}

	/**
	 * v1-v2 is the symmetry line, v0-v1 is the subject to be copied.
	 * 
	 * @param v0
	 * @param v1
	 * @param v2
	 * @param creasePattern
	 * 
	 * @return true if line is added
	 * @throws PainterCommandFailedException
	 */
	public boolean addSymmetricLine(
			final Vector2d v0, final Vector2d v1, final Vector2d v2) {

		SymmetricLineFactory factory = new SymmetricLineFactory();
		OriLine symmetricLine;
		try {
			symmetricLine = factory.createSymmetricLine(v0, v1, v2, creasePattern);
		} catch (PainterCommandFailedException comEx) {
			return false;
		}

		LineAdder adder = new LineAdder();
		adder.addLine(symmetricLine, creasePattern);

		return true;
	}

	/**
	 * add possible rebouncing of the fold.
	 * 
	 * @param v0
	 *            terminal point of the line to be copied
	 * @param v1
	 *            connecting point of symmetry line and the line to be copied.
	 * @param v2
	 *            terminal point of symmetry line
	 * @param startV
	 * @param creasePattern
	 * 
	 * @return true if line is added
	 * @throws PainterCommandFailedException
	 */
	public boolean addSymmetricLineAutoWalk(
			final Vector2d v0, final Vector2d v1, final Vector2d v2, final Vector2d startV) {

		SymmetricLineFactory factory = new SymmetricLineFactory();

		Collection<OriLine> autoWalkLines;
		try {
			autoWalkLines =
					factory.createSymmetricLineAutoWalk(
							v0, v1, v2, startV, creasePattern);

		} catch (PainterCommandFailedException comEx) {
			return false;
		}
		LineAdder adder = new LineAdder();
		adder.addAll(autoWalkLines, creasePattern);

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
	 * @param creasePattern
	 * @param paperSize
	 * 
	 * @return rotated lines
	 */
	// TODO a collection of selected line should be a parameter as like mirror
	// copy.
	public void copyWithRotation(
			final double cx, final double cy, final double angleDeg, final int repetitionCount) {

		RotatedLineFactory factory = new RotatedLineFactory();

		Collection<OriLine> copiedLines = factory.createRotatedLines(
				cx, cy, angleDeg, repetitionCount, creasePattern, creasePattern.getPaperSize());

		LineSelectionModifier selectionModifier = new LineSelectionModifier();
		selectionModifier.resetSelectedOriLines(creasePattern);

		LineAdder adder = new LineAdder();
		adder.addAll(copiedLines, creasePattern);

	}

	/**
	 * add copy of selected lines with tiling.
	 * 
	 * @param row
	 * @param col
	 * @param interX
	 * @param interY
	 * @param creasePattern
	 * @param paperSize
	 */
	public void copyWithTiling(
			final int row, final int col, final double interX, final double interY) {

		TiledLineFactory factory = new TiledLineFactory();

		Collection<OriLine> copiedLines =
				factory.createTiledLines(
						row, col, interX, interY,
						creasePattern, creasePattern.getPaperSize());

		LineAdder adder = new LineAdder();
		adder.addAll(copiedLines, creasePattern);
	}

	/**
	 * add copy of selected lines as the paper is filled out.
	 * 
	 * @param lines
	 * @param creasePattern
	 * @param paperSize
	 */
	public void fillOut(
			final Collection<OriLine> lines) {

		TiledLineFactory factory = new TiledLineFactory();

		Collection<OriLine> copiedLines =
				factory.createFullyTiledLines(lines, creasePattern, creasePattern.getPaperSize());

		LineAdder adder = new LineAdder();
		adder.addAll(copiedLines, creasePattern);

	}
}