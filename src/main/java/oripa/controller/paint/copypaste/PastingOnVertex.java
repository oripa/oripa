package oripa.controller.paint.copypaste;

import java.awt.geom.Point2D.Double;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Vector2d;

import oripa.controller.paint.PaintContextInterface;
import oripa.controller.paint.core.PickingVertex;
import oripa.domain.cptool.Painter;
import oripa.value.OriLine;

public class PastingOnVertex extends PickingVertex {

	@Override
	protected void initialize() {
	}

	@Override
	protected void undoAction(final PaintContextInterface context) {
		context.setMissionCompleted(false);
		context.creasePatternUndo().loadUndoInfo();
	}

	@Override
	protected boolean onAct(final PaintContextInterface context, final Double currentPoint,
			final boolean freeSelection) {

		Vector2d candidate = context.getCandidateVertexToPick();
		if (candidate == null) {
			return false;
		}

		context.pushVertex(candidate);

		return true;
	}

	@Override
	protected void onResult(final PaintContextInterface context) {

		Vector2d v = context.popVertex();

		if (context.getLineCount() > 0) {
			context.creasePatternUndo().pushUndoInfo();

			Vector2d origin = OriginHolder.getInstance().getOrigin(context);

			double ox = origin.x;
			double oy = origin.y;

			List<OriLine> shiftedLines;
			shiftedLines =
					shiftLines(
							context.getPickedLines(), v.x - ox, v.y - oy);

//            for(int i = 0; i < context.getLineCount(); i++){
//            	ORIPA.doc.addLine(shiftedLines.get(i));
//            }

			Painter painter = context.getPainter();
			painter.pasteLines(shiftedLines);

			context.setMissionCompleted(true);
		}

	}

	private List<OriLine> shiftLines(final Collection<OriLine> lines,
			final double diffX, final double diffY) {

		List<OriLine> shiftedLines = new LinkedList<>();

		int i = 0;
		for (OriLine l : lines) {
			OriLine shifted = new OriLine();

			shifted.p0.x = l.p0.x + diffX;
			shifted.p0.y = l.p0.y + diffY;

			shifted.p1.x = l.p1.x + diffX;
			shifted.p1.y = l.p1.y + diffY;

			shifted.typeVal = l.typeVal;

			shiftedLines.add(shifted);
		}

		return shiftedLines;
	}

}
