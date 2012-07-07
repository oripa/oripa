package oripa.paint.line;

import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import javax.vecmath.Vector2d;

import oripa.Constants;
import oripa.Globals;
import oripa.ORIPA;
import oripa.geom.GeomUtil;
import oripa.geom.OriLine;
import oripa.paint.segment.TwoPointSegmentAction;

public class TwoPointLineAction extends TwoPointSegmentAction {


	public TwoPointLineAction(){
		setActionState(new SelectingFirstVertexForLine());
	}

}
