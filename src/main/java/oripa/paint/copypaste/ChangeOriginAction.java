package oripa.paint.copypaste;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D.Double;
import java.util.Collection;

import javax.vecmath.Vector2d;

import oripa.paint.GraphicMouseActionInterface;
import oripa.paint.PaintContextInterface;
import oripa.paint.core.GraphicMouseAction;
import oripa.paint.geometry.NearestVertexFinder;
import oripa.value.OriLine;

public class ChangeOriginAction extends GraphicMouseAction{


	
	@Override
	public GraphicMouseActionInterface onLeftClick(PaintContextInterface context,
			AffineTransform affine, boolean keepDoing) {

		return this;
	}
	
	@Override
	public void doAction(PaintContextInterface context, Double point,
			boolean differntAction) {

	}
	
	@Override
	public void undo(PaintContextInterface context) {
	}
	
	@Override
	public void onPress(PaintContextInterface context, AffineTransform affine,
			boolean differentAction) {
		
	}

	@Override
	public void onDrag(PaintContextInterface context, AffineTransform affine,
			boolean differentAction) {
		
	}

	@Override
	public void onRelease(PaintContextInterface context, AffineTransform affine,
			boolean differentAction) {
		
	}

	@Override
	public Vector2d onMove(PaintContextInterface context, AffineTransform affine,
			boolean differentAction) {
		Vector2d closeVertex = NearestVertexFinder.pickVertexFromPickedLines(context);
		context.setCandidateVertexToPick(closeVertex);
		
		if(closeVertex != null){
			OriginHolder holder = OriginHolder.getInstance();
			holder.setOrigin(closeVertex);
		}
		
		return closeVertex;
	}
	
	
	@Override
	public void onDraw(Graphics2D g2d, PaintContextInterface context) {
		super.onDraw(g2d, context);		

		Collection<OriLine> lines = context.getPickedLines();

		g2d.setColor(Color.MAGENTA);
		
		for(OriLine line : lines){
			this.drawVertex(g2d, context, line.p0.x, line.p0.y);
			this.drawVertex(g2d, context, line.p1.x, line.p1.y);
		}
		
		this.drawPickCandidateVertex(g2d, context);
	}
}
