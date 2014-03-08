package oripa.controller.paint.byvalue;

import javax.vecmath.Vector2d;

import oripa.controller.paint.PaintContextInterface;
import oripa.controller.paint.core.PaintConfig;
import oripa.controller.paint.core.PickingVertex;
import oripa.domain.cptool.Painter;
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

				context.getUndoer().pushUndoInfo();

				Painter painter = context.getPainter();
				painter.addLine(vl);
			}
		} catch (Exception ex) {
		}

		context.clear(false);
	}

}
