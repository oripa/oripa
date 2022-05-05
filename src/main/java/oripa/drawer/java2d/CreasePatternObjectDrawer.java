package oripa.drawer.java2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;

import javax.vecmath.Vector2d;

import oripa.gui.view.creasepattern.ObjectGraphicDrawer;
import oripa.value.OriLine;
import oripa.value.OriLine.Type;

public class CreasePatternObjectDrawer implements ObjectGraphicDrawer {
	// private final static Logger logger =
	// LoggerFactory.getLogger(Java2DGraphicDrawer.class);

	private double vertexSize;

	private final CreasePatternElementSelector selector = new CreasePatternElementSelector();
	private final GraphicItemConverter converter = new GraphicItemConverter();

	private final Graphics2D g2d;

	public CreasePatternObjectDrawer(final Graphics2D g2d) {
		this.g2d = g2d;
	}

	@Override
	public void selectColor(final Type lineType) {
		g2d.setColor(selector.getColor(lineType));
	}

	@Override
	public void selectStroke(final Type lineType, final double scale, final boolean zeroWidth) {
		g2d.setStroke(selector.createStroke(lineType, scale, zeroWidth));
	}

	@Override
	public void selectSelectedItemColor() {
		g2d.setColor(selector.getSelectedItemColor());
	}

	@Override
	public void selectSelectedLineStroke(final double scale, final boolean zeroWidth) {
		g2d.setStroke(selector.createSelectedLineStroke(scale, zeroWidth));
	}

	@Override
	public void selectCandidateItemColor() {
		g2d.setColor(selector.getCandidateItemColor());
	}

	@Override
	public void selectCandidateLineStroke(final double scale, final boolean zeroWidth) {
		g2d.setStroke(selector.createCandidateLineStroke(scale, zeroWidth));
	}

	@Override
	public void selectEditingOutlineColor() {
		g2d.setColor(selector.getEditingOutlineColor());
	}

	@Override
	public void selectEditingOutlineStroke(final double scale) {
		g2d.setStroke(selector.createEditingOutlineStroke(scale));
	}

	@Override
	public void selectAssistLineColor() {
		g2d.setColor(selector.getAssistLineColor());
	}

	@Override
	public void selectAreaSelectionColor() {
		g2d.setColor(selector.getAreaSelectionColor());
	}

	@Override
	public void selectAreaSelectionStroke(final double scale) {
		g2d.setStroke(selector.createAreaSelectionStroke(scale));
	}

	@Override
	public void selectOverlappingLineHighlightColor() {
		g2d.setColor(selector.getOverlappingLineHighlightColor());
	}

	@Override
	public void selectOverlappingLineHighlightStroke(final double scale) {
		g2d.setStroke(selector.createOverlappingLineHighlightStroke(scale));
	}

	@Override
	public void selectNormalVertexColor() {
		g2d.setColor(selector.getNormalVertexColor());
	}

	@Override
	public void selectViolatingVertexColor() {
		g2d.setColor(selector.getViolatingVertexColor());
	}

	@Override
	public void selectViolatingFaceColor() {
		g2d.setColor(selector.getViolatingFaceColor());
	}

	@Override
	public void selectNormalFaceColor() {
		g2d.setColor(selector.getNormalFaceColor());
	}

	@Override
	public void selectNormalVertexSize(final double scale) {
		vertexSize = selector.createNormalVertexSize(scale);
	}

	@Override
	public void selectViolatingVertexSize(final double scale) {
		vertexSize = selector.createViolatingVertexSize(scale);
	}

	@Override
	public void selectMouseActionVertexSize(final double scale) {
		vertexSize = selector.createMouseActionVertexSize(scale);
	}

	@Override
	public void drawVertex(final Vector2d vertex) {
		g2d.fill(converter.toRectangle2D(vertex, vertexSize));
	}

	@Override
	public void drawLine(final OriLine line) {
		g2d.draw(converter.toLine2D(line));
	}

	@Override
	public void drawLine(final Vector2d p0, final Vector2d p1) {
		g2d.draw(converter.toLine2D(p0, p1));
	}

	@Override
	public void drawLine(final double x0, final double y0, final double x1, final double y1) {
		g2d.draw(converter.toLine2D(new Vector2d(x0, y0), new Vector2d(x1, y1)));
	}

	@Override
	public void drawRectangle(final Vector2d p0, final Vector2d p1) {
		g2d.draw(converter.toRectangle2D(p0, p1));
	}

	@Override
	public void fillFace(final List<Vector2d> vertices) {
		g2d.fill(converter.toPath2D(vertices));
	}

	@Override
	public void drawString(final String text, final float x, final float y) {
		g2d.setColor(Color.BLACK);
		g2d.drawString(text, x, y);
	}

	@Override
	public void setUntiAlias(final boolean untiAlias) {
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				untiAlias ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);

	}
}
