package oripa.paint.mirror;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

import oripa.Config;
import oripa.paint.GraphicMouseAction;
import oripa.paint.MouseContext;

public class MirrorCopyAction extends GraphicMouseAction {

	
	public MirrorCopyAction(){
		setActionState(new SelectingLineForMirror());
	}
	
	@Override
	public void onDrag(MouseContext context, AffineTransform affine,
			MouseEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRelease(MouseContext context, AffineTransform affine,
			MouseEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDraw(Graphics2D g2d, MouseContext context) {
		// TODO Auto-generated method stub
		super.onDraw(g2d, context);
		
		if(context.pickCandidateL != null){
			g2d.setColor(Config.LINE_COLOR_CANDIDATE);
			drawLine(g2d, context.pickCandidateL);
		}
	}
	
	

}
