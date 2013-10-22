package oripa.paint.creasepattern.tool;

import java.util.ArrayList;
import java.util.Collection;

import oripa.value.OriLine;

public class TiledLineFactory {

	/**
	 * create lines that fill out the paper.
	 * @param lines
	 * @param creasePattern
	 * @param paperSize
	 * @return
	 */
	public Collection<OriLine> createFullyTiledLines(
			Collection<OriLine> lines,
			Collection<OriLine> creasePattern, double paperSize) {

		RectangleDomain domain = new RectangleDomain(lines);

		int startRow = (int) (-paperSize / domain.getHeight());
		int startCol = (int) (-paperSize / domain.getWidth());
		int endRow =  (int) (paperSize / domain.getHeight() + 0.5);
		int endCol =  (int) (paperSize / domain.getWidth() + 0.5);

		return createTiledLinesImpl(
				startRow, startCol, endRow, endCol,
				domain.getWidth(), domain.getHeight(), creasePattern, paperSize);
	}

	/**
	 * create lines that fill out given area.
	 * @param row
	 * @param col
	 * @param interX
	 * @param interY
	 * @param creasePattern
	 * @param paperSize
	 * @return
	 */
	public Collection<OriLine> createTiledLines(
			int row, int col, double interX, double interY,
			Collection<OriLine> creasePattern, double paperSize) {

		int startRow =  0;
		int startCol =  0;
		int endRow =  row;
		int endCol =  col;

		return createTiledLinesImpl(
				startRow, startCol, endRow, endCol,
				interX, interY, creasePattern, paperSize);
	}


	private Collection<OriLine> createTiledLinesImpl(
			int startRow, int startCol, int endRow, int endCol,
			double interX, double interY,
			Collection<OriLine> creasePattern, double paperSize) {


		System.out.println("startRow=" + startRow + " startCol=" + startCol + " endRow=" + endRow + " endCol=" + endCol);

		ArrayList<OriLine> copiedLines = new ArrayList<OriLine>();

	    //FIXME Restrictedly speaking, paper is not always in this position. CreasePattern should have the values??
		oripa.geom.RectangleClipper clipper =
				new oripa.geom.RectangleClipper(
						-paperSize / 2, -paperSize / 2, paperSize / 2, paperSize / 2);

		for (int x = startCol; x < endCol; x++) {
			for (int y = startRow; y < endRow; y++) {
				if (x == 0 && y == 0) {
					continue;
				}

				// copies the selected lines
				for (OriLine l : creasePattern) {
					if (!l.selected) {
						continue;
					}

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
//		LineAdder adder = new LineAdder();
//		for (OriLine l : copiedLines) {
//			adder.addLine(l, creasePattern);
//		}
//		Painter painter = new Painter();
//		painter.resetSelectedOriLines(creasePattern);
	}

}
