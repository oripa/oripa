package oripa.domain.cptool;

import java.util.ArrayList;
import java.util.Collection;

import oripa.geom.RectangleDomain;
import oripa.value.OriLine;

public class TiledLineFactory {

	/**
	 * create lines that fill out the paper.
	 *
	 * @param selecetdLines
	 * @param creasePattern
	 * @param paperSize
	 * @return
	 */
	public Collection<OriLine> createFullyTiledLines(
			final Collection<OriLine> selecetdLines,
			final Collection<OriLine> creasePattern, final double paperSize) {

		var selectionDomain = new RectangleDomain(selecetdLines);

		int startRow = (int) (-paperSize / selectionDomain.getHeight());
		int startCol = (int) (-paperSize / selectionDomain.getWidth());
		int endRow = (int) (paperSize / selectionDomain.getHeight() + 0.5);
		int endCol = (int) (paperSize / selectionDomain.getWidth() + 0.5);

		return createTiledLinesImpl(
				startRow, startCol, endRow, endCol,
				selectionDomain.getWidth(), selectionDomain.getHeight(),
				selecetdLines, creasePattern);
	}

	/**
	 * create lines that fill out given area.
	 *
	 * @param row
	 * @param col
	 * @param interX
	 * @param interY
	 * @param selectedLines
	 * @param creasePattern
	 * @return
	 */
	public Collection<OriLine> createTiledLines(
			final int row, final int col, final double interX, final double interY,
			final Collection<OriLine> selectedLines,
			final Collection<OriLine> creasePattern) {

		int startRow = 0;
		int startCol = 0;
		int endRow = row;
		int endCol = col;

		return createTiledLinesImpl(
				startRow, startCol, endRow, endCol,
				interX, interY,
				selectedLines, creasePattern);
	}

	private Collection<OriLine> createTiledLinesImpl(
			final int startRow, final int startCol, final int endRow, final int endCol,
			final double interX, final double interY,
			final Collection<OriLine> selectedLines,
			final Collection<OriLine> creasePattern) {

		System.out.println("startRow=" + startRow + " startCol=" + startCol + " endRow=" + endRow
				+ " endCol=" + endCol);

		ArrayList<OriLine> copiedLines = new ArrayList<OriLine>();

		var domain = new RectangleDomain(creasePattern);
		var clipper = new oripa.domain.paint.util.RectangleClipper(
				domain.getLeft(), domain.getTop(), domain.getRight(), domain.getBottom());

		for (int x = startCol; x < endCol; x++) {
			for (int y = startRow; y < endRow; y++) {
				if (x == 0 && y == 0) {
					continue;
				}

				// copies the selected lines
				for (OriLine l : selectedLines) {
					OriLine cl = new OriLine(l);
					cl.p0.x += interX * x;
					cl.p0.y += interY * y;
					cl.p1.x += interX * x;
					cl.p1.y += interY * y;

					if (clipper.clip(cl)) {
						copiedLines.add(cl);
					}
				}
			}
		}

		return copiedLines;
	}

}
