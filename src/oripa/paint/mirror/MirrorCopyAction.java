package oripa.paint.mirror;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

import oripa.ORIPA;
import oripa.geom.OriLine;
import oripa.paint.GraphicMouseAction;
import oripa.paint.MouseContext;

public class MirrorCopyAction extends GraphicMouseAction {

	
	public MirrorCopyAction(MouseContext context){
		setActionState(new SelectingLineForMirror());
		
		recover(context);
	}
	
	
	
//	private OriLine closeLine = null;
//
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
	public void onDestroy(MouseContext context) {
		context.clear(false);
	}



	@Override
	public void onRightClick(MouseContext context, AffineTransform affine,
			MouseEvent event) {
		// TODO Auto-generated method stub
		if(context.getLineCount() > 0){
			super.onRightClick(context, affine, event);
		}
		else {
			ORIPA.doc.loadUndoInfo();
		}
	}



	@Override
	public void onDragged(MouseContext context, AffineTransform affine,
			MouseEvent event) {

	}

	@Override
	public void onReleased(MouseContext context, AffineTransform affine,
			MouseEvent event) {

	}

	@Override
	public void onDraw(Graphics2D g2d, MouseContext context) {

		super.onDraw(g2d, context);

		drawPickCandidateLine(g2d, context);
	}



	@Override
	public void onPressed(MouseContext context, AffineTransform affine,
			MouseEvent event) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void recover(MouseContext context) {
		context.clear(false);
		
		for(OriLine line : ORIPA.doc.lines){
			if(line.selected){
				context.pushLine(line);
			}
		}
	}
	
	

	
}
