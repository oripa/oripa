package oripa.domain.paint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import oripa.domain.cptool.Painter;
import oripa.domain.creasepattern.CreasePattern;
import oripa.geom.GeomUtil;
import oripa.geom.Line;
import oripa.geom.RectangleDomain;
import oripa.value.OriLine;
import oripa.vecmath.Vector2d;

class PaintContextImpl implements PaintContext {

	private CreasePattern creasePattern;
	private final CreasePatternUndoer undoer = new CreasePatternUndoerImpl(this);

	private final LinkedList<Vector2d> pickedVertices = new LinkedList<>();

	private final LinkedList<OriLine> pickedLines = new LinkedList<>();
	private boolean isPasting = false;

	private Vector2d candidateVertexToPick = new Vector2d(0, 0);
	private OriLine candidateLineToPick = null;
	private Line solutionLineToPick = null;

	private OriLine.Type lineTypeOfNewLines;

	private AngleStep angleStep;

	private int gridDivNum;
	private List<Vector2d> gridPoints;
	private boolean triangularGridMode = false; // true: triangular grid, false:
												// square grid

	private Collection<Line> solutionLines = new ArrayList<>();
	private Collection<Vector2d> snapPoints = new ArrayList<Vector2d>();

	private Collection<OriLine> importedLines = List.of();

	private CircleCopyParameter circleCopyParameter;
	private ArrayCopyParameter arrayCopyParameter;

	private final double pointEps = GeomUtil.pointEps();

	public PaintContextImpl() {
	}

	@Override
	public boolean isPasting() {
		return isPasting;
	}

	@Override
	public void startPasting() {
		this.isPasting = true;
	}

	@Override
	public void finishPasting() {
		this.isPasting = false;
	}

	@Override
	public void clear(final boolean unselect) {

		if (unselect) {
			pickedLines.stream().forEach(l -> l.setSelected(false));
		}

		pickedLines.clear();
		pickedVertices.clear();

		candidateLineToPick = null;
		candidateVertexToPick = null;
		solutionLineToPick = null;

		clearSnapPoints();
		clearSolutionLines();
	}

	@Override
	public List<Vector2d> getPickedVertices() {
		return Collections.unmodifiableList(pickedVertices);
	}

	@Override
	public List<OriLine> getPickedLines() {
		return Collections.unmodifiableList(pickedLines);
	}

	@Override
	public OriLine getLine(final int index) {
		return pickedLines.get(index);
	}

	@Override
	public Vector2d getVertex(final int index) {
		return pickedVertices.get(index);
	}

	@Override
	public void pushVertex(final Vector2d picked) {
		pickedVertices.addLast(picked);
	}

	@Override
	public void pushLine(final OriLine picked) {
		// picked.selected = true;
		pickedLines.addLast(picked);
	}

	@Override
	public Optional<Vector2d> popVertex() {
		if (pickedVertices.isEmpty()) {
			return Optional.empty();
		}

		return Optional.of(pickedVertices.removeLast());
	}

	@Override
	public Optional<OriLine> popLine() {
		if (pickedLines.isEmpty()) {
			return Optional.empty();
		}

		OriLine line = pickedLines.removeLast();
		line.setSelected(false);

		return Optional.of(line);
	}

	@Override
	public boolean removeLine(final OriLine line) {

		return pickedLines.remove(line);
	}

	@Override
	public Optional<Vector2d> peekVertex() {
		return Optional.ofNullable(pickedVertices.peekLast());
	}

	@Override
	public Optional<OriLine> peekLine() {
		return Optional.ofNullable(pickedLines.peekLast());
	}

	@Override
	public int getLineCount() {
		return pickedLines.size();
	}

	@Override
	public int getVertexCount() {
		return pickedVertices.size();
	}

	@Override
	public Optional<Vector2d> getCandidateVertexToPick() {
		return Optional.ofNullable(candidateVertexToPick);
	}

	@Override
	public void setCandidateVertexToPick(final Vector2d candidate) {
		this.candidateVertexToPick = candidate;
	}

	@Override
	public Optional<OriLine> getCandidateLineToPick() {
		return Optional.ofNullable(candidateLineToPick);
	}

	@Override
	public void setCandidateLineToPick(final OriLine candidate) {
		this.candidateLineToPick = candidate;
	}

	@Override
	public void setSolutionLineToPick(final Line solutionLine) {
		solutionLineToPick = solutionLine;
	}

	@Override
	public Optional<Line> getSolutionLineToPick() {
		return Optional.ofNullable(solutionLineToPick);
	}

	@Override
	public void setLineTypeOfNewLines(final OriLine.Type lineType) {
		lineTypeOfNewLines = lineType;
	}

	@Override
	public OriLine.Type getLineTypeOfNewLines() {
		return lineTypeOfNewLines;
	}

	@Override
	public Painter getPainter() {
		return new Painter(creasePattern, pointEps);
	}

	@Override
	public CreasePatternUndoer creasePatternUndo() {
		return undoer;
	}

	@Override
	public void refreshCreasePattern() {
		creasePattern.refresh(pointEps);
		if (!gridPoints.isEmpty()) {
			updateGrids();
		}
	}

	@Override
	public void setCreasePattern(final CreasePattern aCreasePattern) {
		creasePattern = aCreasePattern;
	}

	@Override
	public CreasePattern getCreasePattern() {
		return creasePattern;
	}

	@Override
	public RectangleDomain getPaperDomain() {
		return creasePattern.getPaperDomain();
	}

	@Override
	public void setAngleStep(final AngleStep step) {
		angleStep = step;
	}

	@Override
	public AngleStep getAngleStep() {
		return angleStep;
	}

	@Override
	public void setSolutionLines(final Collection<Line> lines) {
		solutionLines = Collections.unmodifiableList(new ArrayList<>(lines));
	}

	@Override
	public Collection<Line> getSolutionLines() {
		return solutionLines;
	}

	@Override
	public void clearSolutionLines() {
		solutionLines = List.of();
	}

	@Override
	public void setSnapPoints(final Collection<Vector2d> points) {
		snapPoints = Collections.unmodifiableList(new ArrayList<>(points));
	}

	@Override
	public Collection<Vector2d> getSnapPoints() {
		return snapPoints;
	}

	@Override
	public void clearSnapPoints() {
		snapPoints = List.of();
	}

	@Override
	public void SetImportedLines(final Collection<OriLine> lines) {
		importedLines = Collections.unmodifiableList(new ArrayList<>(lines));
	}

	@Override
	public void loadFromImportedLines() {
		pickedLines.addAll(importedLines);
	}

	@Override
	public void clearImportedLines() {
		importedLines = List.of();
	}

	@Override
	public void updateGrids() {
		ArrayList<Vector2d> points;
		if (triangularGridMode) {
			points = getTriangularGridPoints();
		} else {
			points = getSquareGridPoints();
		}
		gridPoints = Collections.unmodifiableList(points);
	}

	private ArrayList<Vector2d> getSquareGridPoints() {
		var points = new ArrayList<Vector2d>();
		var domain = getPaperDomain();

		double smaller = Math.min(domain.getWidth(), domain.getHeight());
		double step = smaller / gridDivNum;
		double right = domain.getRight();
		double top = domain.getTop();

		for (var x = domain.getLeft(); x < right; x += step) {
			points.add(new Vector2d(x, top));
			for (var y = domain.getBottom(); y > top; y -= step) {
				points.add(new Vector2d(x, y));
			}
		}

		// When smaller is vertical, right end may not have points
		for (var y = domain.getBottom(); y > top; y -= step) {
			points.add(new Vector2d(right, y));
		}
		return points;
	}

	private ArrayList<Vector2d> getTriangularGridPoints() {
		var points = new ArrayList<Vector2d>();
		var domain = getPaperDomain();

		double tan30 = Math.tan(Math.PI / 6);

		double stepX = domain.getWidth() / gridDivNum;
		double stepY = stepX * tan30 * 2;

		double left = domain.getLeft();
		double right = domain.getRight();

		double bottom = domain.getBottom();
		double top = bottom - Math.ceil(domain.getHeight() / stepY) * stepY;

		double oddColumnsYOffset = 0;
		for (var x = left; x <= right; x += stepX) {
			points.add(new Vector2d(x, top));
			points.add(new Vector2d(x, bottom));

			for (var y = bottom; y > top; y -= stepY) {
				points.add(new Vector2d(x, y + oddColumnsYOffset));
			}
			oddColumnsYOffset = -oddColumnsYOffset + stepY / 2.0;
		}

		return points;
	}

	public void setTriangularGridMode(boolean enabled) {
		if (this.triangularGridMode != enabled) {

			this.triangularGridMode = enabled;
			updateGrids();
		}
	}

	public boolean isTriangularGridMode() {
		return triangularGridMode;
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
		return Collections.unmodifiableList(gridPoints);
	}

	@Override
	public void clearGrids() {
		gridPoints = List.of();
	}

	@Override
	public String toString() {
		return "PaintContext: #line=" + pickedLines.size() +
				", #vertex=" + pickedVertices.size();
	}

	@Override
	public CircleCopyParameter getCircleCopyParameter() {
		return circleCopyParameter;
	}

	@Override
	public void setCircleCopyParameter(final CircleCopyParameter circleCopyParameter) {
		this.circleCopyParameter = circleCopyParameter;
	}

	@Override
	public void setArrayCopyParameter(final ArrayCopyParameter p) {
		arrayCopyParameter = p;
	}

	@Override
	public ArrayCopyParameter getArrayCopyParameter() {
		return arrayCopyParameter;
	}

	@Override
	public double getPointEps() {
		return pointEps;
	}

}
