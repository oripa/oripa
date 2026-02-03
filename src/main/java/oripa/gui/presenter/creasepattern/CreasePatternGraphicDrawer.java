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
package oripa.gui.presenter.creasepattern;

import java.util.Collection;
import java.util.Comparator;

import oripa.domain.creasepattern.CreasePattern;
import oripa.domain.paint.PaintContext;
import oripa.geom.RectangleDomain;
import oripa.gui.view.creasepattern.ObjectGraphicDrawer;
import oripa.value.OriLine;
import oripa.vecmath.Vector2d;

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
     * @param viewContext
     *            a context of crease pattern appearance.
     * @param paintContext
     *            a context of paint interaction
     * @param forceShowingVertex
     *            true if vertices must be drawn regardless of the context.
     */
    public void draw(
            final ObjectGraphicDrawer drawer,
            final CreasePatternViewContext viewContext, final PaintContext paintContext,
            final boolean forceShowingVertex) {

        CreasePattern creasePattern = paintContext.getCreasePattern();

        if (viewContext.isGridVisible()) {
            if (paintContext.isTriangularGridMode()) {
                drawTriangularGrid(drawer, paintContext.getGridDivNum(),
                        paintContext.getPaperDomain(),
                        viewContext.getScale(), viewContext.isZeroLineWidth());
            } else {
                drawSquareGrid(drawer, paintContext.getGridDivNum(),
                        paintContext.getPaperDomain(),
                        viewContext.getScale(), viewContext.isZeroLineWidth());
            }
        }

        drawLines(drawer, creasePattern, viewContext.getScale(), viewContext.isZeroLineWidth(),
                viewContext.isMVLineVisible(),
                viewContext.isAuxLineVisible());

        // Drawing of the vertices
        if (viewContext.isVertexVisible() || forceShowingVertex) {
            drawVertices(drawer, creasePattern, viewContext.getScale(), viewContext.isMVLineVisible(),
                    viewContext.isAuxLineVisible());
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
                    if (line.isFoldLine() && !creaseVisible) {
                        return;
                    }
                    // if (pickedLines != null && pickedLines.contains(line)) {
                    // return;
                    // }
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
            if (!creaseVisible && line.isFoldLine()) {
                continue;
            }

            drawer.drawVertex(line.getP0());
            drawer.drawVertex(line.getP1());
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
        drawer.drawString("(" + candidate.getX() + "," + candidate.getY() + ")", 0, 10);
    }

    /**
     * draws square grid lines.
     *
     * @param drawer
     *            a graphic object.
     * @param gridDivNum
     *            the number of grid division.
     * @param domain
     *            a ractangle domain fitting to the crease pattern.
     * @param scale
     *            scale of crease pattern drawing.
     * @param zeroLineWidth
     *            true if width of each line should be zero (thinnest).
     */
    private void drawSquareGrid(final ObjectGraphicDrawer drawer,
            final int gridDivNum,
            final RectangleDomain domain,
            final double scale, final boolean zeroLineWidth) {

        drawer.selectColor(OriLine.Type.AUX);
        drawer.selectStroke(OriLine.Type.AUX, scale, zeroLineWidth);

        double smaller = Math.min(domain.getWidth(), domain.getHeight());
        double step = smaller / gridDivNum;
        double left = domain.getLeft();
        double right = domain.getRight();
        double top = domain.getTop();
        double bottom = domain.getBottom();

        // vertical lines
        for (var x = left; x <= right; x += step) {
            drawer.drawLine(x, top, x, bottom);
        }

        // horizontal lines
        for (var y = bottom; y >= top; y -= step) {
            drawer.drawLine(left, y, right, y);
        }
    }

    public void drawTriangularGrid(final ObjectGraphicDrawer drawer,
            final int gridDivNum,
            final RectangleDomain domain,
            final double scale, final boolean zeroLineWidth) {
        double tan30 = Math.tan(Math.PI / 6);

        double stepX = domain.getWidth() / gridDivNum;
        double stepY = stepX * tan30 * 2;

        double left = domain.getLeft();
        double right = domain.getRight();

        double fullY = (right - left) * tan30;

        double bottom = domain.getBottom();
        double top = bottom - Math.ceil(domain.getHeight() / stepY) * stepY;
        double fullX = (bottom - top) / tan30;

        drawer.selectColor(OriLine.Type.AUX);
        drawer.selectStroke(OriLine.Type.AUX, scale, zeroLineWidth);

        // Vertical lines
        for (var x = left; x <= right; x += stepX) {
            drawer.drawLine(x, top, x, bottom);
        }

        // Oblique lines: walking left and right
        for (var y = bottom; y - fullY >= top; y -= stepY) {
            drawer.drawLine(left, y, right, y - fullY);
            drawer.drawLine(right, y, left, y - fullY);
        }

        // Oblique lines: walking top and bottom
        for (var deltaX = stepX * 2; deltaX <= right - left; deltaX += stepX * 2) {
            double h = deltaX * tan30;

            // side-to-top
            if (top + h < bottom) {
                drawer.drawLine(left + deltaX, top, left, top + h);
                drawer.drawLine(right - deltaX, top, right, top + h);
            } else {
                drawer.drawLine(left + deltaX, top, left + deltaX - fullX, bottom);
                drawer.drawLine(right - deltaX, top, right - deltaX + fullX, bottom);
            }

            // side-to-bottom
            if (bottom - h > top) {
                drawer.drawLine(left + deltaX, bottom, left, bottom - h);
                drawer.drawLine(right - deltaX, bottom, right, bottom - h);
            }
        }
    }

    public void highlightOverlappingLines(final ObjectGraphicDrawer drawer,
            final Collection<OriLine> overlappingLines,
            final double scale) {
        for (var line : overlappingLines) {
            drawer.selectOverlappingLineHighlightColor();
            drawer.selectOverlappingLineHighlightStroke(scale);

            drawer.drawLine(line);
        }
    }

}
