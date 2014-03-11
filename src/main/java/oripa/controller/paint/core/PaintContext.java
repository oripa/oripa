package oripa.controller.paint.core;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Vector2d;

import oripa.controller.paint.CreasePatternUndoer;
import oripa.controller.paint.CreasePatternUndoerInterface;
import oripa.controller.paint.PaintContextInterface;
import oripa.domain.cptool.Painter;
import oripa.domain.creasepattern.CreasePatternInterface;
import oripa.value.OriLine;

public class PaintContext implements PaintContextInterface {

	private static PaintContextInterface instance = null;

	public static PaintContextInterface getInstance() {
		if (instance == null) {
			instance = new PaintContext();
		}

		return instance;
	}

	private PaintContext() {
	}

//--------------------------------------------------

	private CreasePatternInterface creasePattern;
	private final CreasePatternUndoerInterface undoer = new CreasePatternUndoer(this);

	private final LinkedList<Vector2d> pickedVertices = new LinkedList<>();

	private final LinkedList<OriLine> pickedLines = new LinkedList<>();
	private boolean isPasting = false;

	private Vector2d candidateVertexToPick = new Vector2d();
	private OriLine candidateLineToPick = new OriLine();

	private boolean gridVisible = true;
	private double scale;

	private ArrayList<Vector2d> gridPoints;

	private boolean missionCompleted = false;

	private Point2D.Double mousePoint;

	/*
	 * (non Javadoc)
	 * 
	 * @see
	 * oripa.controller.paint.core.PaintContextInterface#getLogicalMousePoint()
	 */
	@Override
	public Point2D.Double getLogicalMousePoint() {
		return mousePoint;
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see oripa.controller.paint.core.PaintContextInterface#isPasting()
	 */
	@Override
	public boolean isPasting() {
		return isPasting;
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see oripa.controller.paint.core.PaintContextInterface#startPasting()
	 */
	@Override
	public void startPasting() {
		this.isPasting = true;
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see oripa.controller.paint.core.PaintContextInterface#finishPasting()
	 */
	@Override
	public void finishPasting() {
		this.isPasting = false;
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see
	 * oripa.controller.paint.core.PaintContextInterface#setLogicalMousePoint
	 * (java.awt.geom.Point2D.Double)
	 */
	@Override
	public void setLogicalMousePoint(final Point2D.Double logicalPoint) {
		this.mousePoint = logicalPoint;
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see oripa.controller.paint.core.PaintContextInterface#set(double,
	 * boolean)
	 */
	@Override
	public void setDisplayConfig(final double scale, final boolean dispGrid) {
		this.scale = scale;
		this.gridVisible = dispGrid;
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see oripa.controller.paint.core.PaintContextInterface#updateGrids(int)
	 */
	@Override
	public Collection<Vector2d> updateGrids(final int gridDivNum) {
		gridPoints = new ArrayList<>();
		double paperSize = creasePattern.getPaperSize();

		double step = paperSize / gridDivNum;
		for (int ix = 0; ix < PaintConfig.gridDivNum + 1; ix++) {
			for (int iy = 0; iy < gridDivNum + 1; iy++) {
				double x = -paperSize / 2 + step * ix;
				double y = -paperSize / 2 + step * iy;

				gridPoints.add(new Vector2d(x, y));
			}
		}

		return gridPoints;
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see oripa.controller.paint.core.PaintContextInterface#clear(boolean)
	 */
	@Override
	public void clear(final boolean unselect) {

		if (unselect && pickedLines.isEmpty() == false) {
			for (OriLine l : pickedLines) {
				l.selected = false;
			}
		}

		pickedLines.clear();
		pickedVertices.clear();

		candidateLineToPick = null;
		candidateVertexToPick = null;

		missionCompleted = false;
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see
	 * oripa.controller.paint.core.PaintContextInterface#isMissionCompleted()
	 */
	@Override
	public boolean isMissionCompleted() {
		return missionCompleted;
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see
	 * oripa.controller.paint.core.PaintContextInterface#setMissionCompleted
	 * (boolean)
	 */
	@Override
	public void setMissionCompleted(final boolean missionCompleted) {
		this.missionCompleted = missionCompleted;
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see oripa.controller.paint.core.PaintContextInterface#getVertices()
	 */
	@Override
	public List<Vector2d> getPickedVertices() {
		return Collections.unmodifiableList(pickedVertices);
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see oripa.controller.paint.core.PaintContextInterface#getLines()
	 */
	@Override
	public List<OriLine> getPickedLines() {
		return Collections.unmodifiableList(pickedLines);
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see oripa.controller.paint.core.PaintContextInterface#getLine(int)
	 */
	@Override
	public OriLine getLine(final int index) {
		return pickedLines.get(index);
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see oripa.controller.paint.core.PaintContextInterface#getVertex(int)
	 */
	@Override
	public Vector2d getVertex(final int index) {
		return pickedVertices.get(index);
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see
	 * oripa.controller.paint.core.PaintContextInterface#pushVertex(javax.vecmath
	 * .Vector2d)
	 */
	@Override
	public void pushVertex(final Vector2d picked) {
		pickedVertices.push(picked);
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see
	 * oripa.controller.paint.core.PaintContextInterface#pushLine(oripa.value
	 * .OriLine)
	 */
	@Override
	public void pushLine(final OriLine picked) {
		// picked.selected = true;
		pickedLines.push(picked);
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see oripa.controller.paint.core.PaintContextInterface#popVertex()
	 */
	@Override
	public Vector2d popVertex() {
		if (pickedVertices.isEmpty()) {
			return null;
		}

		return pickedVertices.pop();
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see oripa.controller.paint.core.PaintContextInterface#popLine()
	 */
	@Override
	public OriLine popLine() {
		if (pickedLines.isEmpty()) {
			return null;
		}

		OriLine line = pickedLines.pop();
		line.selected = false;
		return line;
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see
	 * oripa.controller.paint.core.PaintContextInterface#removeLine(oripa.value
	 * .OriLine)
	 */
	@Override
	public boolean removeLine(final OriLine line) {

		return pickedLines.remove(line);
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see oripa.controller.paint.core.PaintContextInterface#peekVertex()
	 */
	@Override
	public Vector2d peekVertex() {
		return pickedVertices.peek();
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see oripa.controller.paint.core.PaintContextInterface#peekLine()
	 */
	@Override
	public OriLine peekLine() {
		return pickedLines.peek();
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see oripa.controller.paint.core.PaintContextInterface#getLineCount()
	 */
	@Override
	public int getLineCount() {
		return pickedLines.size();
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see oripa.controller.paint.core.PaintContextInterface#getVertexCount()
	 */
	@Override
	public int getVertexCount() {
		return pickedVertices.size();
	}

	/**
	 * @return a candidate vertex to pick
	 */
	@Override
	public Vector2d getCandidateVertexToPick() {
		return candidateVertexToPick;
	}

	/**
	 * @param candidate
	 *            Sets candidateVertexToPick
	 */
	@Override
	public void setCandidateVertexToPick(final Vector2d candidate) {
		this.candidateVertexToPick = candidate;
	}

	/**
	 * @return candidateLineToPick
	 */
	@Override
	public OriLine getCandidateLineToPick() {
		return candidateLineToPick;
	}

	/**
	 * @param candidate
	 *            Sets candidateLineToPick
	 */
	@Override
	public void setCandidateLineToPick(final OriLine candidate) {
		this.candidateLineToPick = candidate;
	}

	/**
	 * @return dispGrid
	 */
	@Override
	public boolean isGridVisible() {
		return gridVisible;
	}

	/**
	 * @param gridVisible
	 *            Sets gridVisibleS
	 */
	@Override
	public void setGridVisible(final boolean gridVisible) {
		this.gridVisible = gridVisible;
	}

	/**
	 * @return scale
	 */
	@Override
	public double getScale() {
		return scale;
	}

	/**
	 * @param scale
	 *            Sets scale
	 */
	@Override
	public void setScale(final double scale) {
		this.scale = scale;
	}

	/**
	 * returns a painter for current crease pattern instance.
	 * 
	 */
	@Override
	public Painter getPainter() {
		return new Painter(creasePattern);
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see oripa.controller.paint.PaintContextInterface#getUndoer()
	 */
	@Override
	public CreasePatternUndoerInterface creasePatternUndo() {
		return undoer;
	}

	@Override
	public void setCreasePattern(final CreasePatternInterface aCreasePattern) {
		creasePattern = aCreasePattern;
	}

	@Override
	public CreasePatternInterface getCreasePattern() {
		// TODO 自動生成されたメソッド・スタブ
		return creasePattern;
	}
}
