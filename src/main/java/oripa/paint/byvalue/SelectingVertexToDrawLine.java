package oripa.paint.byvalue;

import javax.vecmath.Vector2d;

import oripa.ORIPA;
import oripa.doc.Doc;
import oripa.geom.GeomUtil;
import oripa.paint.CreasePatternInterface;
import oripa.paint.PaintContextInterface;
import oripa.paint.core.PaintConfig;
import oripa.paint.core.PickingVertex;
import oripa.paint.cptool.Painter;
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

			Doc document = ORIPA.doc;
			CreasePatternInterface creasePattern = document.getCreasePattern();
			
			if (length > 0) {
				OriLine vl = GeomUtil.getLineByValue(vertex, length, -angle, PaintConfig.inputLineType);

				document.pushUndoInfo();

				Painter painter = new Painter();
				painter.addLine(vl, creasePattern);
			}
		} 
		catch (Exception ex) {
		}

		context.clear(false);
	}


}
