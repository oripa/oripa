package oripa.domain.paint.byvalue;

import javax.vecmath.Vector2d;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.cptool.Painter;
import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.PickingVertex;
import oripa.geom.GeomUtil;
import oripa.value.OriLine;

public class SelectingVertexToDrawLine extends PickingVertex {
	private static final Logger logger = LoggerFactory.getLogger(SelectingVertexToDrawLine.class);

	private final ValueSetting valueSetting;

	/**
	 * Constructor
	 */
	public SelectingVertexToDrawLine(final ValueSetting valueSetting) {
		super();
		this.valueSetting = valueSetting;
	}

	@Override
	protected void initialize() {

	}

	@Override
	protected void onResult(final PaintContext context, final boolean doSpecial) {
		logger.debug("start onResult()");

		if (context.getVertexCount() != 1 || context.getLineCount() > 0) {
			throw new IllegalStateException(
					"wrong state: impossible selection of vertex and lines.");
		}

		Vector2d vertex = context.popVertex();

		try {
			var length = valueSetting.getLength();
			var angle = valueSetting.getAngle();

			logger.debug("length = " + length);

			if (length > 0) {
				OriLine vl = new OriLine(GeomUtil.getLineByValue(vertex, length, -angle),
						context.getLineTypeOfNewLines());

				context.creasePatternUndo().pushUndoInfo();

				Painter painter = context.getPainter();
				painter.addLine(vl);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		logger.debug("end onResult()");

	}

}
