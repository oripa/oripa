package oripa.domain.paint.byvalue;

import javax.vecmath.Vector2d;

import oripa.domain.cptool.Painter;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.core.PaintConfig;
import oripa.domain.paint.core.PickingVertex;
import oripa.geom.GeomUtil;
import oripa.value.OriLine;

public class SelectingVertexToDrawLine extends PickingVertex {

	@Override
	protected void initialize() {

	}

	@Override
	protected void onResult(final PaintContextInterface context) {
		Vector2d vertex = context.getVertex(0);

		double length;
		double angle;
		try {
			ValueDB valDB = ValueDB.getInstance();
			length = valDB.getLength();
			angle = valDB.getAngle();

			if (length > 0) {
				OriLine vl = GeomUtil.getLineByValue(vertex, length, -angle,
						PaintConfig.inputLineType);

				context.creasePatternUndo().pushUndoInfo();

				Painter painter = context.getPainter();
				painter.addLine(vl);
			}
		} catch (Exception ex) {
		}

		context.clear(false);
	}

}
