package oripa.domain.paint;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Vector2d;

import oripa.domain.cptool.Painter;
import oripa.domain.creasepattern.CreasePatternInterface;
import oripa.value.OriLine;

class PaintContext implements PaintContextInterface {

	private CreasePatternInterface creasePattern;
	private final CreasePatternUndoerInterface undoer = new CreasePatternUndoer(this);

	private final LinkedList<Vector2d> pickedVertices = new LinkedList<>();

	private final LinkedList<OriLine> pickedLines = new LinkedList<>();
	private boolean isPasting = false;

	private Vector2d candidateVertexToPick = new Vector2d();
	private OriLine candidateLineToPick = new OriLine();

	private boolean gridVisible = true;
	private int gridDivNum;
	private double scale;

	private ArrayList<Vector2d> gridPoints;

	private boolean vertexVisible = true;
	private boolean mvLineVisible = true;
	private boolean auxLineVisible = true;
	private boolean crossLineVisible = false;

	private boolean missionCompleted = false;

	private OriLine.Type lineTypeOfNewLines;

	private Point2D.Double mousePoint;

	public PaintContext() {
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.domain.paint.PaintContextInterface#getLogicalMousePoint()
	 */
	@Override
	public synchronized Point2D.Double getLogicalMousePoint() {
		return mousePoint;
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.domain.paint.PaintContextInterface#isPasting()
	 */
	@Override
	public boolean isPasting() {
		return isPasting;
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.domain.paint.PaintContextInterface#startPasting()
	 */
	@Override
	public void startPasting() {
		this.isPasting = true;
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.domain.paint.PaintContextInterface#finishPasting()
	 */
	@Override
	public void finishPasting() {
		this.isPasting = false;
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.domain.paint.PaintContextInterface#setLogicalMousePoint
	 * (java.awt.geom.Point2D.Double)
	 */
	@Override
	public synchronized void setLogicalMousePoint(final Point2D.Double logicalPoint) {
		this.mousePoint = logicalPoint;
	}

	@Override
	public void setMVLineVisible(final boolean visible) {
		mvLineVisible = visible;
	}

	@Override
	public boolean isMVLineVisible() {
		return mvLineVisible;
	}

	@Override
	public boolean isVertexVisible() {
		return vertexVisible;
	}

	@Override
	public void setVertexVisible(final boolean visible) {
		vertexVisible = visible;
	}

	@Override
	public void setAuxLineVisible(final boolean visible) {
		auxLineVisible = visible;
	}

	@Override
	public boolean isAuxLineVisible() {
		return auxLineVisible;
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.domain.paint.PaintContextInterface#getGridDivNum()
	 */
	@Override
	public int getGridDivNum() {
		return gridDivNum;
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.domain.paint.PaintContextInterface#setGridDivNum(int)
	 */
	@Override
	public void setGridDivNum(final int divNum) {
		gridDivNum = divNum;
		updateGrids();
	}

	@Override
	public void updateGrids() {
		gridPoints = new ArrayList<>();
		double paperSize = creasePattern.getPaperSize();

		double step = paperSize / gridDivNum;
		for (int ix = 0; ix < gridDivNum + 1; ix++) {
			for (int iy = 0; iy < gridDivNum + 1; iy++) {
				double x = -paperSize / 2 + step * ix;
				double y = -paperSize / 2 + step * iy;

				gridPoints.add(new Vector2d(x, y));
			}
		}
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.domain.paint.PaintContextInterface#updateGrids(int)
	 */
	@Override
	public Collection<Vector2d> getGrids() {

		return gridPoints;
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.domain.paint.PaintContextInterface#clear(boolean)
	 */
	@Override
	public void clear(final boolean unselect) {

		if (unselect) {
			pickedLines.stream().forEach(l -> l.selected = false);
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
	 * @see oripa.domain.paint.PaintContextInterface#isMissionCompleted()
	 */
	@Override
	public boolean isMissionCompleted() {
		return missionCompleted;
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.domain.paint.PaintContextInterface#setMissionCompleted
	 * (boolean)
	 */
	@Override
	public void setMissionCompleted(final boolean missionCompleted) {
		this.missionCompleted = missionCompleted;
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.domain.paint.PaintContextInterface#getVertices()
	 */
	@Override
	public List<Vector2d> getPickedVertices() {
		return Collections.unmodifiableList(pickedVertices);
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.domain.paint.PaintContextInterface#getLines()
	 */
	@Override
	public List<OriLine> getPickedLines() {
		return Collections.unmodifiableList(pickedLines);
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.domain.paint.PaintContextInterface#getLine(int)
	 */
	@Override
	public OriLine getLine(final int index) {
		return pickedLines.get(index);
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.domain.paint.PaintContextInterface#getVertex(int)
	 */
	@Override
	public Vector2d getVertex(final int index) {
		return pickedVertices.get(index);
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.domain.paint.PaintContextInterface#pushVertex(javax.vecmath
	 * .Vector2d)
	 */
	@Override
	public void pushVertex(final Vector2d picked) {
		pickedVertices.push(picked);
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.domain.paint.PaintContextInterface#pushLine(oripa.value
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
	 * @see oripa.domain.paint.PaintContextInterface#popVertex()
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
	 * @see oripa.domain.paint.PaintContextInterface#popLine()
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
	 * @see oripa.domain.paint.PaintContextInterface#removeLine(oripa.value
	 * .OriLine)
	 */
	@Override
	public boolean removeLine(final OriLine line) {

		return pickedLines.remove(line);
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.domain.paint.PaintContextInterface#peekVertex()
	 */
	@Override
	public Vector2d peekVertex() {
		return pickedVertices.peek();
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.domain.paint.PaintContextInterface#peekLine()
	 */
	@Override
	public OriLine peekLine() {
		return pickedLines.peek();
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.domain.paint.PaintContextInterface#getLineCount()
	 */
	@Override
	public int getLineCount() {
		return pickedLines.size();
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.domain.paint.PaintContextInterface#getVertexCount()
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
	 * @return whether grid is visible or not.
	 */
	@Override
	public boolean isGridVisible() {
		return gridVisible;
	}

	/**
	 * @param gridVisible
	 *            Sets gridVisible
	 */
	@Override
	public void setGridVisible(final boolean gridVisible) {
		this.gridVisible = gridVisible;
	}

	@Override
	public void setCrossLineVisible(final boolean visible) {
		crossLineVisible = visible;
	}

	@Override
	public boolean isCrossLineVisible() {
		return crossLineVisible;
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

	@Override
	public void setLineTypeOfNewLines(final OriLine.Type lineType) {
		lineTypeOfNewLines = lineType;
	}

	@Override
	public OriLine.Type getLineTypeOfNewLines() {
		return lineTypeOfNewLines;
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
	 * @see oripa.domain.paint.PaintContextInterface#getUndoer()
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
		return creasePattern;
	}

	/*
	 * (non Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PaintContext: #line=" + pickedLines.size() +
				", #vertex=" + pickedVertices.size() +
				", #undoStack=" + undoer.size();
	}
}
