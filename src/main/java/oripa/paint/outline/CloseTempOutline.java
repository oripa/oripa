package oripa.paint.outline;

import java.util.ArrayList;
import java.util.Collection;

import javax.vecmath.Vector2d;

import oripa.ORIPA;
import oripa.geom.OriLine;
import oripa.paint.PairLoop;

public class CloseTempOutline {

	public void execute(Collection<Vector2d> outlinevertices){

		// Delete the current outline
		ArrayList<OriLine> outlines = new ArrayList<>();
		for (OriLine line : ORIPA.doc.creasePattern) {
			if (line.typeVal == OriLine.TYPE_CUT) {
				outlines.add(line);
			}
		}
		for (OriLine line : outlines) {
			ORIPA.doc.creasePattern.remove(line);
		}

		// Update the contour line
		PairLoop.iterateAll(outlinevertices, new PairLoop.Block<Vector2d>(){
			@Override
			public boolean yield(Vector2d v1, Vector2d v2) {
				OriLine line;

				line = new OriLine(v1, v2, OriLine.TYPE_CUT);
				ORIPA.doc.addLine(line);

				return true;
			}
		});


		// To delete a segment out of the contour
		while (true) {
			boolean bDeleteLine = false;
			for (OriLine line : ORIPA.doc.creasePattern) {
				if (line.typeVal == OriLine.TYPE_CUT) {
					continue;
				}
				Vector2d OnPoint0 = isOnTmpOutlineLoop(outlinevertices, line.p0);
				Vector2d OnPoint1 = isOnTmpOutlineLoop(outlinevertices, line.p1);

				if (OnPoint0 != null && OnPoint0 == OnPoint1) {
					ORIPA.doc.removeLine(line);
					bDeleteLine = true;
					break;
				}

				if ((OnPoint0 == null && isOutsideOfTmpOutlineLoop(outlinevertices, line.p0))
						|| (OnPoint1 == null && isOutsideOfTmpOutlineLoop(outlinevertices, line.p1))) {
					ORIPA.doc.removeLine(line);
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
