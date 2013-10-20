package oripa.paint.creasepattern;

import java.util.Collection;

import javax.vecmath.Vector2d;

import oripa.geom.GeomUtil;
import oripa.paint.core.PaintConfig;
import oripa.paint.creasepattern.command.BisectorFactory;
import oripa.paint.creasepattern.command.ElementRemover;
import oripa.paint.creasepattern.command.LineAdder;
import oripa.paint.creasepattern.command.LineDivider;
import oripa.paint.creasepattern.command.LineMirror;
import oripa.paint.creasepattern.command.LineSelectionModifier;
import oripa.paint.creasepattern.command.LineTypeChanger;
import oripa.paint.creasepattern.command.PainterCommandFailedException;
import oripa.paint.creasepattern.command.SymmetricLineFactory;
import oripa.paint.creasepattern.command.TypeForChange;
import oripa.value.OriLine;

public class Painter {

	/**
	 * reset selection mark of all lines in given collection.
	 * @param creasePattern
	 */
	public void resetSelectedOriLines(Collection<OriLine> creasePattern) {
		LineSelectionModifier modifier = new LineSelectionModifier();
		modifier.resetSelectedOriLines(creasePattern);
	}

	/**
	 * set  {@code true} to selection mark of all lines in given collection.
	 * @param creasePattern
	 */
	public void selectAllOriLines(Collection<OriLine> creasePattern) {
		LineSelectionModifier modifier = new LineSelectionModifier();
		modifier.selectAllOriLines(creasePattern);
	}

	/**
	 * 
	 * @param creasePattern
	 */
	public void removeSelectedLines(
			Collection<OriLine> creasePattern) {

		ElementRemover remover = new ElementRemover();
		remover.removeSelectedLines(creasePattern);
	}

	
	/**
	 * Adds a new OriLine, also searching for intersections with others 
	 * that would cause their mutual division.
	 * @param inputLine      a line to be added
	 * @param creasePattern  destination of inputLine
	 */
	public void addLine(
			OriLine inputLine, Collection<OriLine> creasePattern) {

		LineAdder lineAdder = new LineAdder();
		lineAdder.addLine(inputLine, creasePattern);		
	}

	/**
	 * 
	 * 
	 * @param baseLine       a line to be the axis of symmetry
	 * @param lines          lines to be mirrored
	 * @param creasePattern  destination of mirrored lines
	 */
	public void mirrorCopyBy(OriLine baseLine,
			Collection<OriLine> lines, Collection<OriLine> creasePattern) {

		LineMirror mirror = new LineMirror();
		Collection<OriLine> copiedLines = mirror.createMirroredLines(baseLine, lines);

		LineAdder adder = new LineAdder();
		adder.addAll(copiedLines, creasePattern);
	}    


	/**
	 * remove given line from the collection.
	 * @param l
	 * @param creasePattern
	 */
	public void removeLine(
			OriLine l, Collection<OriLine> creasePattern) {
		
		ElementRemover remover = new ElementRemover();
		remover.removeLine(l, creasePattern);
	}

	/**
	 * remove given vertex from the collection.
	 * @param v
	 * @param creasePattern
	 */
	public void removeVertex(
			Vector2d v, Collection<OriLine> creasePattern) {

		ElementRemover remover = new ElementRemover();
		remover.removeVertex(v, creasePattern);
	}

	/**
	 * add vertex on a line
	 * @param line
	 * @param v
	 * @param creasePattern
	 * @param paperSize
	 * @return true if the vertex is added.
	 */
	public boolean addVertexOnLine(
			OriLine line, Vector2d v,
			Collection<OriLine> creasePattern, double paperSize) {

		LineDivider divider = new LineDivider();
		return divider.divideLineInCollection(line, v, creasePattern, paperSize);
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
			Vector2d v0, Vector2d v1, Vector2d v2,
			Collection<OriLine> creasePattern) {

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
	 * @param v0
	 * @param v1
	 * @param creasePattern
	 * @param paperSize
	 */
	public void addPBisector(
			Vector2d v0, Vector2d v1,
			Collection<OriLine> creasePattern, double paperSize) {
		
		BisectorFactory factory = new BisectorFactory();
		OriLine bisector =
				factory.createPerpendicularBisector(v0, v1, paperSize);

		LineAdder adder = new LineAdder();
		adder.addLine(bisector, creasePattern);
	}

	/**
	 * add a bisector line from v1 to given line.
	 * @param v0
	 * @param v1
	 * @param v2
	 * @param l
	 * @param creasePattern
	 */
	public void addBisectorLine(
			Vector2d v0, Vector2d v1, Vector2d v2,
			OriLine l,
			Collection<OriLine> creasePattern) {
		
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
			OriLine l, TypeForChange from,  TypeForChange to,
			Collection<OriLine> creasePattern) {

		LineTypeChanger changer = new LineTypeChanger();
		changer.alterLineType(l, creasePattern, from, to);
	}


	/**
	 * v1-v2 is the symmetry line, v0-v1 is the subject to be copied. 
	 * @param v0
	 * @param v1
	 * @param v2
	 * @param creasePattern
	 * @throws PainterCommandFailedException 
	 */
	public void addSymmetricLine(
			Vector2d v0, Vector2d v1, Vector2d v2,
			Collection<OriLine> creasePattern) throws PainterCommandFailedException {

		SymmetricLineFactory factory = new SymmetricLineFactory();
		OriLine symmetricLine =
				factory.createSymmetricLine(v0, v1, v2, creasePattern);

		LineAdder adder = new LineAdder();
		adder.addLine(symmetricLine, creasePattern);
	}

	/**
	 * add possible rebouncing of the fold.
	 * 
	 * @param v0 terminal point of the line to be copied
	 * @param v1 connecting point of symmetry line and the line to be copied.
	 * @param v2 terminal point of symmetry line
	 * @param startV
	 * @param creasePattern
	 * @throws PainterCommandFailedException 
	 */
	public void addSymmetricLineAutoWalk(
			Vector2d v0, Vector2d v1, Vector2d v2, Vector2d startV,
			Collection<OriLine> creasePattern) throws PainterCommandFailedException {

		SymmetricLineFactory factory = new SymmetricLineFactory();

		Collection<OriLine> autoWalkLines =
				factory.createSymmetricLineAutoWalk(
						v0, v1, v2, startV, creasePattern);

		LineAdder adder = new LineAdder();
		adder.addAll(autoWalkLines, creasePattern);
	}
}
