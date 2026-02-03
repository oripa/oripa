package oripa.domain.cptool;

import java.util.ArrayList;
import java.util.Collection;

import oripa.geom.GeomUtil;
import oripa.geom.RectangleDomain;
import oripa.value.OriLine;
import oripa.vecmath.Vector2d;

public class TiledLineFactory {

    /**
     * create lines that fill out the paper.
     *
     * @param selectedLines
     *            lines to be copied
     * @param creasePattern
     *
     * @param paperSize
     *            paper size
     * @return
     */
    public Collection<OriLine> createFullyTiledLines(
            final Collection<OriLine> selectedLines,
            final Collection<OriLine> creasePattern, final double paperSize, final double eps) {

        var selectionDomain = RectangleDomain.createFromSegments(selectedLines);

        int startRow = (int) (-paperSize / selectionDomain.getHeight());
        int startCol = (int) (-paperSize / selectionDomain.getWidth());
        int endRow = (int) (paperSize / selectionDomain.getHeight() + 0.5);
        int endCol = (int) (paperSize / selectionDomain.getWidth() + 0.5);

        return createTiledLinesImpl(
                startRow, startCol, endRow, endCol,
                selectionDomain.getWidth(), selectionDomain.getHeight(),
                selectedLines, creasePattern, eps);
    }

    /**
     * create lines that fill out given area. the tiling count starts from
     * selectedLines domain.
     *
     * @param row
     *            the count of tiles on x coordinate
     * @param col
     *            the count of tiles on y coordinate
     * @param interX
     *            interval length of x coordinate
     * @param interY
     *            interval length of y coordinate
     * @param selectedLines
     *            lines to be copied
     * @param creasePattern
     *
     * @return copies of selectedLines
     */
    public Collection<OriLine> createTiledLines(
            final int row, final int col, final double interX, final double interY,
            final Collection<OriLine> selectedLines,
            final Collection<OriLine> creasePattern,
            final double eps) {

        int startRow = 0;
        int startCol = 0;
        int endRow = row;
        int endCol = col;

        return createTiledLinesImpl(
                startRow, startCol, endRow, endCol,
                interX, interY,
                selectedLines, creasePattern, eps);
    }

    private Collection<OriLine> createTiledLinesImpl(
            final int startRow, final int startCol, final int endRow, final int endCol,
            final double interX, final double interY,
            final Collection<OriLine> selectedLines,
            final Collection<OriLine> creasePattern,
            final double eps) {

        System.out.println("startRow=" + startRow + " startCol=" + startCol + " endRow=" + endRow
                + " endCol=" + endCol);

        ArrayList<OriLine> copiedLines = new ArrayList<OriLine>();

        var domain = RectangleDomain.createFromSegments(creasePattern);

        var boundaries = creasePattern.stream()
                .filter(OriLine::isBoundary)
                .toList();

        var clipper = new RectangleClipper(domain, eps);

        for (int x = startCol; x < endCol; x++) {
            for (int y = startRow; y < endRow; y++) {
                if (x == 0 && y == 0) {
                    continue;
                }

                var interval = new Vector2d(interX * x, interY * y);

                // copies the selected lines
                for (OriLine l : selectedLines) {
                    var clP0 = l.getP0().add(interval);
                    var clP1 = l.getP1().add(interval);

                    var clOpt = clipper.clip(new OriLine(clP0, clP1, l.getType()))
                            .filter(cl -> boundaries.stream()
                                    .noneMatch(boundary -> GeomUtil.isOverlap(cl, boundary, eps)));

                    clOpt.ifPresent(copiedLines::add);
                }
            }
        }

        return copiedLines;
    }

}
