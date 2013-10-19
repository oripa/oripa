package oripa.paint.creasepattern;

import java.util.Collection;

import javax.vecmath.Vector2d;

import oripa.geom.GeomUtil;
import oripa.geom.Line;
import oripa.paint.core.PaintConfig;
import oripa.paint.creasepattern.command.ElementRemover;
import oripa.paint.creasepattern.command.LineAdder;
import oripa.paint.creasepattern.command.LineMirror;
import oripa.paint.creasepattern.command.LineTypeChanger;
import oripa.paint.creasepattern.command.TypeForChange;
import oripa.resource.Constants;
import oripa.value.OriLine;

public class Painter {

	/**
	 * reset selection mark of all lines in given collection.
	 * @param creasePattern
	 */
	public void resetSelectedOriLines(Collection<OriLine> creasePattern) {
		for (OriLine line : creasePattern) {
			line.selected = false;
		}
	}

	/**
	 * set  {@code true} to selection mark of all lines in given collection.
	 * @param creasePattern
	 */
	public void selectAllOriLines(Collection<OriLine> creasePattern) {
		for (OriLine l : creasePattern) {
			if (l.typeVal != OriLine.TYPE_CUT) {
				l.selected = true;
			}
		}
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

		for (OriLine line : copiedLines) {
			addLine(line, creasePattern);
		}

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
		
		// Normally you don't want to add a vertex too close to the end of the line
		if (GeomUtil.Distance(line.p0, v) < paperSize * 0.001
				|| GeomUtil.Distance(line.p1, v) < paperSize * 0.001) {
			return false;
		}

		OriLine l0 = new OriLine(line.p0, v, line.typeVal);
		OriLine l1 = new OriLine(v, line.p1, line.typeVal);
		creasePattern.remove(line);
		creasePattern.add(l0);
		creasePattern.add(l1);

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
			Vector2d v0, Vector2d v1, Vector2d v2,
			Collection<OriLine> creasePattern) {

		Vector2d c = GeomUtil.getIncenter(v0, v1, v2);
		if (c == null) {
			System.out.print("Failed to calculate incenter of the triangle");
		}
		addLine(new OriLine(c, v0, PaintConfig.inputLineType), creasePattern);
		addLine(new OriLine(c, v1, PaintConfig.inputLineType), creasePattern);
		addLine(new OriLine(c, v2, PaintConfig.inputLineType), creasePattern);
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

		Vector2d cp = new Vector2d(v0);
		cp.add(v1);
		cp.scale(0.5);

		Vector2d dir = new Vector2d();
		dir.sub(v0, v1);
		double tmp = dir.y;
		dir.y = -dir.x;
		dir.x = tmp;
		dir.scale(Constants.DEFAULT_PAPER_SIZE * 8);

		OriLine bisector = new OriLine(
				cp.x - dir.x, cp.y - dir.y,
				cp.x + dir.x, cp.y + dir.y, PaintConfig.inputLineType);

		GeomUtil.clipLine(bisector, paperSize / 2);
		addLine(bisector, creasePattern);
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
		
		Vector2d dir = GeomUtil.getBisectorVec(v0, v1, v2);
		Vector2d cp = GeomUtil.getCrossPoint(
				new Line(l.p0, new Vector2d(l.p1.x - l.p0.x, l.p1.y - l.p0.y)),
				new Line(v1, dir));

		OriLine bisector = new OriLine(v1, cp, PaintConfig.inputLineType);
		addLine(bisector, creasePattern);

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
}
