package oripa.paint.outline;

import java.util.ArrayList;
import java.util.Collection;

import javax.vecmath.Vector2d;

import oripa.ORIPA;
import oripa.paint.CreasePatternInterface;
import oripa.paint.creasepattern.Painter;
import oripa.paint.util.PairLoop;
import oripa.value.OriLine;

public class CloseTempOutline {

	private class ContourLineAdder implements PairLoop.Block<Vector2d> {

		private Collection<OriLine> creasePattern;

		public ContourLineAdder(Collection<OriLine> creasePattern) {
			this.creasePattern = creasePattern;
		}

		@Override
		public boolean yield(Vector2d element, Vector2d nextElement) {
			OriLine line;
	        Painter painter = new Painter();

			line = new OriLine(element, nextElement, OriLine.TYPE_CUT);
			painter.addLine(line, creasePattern);
			return true;
		}
	}

	public void execute(Collection<Vector2d> outlinevertices){

		CreasePatternInterface creasePattern = ORIPA.doc.getCreasePattern();

        // Delete the current outline
		ArrayList<OriLine> outlines = new ArrayList<>();
		for (OriLine line : creasePattern) {
			if (line.typeVal == OriLine.TYPE_CUT) {
				outlines.add(line);
			}
		}
		for (OriLine line : outlines) {
			creasePattern.remove(line);
		}

		// Update the contour line
		PairLoop.iterateAll(
				outlinevertices, new ContourLineAdder(creasePattern));


		// To delete a segment out of the contour
		while (true) {
			boolean bDeleteLine = false;
			for (OriLine line : creasePattern) {
				if (line.typeVal == OriLine.TYPE_CUT) {
					continue;
				}
				Vector2d OnPoint0 = isOnTmpOutlineLoop(outlinevertices, line.p0);
				Vector2d OnPoint1 = isOnTmpOutlineLoop(outlinevertices, line.p1);

				Painter painter = new Painter();
				if (OnPoint0 != null && OnPoint0 == OnPoint1) {
					painter.removeLine(line, creasePattern);
					bDeleteLine = true;
					break;
				}

				if ((OnPoint0 == null && isOutsideOfTmpOutlineLoop(outlinevertices, line.p0))
						|| (OnPoint1 == null && isOutsideOfTmpOutlineLoop(outlinevertices, line.p1))) {
					painter.removeLine(line, creasePattern);
					bDeleteLine = true;
					break;
				}
			}
			if (!bDeleteLine) {
				break;
			}
		}

		outlinevertices.clear();
		//	        ORIPA.mainFrame.uiPanel.modeChanged();


	}

    
    private Vector2d isOnTmpOutlineLoop(
    		Collection<Vector2d> outlinevertices, Vector2d v) {

    	return (new IsOnTempOutlineLoop()).execute(outlinevertices, v);
    }

    private boolean isOutsideOfTmpOutlineLoop(    			
    		Collection<Vector2d> outlinevertices, Vector2d v) {

    	return(new IsOutsideOfTempOutlineLoop()).execute(outlinevertices, v);
    }

}
