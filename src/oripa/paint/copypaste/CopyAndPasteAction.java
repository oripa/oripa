package oripa.paint.copypaste;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D.Double;

import javax.vecmath.Vector2d;

import oripa.ORIPA;
import oripa.paint.EditMode;
import oripa.paint.GraphicMouseAction;
import oripa.paint.PaintContext;

public class CopyAndPasteAction extends GraphicMouseAction {

	ChangeOriginAction originAction = new ChangeOriginAction();
	PasteAction pasteAction = new PasteAction();
	
	GraphicMouseAction action = pasteAction;

	
	public CopyAndPasteAction() {
		setEditMode(EditMode.INPUT);
		setNeedSelect(true);
	}
	
	@Override
	public void recover(PaintContext context) {
		action.recover(context);
	}
	
	@Override
	public void undo(PaintContext context) {
		ORIPA.doc.loadUndoInfo();
	}
	
	
	@Override
	public GraphicMouseAction onLeftClick(PaintContext context,
			AffineTransform affine, boolean differentAction) {
		action.onLeftClick(context, affine, differentAction);
		
		return this;
	}
	
	@Override
	public void doAction(PaintContext context, Double point,
			boolean differntAction) {
		// TODO Auto-generated method stub
		action.doAction(context, point, differntAction);
	}
	
	
	@Override
	public void onPress(PaintContext context, AffineTransform affine,
			boolean differentAction) {
		action.onPress(context, affine, differentAction);
	}

	@Override
	public void onDrag(PaintContext context, AffineTransform affine,
			boolean differentAction) {
		action.onDrag(context, affine, differentAction);
	}

	@Override
	public void onRelease(PaintContext context, AffineTransform affine,
			boolean differentAction) {
		action.onRelease(context, affine, differentAction);
	}
	
	
	public void changeAction(boolean changingOrigin){
		if(changingOrigin){
			action = originAction;
		}
		else {
			action = pasteAction;
		}
	}
	
	@Override
	public Vector2d onMove(PaintContext context, AffineTransform affine,
			boolean changingOrigin) {
		
		changeAction(changingOrigin);
		
		return action.onMove(context, affine, changingOrigin);
	}

	@Override
	public void onDraw(Graphics2D g2d, PaintContext context) {
		// TODO Auto-generated method stub
		action.onDraw(g2d, context);
	}
}
