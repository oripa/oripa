package oripa.paint.outline;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.vecmath.Vector2d;

import oripa.ORIPA;
import oripa.geom.GeomUtil;
import oripa.geom.Line;
import oripa.geom.OriLine;
import oripa.paint.Globals;
import oripa.paint.MouseContext;
import oripa.paint.PairLoop;
import oripa.paint.PickingVertex;
import oripa.view.UIPanelSettingDB;

public class SelectingVertexForOutline extends PickingVertex {

	
	@Override
	protected void initialize() {
		
	}
	
	
	

	@Override
	protected boolean onAct(MouseContext context, Point2D.Double currentPoint,
			boolean freeSelection) {
		return super.onAct(context, currentPoint, freeSelection);
	}




	@Override
	protected void onResult(MouseContext context) {
		
		Vector2d v = context.popVertex();
		
        boolean bClose = false;
        for (Vector2d tv : context.getVertices()) {
            if (GeomUtil.Distance(v, tv) < 1) {
                bClose = true;
                break;
            }
        }

        if (bClose) {
            if (context.getVertexCount() > 2) {
            	ORIPA.doc.pushUndoInfo();
                closeTmpOutline(context.getVertices());
                context.clear(false);
            }
        } else {
        	context.pushVertex(v);
        }

	}

	
	
    private void closeTmpOutline(Collection<Vector2d> outlineVertice) {
        // Delete the current outline
        ArrayList<OriLine> outlines = new ArrayList<>();
        for (OriLine line : ORIPA.doc.lines) {
            if (line.typeVal == OriLine.TYPE_CUT) {
                outlines.add(line);
            }
        }
        for (OriLine line : outlines) {
            ORIPA.doc.lines.remove(line);
        }

        // Update the contour line
        PairLoop.iterateAll(outlineVertice, new PairLoop.Block<Vector2d>(){
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
            for (OriLine line : ORIPA.doc.lines) {
                if (line.typeVal == OriLine.TYPE_CUT) {
                    continue;
                }
                Vector2d OnPoint0 = isOnTmpOutlineLoop(outlineVertice, line.p0);
                Vector2d OnPoint1 = isOnTmpOutlineLoop(outlineVertice, line.p1);

                if (OnPoint0 != null && OnPoint0 == OnPoint1) {
                    ORIPA.doc.removeLine(line);
                    bDeleteLine = true;
                    break;
                }

                if ((OnPoint0 == null && isOutsideOfTmpOutlineLoop(outlineVertice, line.p0))
                        || (OnPoint1 == null && isOutsideOfTmpOutlineLoop(outlineVertice, line.p1))) {
                    ORIPA.doc.removeLine(line);
                    bDeleteLine = true;
                    break;
                }
            }
            if (!bDeleteLine) {
                break;
            }
        }

        outlineVertice.clear();
        Globals.editMode = Globals.preEditMode;
        UIPanelSettingDB.getInstance().updateUIPanel();
//        ORIPA.mainFrame.uiPanel.modeChanged();
    }
    

    
    
    private Vector2d isOnTmpOutlineLoop(
    		Collection<Vector2d> outlineVertice, Vector2d v) {

    	return (new IsOnTempOutlineLoop()).execute(outlineVertice, v);
    }

    private boolean isOutsideOfTmpOutlineLoop(    			
    		Collection<Vector2d> outlineVertice, Vector2d v) {

    	return(new IsOutsideOfTempOutlineLoop()).execute(outlineVertice, v);
    }


}
