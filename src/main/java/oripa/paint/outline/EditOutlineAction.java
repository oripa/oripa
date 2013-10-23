package oripa.paint.outline;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.util.Collection;
import java.util.List;

import javax.vecmath.Vector2d;

import oripa.paint.EditMode;
import oripa.paint.PaintContextInterface;
import oripa.paint.core.GraphicMouseAction;
import oripa.paint.core.LineSetting;
import oripa.paint.util.PairLoop;

public class EditOutlineAction extends GraphicMouseAction {

	public EditOutlineAction(){
		setActionState(new SelectingVertexForOutline());
		setEditMode(EditMode.OTHER);
	}


	private class DrawTempOutlines implements PairLoop.Block<Vector2d>{

		private Graphics2D g2d;

		public void execute(Graphics2D g2d, Collection<Vector2d> outlineVertices){
			this.g2d = g2d;

			g2d.setColor(Color.GREEN);
			g2d.setStroke(LineSetting.STROKE_TMP_OUTLINE);

			if(outlineVertices.size() > 1){
				PairLoop.iterateWithCount(
					outlineVertices, outlineVertices.size() - 1, this);
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
	public void onDraw(Graphics2D g2d, PaintContextInterface context) {
		// TODO Auto-generated method stub
		super.onDraw(g2d, context);

		this.drawPickCandidateVertex(g2d, context);
		
		List<Vector2d> outlinevertices = context.getVertices();

		// Shows the outline of the editing
		int outlineVnum = outlinevertices.size();

		if (outlineVnum != 0) {

			(new DrawTempOutlines()).execute(g2d, outlinevertices);

			Vector2d cv = (context.getPickCandidateV() == null)
					? new Vector2d(context.getLogicalMousePoint().getX(), context.getLogicalMousePoint().getY())
			: context.getPickCandidateV();
					g2d.draw(new Line2D.Double(outlinevertices.get(0).x, 
							outlinevertices.get(0).y,
							cv.x, cv.y));
					g2d.draw(new Line2D.Double(outlinevertices.get(outlineVnum - 1).x,
							outlinevertices.get(outlineVnum - 1).y, cv.x, cv.y));
		}

	}



	@Override
	public void onPress(PaintContextInterface context, AffineTransform affine,
			boolean differentAction) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDrag(PaintContextInterface context, AffineTransform affine,
			boolean differentAction) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRelease(PaintContextInterface context, AffineTransform affine,
			boolean differentAction) {
		// TODO Auto-generated method stub

	}

}
