/**
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2013-     ORIPA OSS Project  https://github.com/oripa/oripa
 * Copyright (C) 2005-2009 Jun Mitani         http://mitani.cs.tsukuba.ac.jp/

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package oripa.domain.paint;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Comparator;

import javax.vecmath.Vector2d;

import oripa.domain.creasepattern.CreasePatternInterface;
import oripa.domain.paint.util.ElementSelector;
import oripa.geom.RectangleDomain;
import oripa.value.OriLine;

/**
 * This class provides a drawing method for crease pattern and some utilities.
 *
 * @author Koji
 *
 */
public class CreasePatternGraphicDrawer {

	private final ElementSelector selector = new ElementSelector();

	/**
	 * draws crease pattern according to the context of user interaction.
	 *
	 * @param g2d
	 * @param context
	 * @param forceShowingVertex
	 */
	public void draw(
			final Graphics2D g2d,
			final PaintContextInterface context, final boolean forceShowingVertex) {

		CreasePatternInterface creasePattern = context.getCreasePattern();

		if (context.isGridVisible()) {
			drawGridLines(g2d, context.getGridDivNum(), creasePattern.getPaperSize(),
					context.getPaperDomain(),
					context.getScale(), context.isZeroLineWidth());
		}

		drawLines(g2d, creasePattern, context.getScale(), context.isZeroLineWidth(),
				context.isMVLineVisible(),
				context.isAuxLineVisible());

		// Drawing of the vertices
		if (context.isVertexVisible() || forceShowingVertex) {
			drawVertices(g2d, creasePattern, context.getScale(), context.isMVLineVisible(),
					context.isAuxLineVisible());
		}
	}

	public void drawAllLines(
			final Graphics2D g2d, final Collection<OriLine> lines,
			final double scale, final boolean zeroLineWidth) {

		drawLines(g2d, lines, scale, zeroLineWidth, true, true);
	}

	/**
	 * draws given lines. {@code pickedLines} will be skipped because
	 * {@link GraphicMouseActionInterface} should draw them.
	 *
	 * @param g2d
	 * @param lines
	 * @param pickedLines
	 *            lines that user picked. null if nothing is selected.
	 * @param scale
	 * @param creaseVisible
	 *            true if mountain/valley lines should be shown.
	 * @param auxVisible
	 *            true if aux lines should be shown.
	 */
	private void drawLines(
			final Graphics2D g2d,
			final Collection<OriLine> lines,
			// final Collection<OriLine> pickedLines,
			final double scale, final boolean zeroLineWidth,
			final boolean creaseVisible, final boolean auxVisible) {
		// draw lines ordered by line type.
		// this is aimed to make aux lines lower.
		lines.stream()
				.sorted(Comparator.comparing(line -> line.getType().toInt()))
				.forEach(line -> {
					if (line.isAux() && !auxVisible) {
						return;
					}
					if (line.isMV() && !creaseVisible) {
						return;
					}
//					if (pickedLines != null && pickedLines.contains(line)) {
//						return;
//					}
					drawLine(g2d, line, scale, zeroLineWidth);
				});
	}

	private void drawLine(final Graphics2D g2d, final OriLine line,
			final double scale, final boolean zeroLineWidth) {

		g2d.setColor(selector.getColor(line.getType()));
		g2d.setStroke(selector.createStroke(
				line.getType(), scale, zeroLineWidth));

		g2d.draw(new Line2D.Double(line.p0.x, line.p0.y, line.p1.x,
				line.p1.y));
	}

	/**
	 * draws all vertices of mountain/valley lines.
	 *
	 * @param g2d
	 * @param creasePattern
	 * @param scale
	 */
	public void drawCreaseVertices(
			final Graphics2D g2d, final Collection<OriLine> creasePattern, final double scale) {
		drawVertices(g2d, creasePattern, scale, true, false);
	}

	private void drawVertices(
			final Graphics2D g2d, final Collection<OriLine> creasePattern, final double scale,
			final boolean creaseVisible, final boolean auxVisible) {

		g2d.setColor(selector.getNormalVertexColor());
		final double vertexSize = selector.createNormalVertexSize(scale);
		for (OriLine line : creasePattern) {
			if (!auxVisible && line.isAux()) {
				continue;
			}
			if (!creaseVisible && line.isMV()) {
				continue;
			}

			drawVertex(g2d, line.p0, vertexSize);
			drawVertex(g2d, line.p1, vertexSize);
		}
	}

	private void drawVertex(final Graphics2D g2d, final Vector2d vertex, final double vertexSize) {
		final double vertexHalfSize = vertexSize / 2;
		g2d.fill(new Rectangle2D.Double(
				vertex.x - vertexHalfSize, vertex.y - vertexHalfSize,
				vertexSize, vertexSize));
	}

	public void drawCandidatePositionString(final Graphics2D g, final Vector2d candidate) {
		if (candidate == null) {
			return;
		}
		g.setColor(Color.BLACK);
		g.drawString("(" + candidate.x +
				"," + candidate.y + ")", 0, 10);
	}

	private void drawGridLines(final Graphics2D g2d,
			final int gridDivNum, final double paperSize,
			final RectangleDomain domain,
			final double scale, final boolean zeroLineWidth) {

		g2d.setColor(selector.getColor(OriLine.Type.AUX));
		g2d.setStroke(selector.createStroke(OriLine.Type.AUX, scale, zeroLineWidth));

		int lineNum = gridDivNum;
		double step = paperSize / lineNum;

		for (int i = 1; i < lineNum; i++) {
			g2d.draw(new Line2D.Double(
					step * i + domain.getLeft(), domain.getTop(),
					step * i + domain.getLeft(), domain.getTop() + paperSize));

			g2d.draw(new Line2D.Double(
					domain.getLeft(), step * i + domain.getTop(),
					domain.getLeft() + paperSize, step * i + domain.getTop()));
		}
	}

}
