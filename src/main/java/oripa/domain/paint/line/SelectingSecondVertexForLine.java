package oripa.domain.paint.line;

import java.util.Optional;

import javax.vecmath.Vector2d;

import oripa.domain.cptool.Painter;
import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.PickingVertex;
import oripa.geom.GeomUtil;
import oripa.geom.Segment;
import oripa.value.OriLine;

public class SelectingSecondVertexForLine extends PickingVertex {

	public SelectingSecondVertexForLine() {
		super();
	}

	@Override
	protected void onResult(final PaintContext context, final boolean doSpecial) {

		if (context.getVertexCount() != 2) {
			throw new RuntimeException();
		}

		Vector2d p0, p1;
		p0 = context.getVertex(0);
		p1 = context.getVertex(1);

		double paperSize = context.getCreasePattern().getPaperSize();

		Vector2d dir = new Vector2d(p0.x - p1.x, p0.y - p1.y);
		dir.normalize();
		dir.scale(paperSize * 8);

		// create new line
		Segment line = new Segment(p0.x - dir.x, p0.y - dir.y,
				p0.x + dir.x, p0.y + dir.y);

		Optional<Segment> clippedOpt = GeomUtil.clipLine(line, context.getPaperDomain());
		clippedOpt.ifPresent(clippedSeg -> {
			// add new line to crease pattern
			context.creasePatternUndo().pushUndoInfo();
			Painter painter = context.getPainter();
			painter.addLine(new OriLine(clippedSeg, context.getLineTypeOfNewLines()));
		});

		context.clear(false);
	}

	@Override
	protected void initialize() {
		setPreviousClass(SelectingFirstVertexForLine.class);
		setNextClass(SelectingFirstVertexForLine.class);

		// System.out.println("SelectingSecondVertex.initialize() is called");
	}
}
