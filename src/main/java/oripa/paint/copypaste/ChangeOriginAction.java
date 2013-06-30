package oripa.paint.copypaste;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D.Double;
import java.util.Collection;

import javax.vecmath.Vector2d;

import oripa.geom.OriLine;
import oripa.paint.GraphicMouseAction;
import oripa.paint.PaintContext;
import oripa.paint.geometry.GeometricOperation;

public class ChangeOriginAction extends GraphicMouseAction{


	
	@Override
	public GraphicMouseAction onLeftClick(PaintContext context,
			AffineTransform affine, boolean keepDoing) {

		return this;
	}
	
	@Override
	public void doAction(PaintContext context, Double point,
			boolean differntAction) {

	}
	
	@Override
	public void undo(PaintContext context) {
	}
	
	@Override
	public void onPress(PaintContext context, AffineTransform affine,
			boolean differentAction) {
		
	}

	@Override
	public void onDrag(PaintContext context, AffineTransform affine,
			boolean differentAction) {
		
	}

	@Override
	public void onRelease(PaintContext context, AffineTransform affine,
			boolean differentAction) {
		
	}

	@Override
	public Vector2d onMove(PaintContext context, AffineTransform affine,
			boolean differentAction) {
		Vector2d closeVertex = GeometricOperation.pickVertexFromPickedLines(context);
		context.pickCandidateV = closeVertex;
		
		if(closeVertex != null){
			OriginHolder holder = OriginHolder.getInstance();
			holder.setOrigin(closeVertex);
		}
		
		return closeVertex;
	}
	
	
	@Override
	public void onDraw(Graphics2D g2d, PaintContext context) {
		super.onDraw(g2d, context);		

		Collection<OriLine> lines = context.getLines();

		g2d.setColor(Color.MAGENTA);
		
		for(OriLine line : lines){
			this.drawVertex(g2d, context, line.p0.x, line.p0.y);
			this.drawVertex(g2d, context, line.p1.x, line.p1.y);
		}
		
		this.drawPickCandidateVertex(g2d, context);
	}
}
