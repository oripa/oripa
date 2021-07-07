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
import java.util.Collection;
import java.util.Comparator;

import javax.vecmath.Vector2d;

import oripa.domain.creasepattern.CreasePatternInterface;
import oripa.drawer.java2d.ElementSelector;
import oripa.drawer.java2d.GraphicItemConverter;
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
	private final GraphicItemConverter converter = new GraphicItemConverter();

	/**
	 * draws crease pattern according to the context of user interaction.
	 *
	 * @param g2d
	 *            a graphic object.
	 * @param context
	 *            a context of user interaction.
	 * @param forceShowingVertex
	 *            true if vertices must be drawn regardless of the context.
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

	/**
	 * draws each of given lines with the color and width which are determined
	 * by the line type.
	 *
	 * @param g2d
	 *            a graphic object.
	 * @param lines
	 *            a collection of lines to be drawn.
	 * @param scale
	 *            scale of crease pattern drawing.
	 * @param zeroLineWidth
	 *            true if width of each line should be zero (thinnest).
	 */
	public void drawAllLines(
			final Graphics2D g2d, final Collection<OriLine> lines,
			final double scale, final boolean zeroLineWidth) {

		drawLines(g2d, lines, scale, zeroLineWidth, true, true);
	}

	/**
	 * Draws given lines. This method first draws aux lines and then other
	 * lines. Hence aux lines will be always overdrawn by others.
	 *
	 * @param g2d
	 *            a graphic object.
	 * @param lines
	 *            a collection of lines to be drawn.
	 * @param scale
	 *            scale of crease pattern drawing.
	 * @param zeroLineWidth
	 *            true if width of each line should be zero (thinnest).
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

	/**
	 * draws given line with the color and width which are determined by the
	 * line type.
	 *
	 * @param g2d
	 *            a graphic object.
	 * @param line
	 *            a line to be drawn.
	 * @param scale
	 *            scale of crease pattern drawing.
	 * @param zeroLineWidth
	 *            true if width of each line should be zero (thinnest).
	 */
	private void drawLine(final Graphics2D g2d, final OriLine line,
			final double scale, final boolean zeroLineWidth) {

		g2d.setColor(selector.getColor(line.getType()));
		g2d.setStroke(selector.createStroke(
				line.getType(), scale, zeroLineWidth));

		g2d.draw(converter.toLine2D(line));
	}

	/**
	 * draws all vertices of mountain/valley lines.
	 *
	 * @param g2d
	 *            a graphic object.
	 * @param creasePattern
	 *            a collection of lines whose end points are to be drawn.
	 * @param scale
	 *            scale of crease pattern drawing.
	 */
	public void drawCreaseVertices(
			final Graphics2D g2d, final Collection<OriLine> creasePattern, final double scale) {
		drawVertices(g2d, creasePattern, scale, true, false);
	}

	/**
	 * draws the vertices of given lines.
	 *
	 * @param g2d
	 *            a graphic object.
	 * @param creasePattern
	 *            a collection of lines whose end points are to be drawn.
	 * @param scale
	 *            scale of crease pattern drawing.
	 * @param creaseVisible
	 *            true if mountain/valley lines should be shown.
	 * @param auxVisible
	 *            true if aux lines should be shown.
	 */
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

	/**
	 * draws the given vertex with given size.
	 *
	 * @param g2d
	 *            a graphic object.
	 * @param vertex
	 *            a vertex to be drawn.
	 * @param vertexSize
	 *            the size of vertex.
	 */
	private void drawVertex(final Graphics2D g2d, final Vector2d vertex, final double vertexSize) {
		g2d.fill(converter.toRectangle2D(
				vertex, vertexSize));
	}

	/**
	 * draws the coordinate of given candidate vertex at the left top of the
	 * graphic.
	 *
	 * @param g
	 *            a graphic object.
	 * @param candidate
	 *            a vertex which is expected to be a candidate of picking.
	 */
	public void drawCandidatePositionString(final Graphics2D g, final Vector2d candidate) {
		if (candidate == null) {
			return;
		}
		g.setColor(Color.BLACK);
		g.drawString("(" + candidate.x +
				"," + candidate.y + ")", 0, 10);
	}

	/**
	 * draws grid lines.
	 *
	 * @param g2d
	 *            a graphic object.
	 * @param gridDivNum
	 *            the number of grid division.
	 * @param paperSize
	 *            the paper size.
	 * @param domain
	 *            a ractangle domain fitting to the crease pattern.
	 * @param scale
	 *            scale of crease pattern drawing.
	 * @param zeroLineWidth
	 *            true if width of each line should be zero (thinnest).
	 */
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
