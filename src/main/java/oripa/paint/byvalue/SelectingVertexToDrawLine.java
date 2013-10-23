package oripa.paint.byvalue;

import javax.vecmath.Vector2d;

import oripa.ORIPA;
import oripa.geom.GeomUtil;
import oripa.paint.PaintContextInterface;
import oripa.paint.core.PaintConfig;
import oripa.paint.core.PickingVertex;
import oripa.value.OriLine;

public class SelectingVertexToDrawLine extends PickingVertex {

	@Override
	protected void initialize() {

	}

	@Override
	protected void onResult(PaintContextInterface context) {
		Vector2d vertex = context.getVertex(0);

		double length;
		double angle;
		try {
			ValueDB valDB = ValueDB.getInstance();
			length = valDB.getLength();
			angle = valDB.getAngle();


			if (length > 0) {
				OriLine vl = GeomUtil.getLineByValue(vertex, length, -angle, PaintConfig.inputLineType);

				ORIPA.doc.pushUndoInfo();
				ORIPA.doc.addLine(vl);
			}
		} 
		catch (Exception ex) {
		}

		context.clear(false);
	}


}
