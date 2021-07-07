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

import java.util.Collection;
import java.util.Comparator;

import javax.vecmath.Vector2d;

import oripa.domain.creasepattern.CreasePatternInterface;
import oripa.geom.RectangleDomain;
import oripa.value.OriLine;

/**
 * This class provides a drawing method for crease pattern and some utilities.
 *
 * @author Koji
 *
 */
public class CreasePatternGraphicDrawer {

	/**
	 * draws crease pattern according to the context of user interaction.
	 *
	 * @param drawer
	 *            a graphic object.
	 * @param context
	 *            a context of user interaction.
	 * @param forceShowingVertex
	 *            true if vertices must be drawn regardless of the context.
	 */
	public void draw(
			final ObjectGraphicDrawer drawer,
			final PaintContextInterface context, final boolean forceShowingVertex) {

		CreasePatternInterface creasePattern = context.getCreasePattern();

		if (context.isGridVisible()) {
			drawGridLines(drawer, context.getGridDivNum(), creasePattern.getPaperSize(),
					context.getPaperDomain(),
					context.getScale(), context.isZeroLineWidth());
		}

		drawLines(drawer, creasePattern, context.getScale(), context.isZeroLineWidth(),
				context.isMVLineVisible(),
				context.isAuxLineVisible());

		// Drawing of the vertices
		if (context.isVertexVisible() || forceShowingVertex) {
			drawVertices(drawer, creasePattern, context.getScale(), context.isMVLineVisible(),
					context.isAuxLineVisible());
		}
	}

	/**
	 * draws each of given lines with the color and width which are determined
	 * by the line type.
	 *
	 * @param drawer
	 *            a graphic object.
	 * @param lines
	 *            a collection of lines to be drawn.
	 * @param scale
	 *            scale of crease pattern drawing.
	 * @param zeroLineWidth
	 *            true if width of each line should be zero (thinnest).
	 */
	public void drawAllLines(
			final ObjectGraphicDrawer drawer, final Collection<OriLine> lines,
			final double scale, final boolean zeroLineWidth) {

		drawLines(drawer, lines, scale, zeroLineWidth, true, true);
	}

	/**
	 * Draws given lines. This method first draws aux lines and then other
	 * lines. Hence aux lines will be always overdrawn by others.
	 *
	 * @param drawer
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
			final ObjectGraphicDrawer drawer,
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
					drawLine(drawer, line, scale, zeroLineWidth);
				});
	}

	/**
	 * draws given line with the color and width which are determined by the
	 * line type.
	 *
	 * @param drawer
	 *            a graphic object.
	 * @param line
	 *            a line to be drawn.
	 * @param scale
	 *            scale of crease pattern drawing.
	 * @param zeroLineWidth
	 *            true if width of each line should be zero (thinnest).
	 */
	private void drawLine(final ObjectGraphicDrawer drawer, final OriLine line,
			final double scale, final boolean zeroLineWidth) {

		drawer.selectColor(line.getType());
		drawer.selectStroke(line.getType(), scale, zeroLineWidth);

		drawer.drawLine(line);
	}

	/**
	 * draws all vertices of mountain/valley lines.
	 *
	 * @param drawer
	 *            a graphic object.
	 * @param creasePattern
	 *            a collection of lines whose end points are to be drawn.
	 * @param scale
	 *            scale of crease pattern drawing.
	 */
	public void drawCreaseVertices(
			final ObjectGraphicDrawer drawer, final Collection<OriLine> creasePattern, final double scale) {
		drawVertices(drawer, creasePattern, scale, true, false);
	}

	/**
	 * draws the vertices of given lines.
	 *
	 * @param drawer
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
			final ObjectGraphicDrawer drawer, final Collection<OriLine> creasePattern, final double scale,
			final boolean creaseVisible, final boolean auxVisible) {

		drawer.selectNormalVertexColor();
		drawer.selectNormalVertexSize(scale);
		for (OriLine line : creasePattern) {
			if (!auxVisible && line.isAux()) {
				continue;
			}
			if (!creaseVisible && line.isMV()) {
				continue;
			}

			drawer.drawVertex(line.p0);
			drawer.drawVertex(line.p1);
		}
	}

	/**
	 * draws the coordinate of given candidate vertex at the left top of the
	 * graphic.
	 *
	 * @param drawer
	 *            a graphic object.
	 * @param candidate
	 *            a vertex which is expected to be a candidate of picking.
	 */
	public void drawCandidatePositionString(final ObjectGraphicDrawer drawer, final Vector2d candidate) {
		if (candidate == null) {
			return;
		}
		drawer.drawString("(" + candidate.x + "," + candidate.y + ")", 0, 10);
	}

	/**
	 * draws grid lines.
	 *
	 * @param drawer
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
	private void drawGridLines(final ObjectGraphicDrawer drawer,
			final int gridDivNum, final double paperSize,
			final RectangleDomain domain,
			final double scale, final boolean zeroLineWidth) {

		drawer.selectColor(OriLine.Type.AUX);
		drawer.selectStroke(OriLine.Type.AUX, scale, zeroLineWidth);

		int lineNum = gridDivNum;
		double step = paperSize / lineNum;

		for (int i = 1; i < lineNum; i++) {
			drawer.drawLine(
					step * i + domain.getLeft(), domain.getTop(),
					step * i + domain.getLeft(), domain.getTop() + paperSize);

			drawer.drawLine(
					domain.getLeft(), step * i + domain.getTop(),
					domain.getLeft() + paperSize, step * i + domain.getTop());
		}
	}

}
