package oripa.paint.segment;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D.Double;

import javax.media.j3d.GraphStructureChangeListener;
import javax.vecmath.Vector2d;

import oripa.Config;
import oripa.ORIPA;
import oripa.geom.GeomUtil;
import oripa.geom.OriLine;
import oripa.paint.ElementSelector;
import oripa.paint.Globals;
import oripa.paint.GraphicMouseAction;
import oripa.paint.MouseContext;

public class TwoPointSegmentAction extends GraphicMouseAction {

	
	
	public TwoPointSegmentAction(){
		setActionState(new SelectingFirstVertexForSegment());
	}
	

	

	@Override
	public void onDrag(MouseContext context, AffineTransform affine, MouseEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRelease(MouseContext context, AffineTransform affine,
			MouseEvent event) {
		// TODO Auto-generated method stub

	}

	
	@Override
	public void onDraw(Graphics2D g2d, MouseContext context) {

		super.onDraw(g2d, context);
		

		drawTemporaryLine(g2d, context);
		drawPickCandidateVertex(g2d, context);


	}

}
