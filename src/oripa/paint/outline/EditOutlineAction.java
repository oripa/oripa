package oripa.paint.outline;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.util.Collection;
import java.util.Stack;

import javax.vecmath.Vector2d;

import oripa.Config;
import oripa.paint.GraphicMouseAction;
import oripa.paint.MouseContext;
import oripa.paint.PairLoop;

public class EditOutlineAction extends GraphicMouseAction {

	public EditOutlineAction(){
		setActionState(new SelectingVertexForOutline());
	}


	private class DrawTempOutlines implements PairLoop.Block<Vector2d>{

		private Graphics2D g2d;

		public void execute(Graphics2D g2d, Collection<Vector2d> outlineVertice){
			this.g2d = g2d;

			g2d.setColor(Color.GREEN);
			g2d.setStroke(Config.STROKE_TMP_OUTLINE);

			if(outlineVertice.size() > 1){
				PairLoop.iterateWithCount(
					outlineVertice, outlineVertice.size() - 1, this);
			}
		}

		@Override
		public boolean yield(Vector2d p0,
				Vector2d p1) {

			g2d.draw(new Line2D.Double(p0.x, p0.y, p1.x, p1.y));


			return true;
		}

	}

	@Override
	public void onDraw(Graphics2D g2d, MouseContext context) {
		// TODO Auto-generated method stub
		super.onDraw(g2d, context);

		this.drawPickCandidateVertex(g2d, context);
		
		Stack<Vector2d> outlineVertice = context.getVertices();

		// Shows of the outline of the editing
		int outlineVnum = outlineVertice.size();

		if (outlineVnum != 0) {

			(new DrawTempOutlines()).execute(g2d, outlineVertice);

			Vector2d cv = (context.pickCandidateV == null)
					? new Vector2d(context.getMousePoint().getX(), context.getMousePoint().getY())
			: context.pickCandidateV;
					g2d.draw(new Line2D.Double(outlineVertice.get(0).x, 
							outlineVertice.get(0).y,
							cv.x, cv.y));
					g2d.draw(new Line2D.Double(outlineVertice.get(outlineVnum - 1).x,
							outlineVertice.get(outlineVnum - 1).y, cv.x, cv.y));
		}

	}



	@Override
	public void onPressed(MouseContext context, AffineTransform affine,
			MouseEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDragged(MouseContext context, AffineTransform affine,
			MouseEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReleased(MouseContext context, AffineTransform affine,
			MouseEvent event) {
		// TODO Auto-generated method stub

	}

}
