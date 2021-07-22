package oripa.domain.paint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Vector2d;

import oripa.domain.cptool.Painter;
import oripa.domain.creasepattern.CreasePattern;
import oripa.geom.RectangleDomain;
import oripa.value.OriLine;

class PaintContextImpl implements PaintContext {

	private CreasePattern creasePattern;
	private final CreasePatternUndoer undoer = new CreasePatternUndoerImpl(this);

	private final LinkedList<Vector2d> pickedVertices = new LinkedList<>();

	private final LinkedList<OriLine> pickedLines = new LinkedList<>();
	private boolean isPasting = false;

	private double scale;

	private Vector2d candidateVertexToPick = new Vector2d();
	private OriLine candidateLineToPick = new OriLine();

	private OriLine.Type lineTypeOfNewLines;

	private AngleStep angleStep;

	private int gridDivNum;
	private ArrayList<Vector2d> gridPoints;

	/*
	 * TODO: Rename for more general usage. snapPoints? assistPoints? something
	 * like that.
	 */
	private Collection<Vector2d> angleSnapCrossPoints = new ArrayList<Vector2d>();

	public PaintContextImpl() {
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

		angleSnapCrossPoints.clear();
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
		pickedVertices.addLast(picked);
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
		pickedLines.addLast(picked);
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

		return pickedVertices.removeLast();
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

		OriLine line = pickedLines.removeLast();
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
		return pickedVertices.peekLast();
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.domain.paint.PaintContextInterface#peekLine()
	 */
	@Override
	public OriLine peekLine() {
		return pickedLines.peekLast();
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
	public CreasePatternUndoer creasePatternUndo() {
		return undoer;
	}

	@Override
	public void setCreasePattern(final CreasePattern aCreasePattern) {
		creasePattern = aCreasePattern;
	}

	@Override
	public CreasePattern getCreasePattern() {
		return creasePattern;
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.domain.paint.PaintContextInterface#getCreasePatternDomain()
	 */
	@Override
	public RectangleDomain getPaperDomain() {
		return creasePattern.getPaperDomain();
	}

	/*
	 * (non Javadoc)
	 *
	 * @see
	 * oripa.domain.paint.PaintContextInterface#setAngleStep(oripa.domain.paint.
	 * AngleStep)
	 */
	@Override
	public void setAngleStep(final AngleStep step) {
		angleStep = step;
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.domain.paint.PaintContextInterface#getAngleStep()
	 */
	@Override
	public AngleStep getAngleStep() {
		return angleStep;
	}

	/*
	 * (non Javadoc)
	 *
	 * @see
	 * oripa.domain.paint.PaintContextInterface#setAngleSnapCrossPoints(java.
	 * util.Collection)
	 */
	@Override
	public void setAngleSnapCrossPoints(final Collection<Vector2d> points) {
		angleSnapCrossPoints = points;
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.domain.paint.PaintContextInterface#getAngleSnapCrossPoints()
	 */
	@Override
	public Collection<Vector2d> getAngleSnapCrossPoints() {
		return angleSnapCrossPoints;
	}

	@Override
	public void updateGrids() {
		gridPoints = new ArrayList<>();
		double paperSize = getCreasePattern().getPaperSize();

		double step = paperSize / gridDivNum;
		for (int ix = 0; ix < gridDivNum + 1; ix++) {
			for (int iy = 0; iy < gridDivNum + 1; iy++) {
				var paperDomain = getPaperDomain();
				double x = paperDomain.getLeft() + step * ix;
				double y = paperDomain.getTop() + step * iy;

				gridPoints.add(new Vector2d(x, y));
			}
		}
	}

	@Override
	public int getGridDivNum() {
		return gridDivNum;
	}

	@Override
	public void setGridDivNum(final int divNum) {
		gridDivNum = divNum;
		updateGrids();
	}

	@Override
	public Collection<Vector2d> getGrids() {
		return gridPoints;
	}

	/*
	 * (non Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PaintContext: #line=" + pickedLines.size() +
				", #vertex=" + pickedVertices.size();
	}

}
