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
					context.getScale());
		}

		drawLines(g2d, creasePattern, null, context.getScale(), context.isMVLineVisible(),
				context.isAuxLineVisible());

		// Drawing of the vertices
		if (context.isVertexVisible() || forceShowingVertex) {
			drawVertices(g2d, creasePattern, context.getScale(), context.isMVLineVisible(),
					context.isAuxLineVisible());
		}
	}

	public void drawAllLines(
			final Graphics2D g2d, final Collection<OriLine> lines, final double scale) {

		drawLines(g2d, lines, null, scale, true, true);
	}

	/**
	 * draws given lines. {@code pickedLines} will be skipped because
	 * {@link GraphicMouseActionInterface} should draw them.
	 *
	 * @param g2d
	 * @param lines
	 * @param pickedLines
	 *            lines that user picked. null if nothing is selected.
	 * @param creaseVisible
	 *            true if mountain/valley lines should be shown.
	 * @param auxVisible
	 *            true if aux lines should be shown.
	 */
	private void drawLines(
			final Graphics2D g2d,
			final Collection<OriLine> lines, final Collection<OriLine> pickedLines,
			final double scale,
			final boolean creaseVisible, final boolean auxVisible) {

		for (OriLine line : lines) {
			if (line.getType() == OriLine.Type.NONE && !auxVisible) {
				continue;
			}

			if ((line.getType() == OriLine.Type.RIDGE || line.getType() == OriLine.Type.VALLEY)
					&& !creaseVisible) {
				continue;
			}

			if (pickedLines == null || !pickedLines.contains(line)) {
				g2d.setColor(selector.getColor(line.getType()));
				g2d.setStroke(selector.createStroke(line.getType(), scale));

				g2d.draw(new Line2D.Double(line.p0.x, line.p0.y, line.p1.x,
						line.p1.y));
			}

		}

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
		final double vertexHalfSize = vertexSize / 2;
		for (OriLine line : creasePattern) {
			if (!auxVisible && line.getType() == OriLine.Type.NONE) {
				continue;
			}
			if (!creaseVisible && (line.getType() == OriLine.Type.RIDGE
					|| line.getType() == OriLine.Type.VALLEY)) {
				continue;
			}
			Vector2d v0 = line.p0;
			Vector2d v1 = line.p1;

			g2d.fill(new Rectangle2D.Double(
					v0.x - vertexHalfSize, v0.y - vertexHalfSize,
					vertexSize, vertexSize));
			g2d.fill(new Rectangle2D.Double(
					v1.x - vertexHalfSize, v1.y - vertexHalfSize,
					vertexSize, vertexSize));
		}

	}

	public void drawCandidatePositionString(final Graphics2D g, final Vector2d candidate) {
		if (candidate != null) {
			g.setColor(Color.BLACK);
			g.drawString("(" + candidate.x +
					"," + candidate.y + ")", 0, 10);
		}

	}

	private void drawGridLines(final Graphics2D g2d, final int gridDivNum, final double paperSize,
			final RectangleDomain domain,
			final double scale) {

		g2d.setColor(selector.getColor(OriLine.Type.NONE));
		g2d.setStroke(selector.createStroke(OriLine.Type.NONE, scale));

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
