package oripa.paint.mirror;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

import oripa.geom.OriLine;
import oripa.paint.GraphicMouseAction;
import oripa.paint.MouseContext;

public class MirrorCopyAction extends GraphicMouseAction {

	
	public MirrorCopyAction(){
		setActionState(new SelectingLineForMirror());
	}
	
	private OriLine closeLine = null;

//	@Override
//	public Vector2d onMove(MouseContext context, AffineTransform affine,
//			MouseEvent event) {
//		Vector2d result = super.onMove(context, affine, event);
//
//		if(closeLine != null){
//			closeLine.selected = false;
//		}
//			
//		closeLine = context.pickCandidateL;
//	
//		if(closeLine != null){
//			closeLine.selected = true;
//		}
//			
//		return result;
//	}

	@Override
	public void onDrag(MouseContext context, AffineTransform affine,
			MouseEvent event) {

	}

	@Override
	public void onRelease(MouseContext context, AffineTransform affine,
			MouseEvent event) {

	}

	@Override
	public void onDraw(Graphics2D g2d, MouseContext context) {

		super.onDraw(g2d, context);

		drawPickCandidateLine(g2d, context);
	}
	
	

}
