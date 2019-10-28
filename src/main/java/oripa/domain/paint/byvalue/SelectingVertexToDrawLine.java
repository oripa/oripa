package oripa.domain.paint.byvalue;

import javax.vecmath.Vector2d;

import oripa.domain.cptool.Painter;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.core.PickingVertex;
import oripa.geom.GeomUtil;
import oripa.value.OriLine;

public class SelectingVertexToDrawLine extends PickingVertex {

	@Override
	protected void initialize() {

	}

	@Override
	protected void onResult(final PaintContextInterface context, final boolean doSpecial) {
		if (context.getVertexCount() != 1 || context.getLineCount() > 0) {
			throw new IllegalStateException(
					"wrong state: impossible selection of vertex and lines.");
		}

		Vector2d vertex = context.popVertex();

		double length;
		double angle;
		try {
			ValueDB valDB = ValueDB.getInstance();
			length = valDB.getLength();
			angle = valDB.getAngle();

			if (length > 0) {
				OriLine vl = GeomUtil.getLineByValue(vertex, length, -angle,
						context.getLineTypeToDraw());

				context.creasePatternUndo().pushUndoInfo();

				Painter painter = context.getPainter();
				painter.addLine(vl);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
