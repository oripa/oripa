package oripa.domain.paint.copypaste;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.vecmath.Vector2d;

import oripa.domain.cptool.Painter;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.core.PickingVertex;
import oripa.value.OriLine;

public class PastingOnVertex extends PickingVertex {

	private final SelectionOriginHolder originHolder;
	private final ShiftedLineFactory factory = new ShiftedLineFactory();

	/**
	 * Constructor
	 */
	public PastingOnVertex(final SelectionOriginHolder originHolder) {
		this.originHolder = originHolder;
	}

	@Override
	protected void initialize() {
	}

	@Override
	protected void undoAction(final PaintContextInterface context) {
		// context.setMissionCompleted(false);
		context.creasePatternUndo().undo();
	}

//	@Override
//	protected boolean onAct(final PaintContextInterface context, final Vector2d currentPoint,
//			final boolean freeSelection) {
//
//		Vector2d candidate = context.getCandidateVertexToPick();
//		if (candidate == null) {
//			return false;
//		}
//
//		context.pushVertex(candidate);
//
//		return true;
//	}

	@Override
	protected void onResult(final PaintContextInterface context, final boolean doSpecial) {

		Vector2d v = context.popVertex();

		if (context.getLineCount() == 0) {
			return;
		}

		context.creasePatternUndo().pushUndoInfo();

		Vector2d origin = originHolder.getOrigin(context);

		var offset = factory.createOffset(origin, v);

		Painter painter = context.getPainter();
		painter.addLines(
				shiftLines(context.getPickedLines(), offset.x, offset.y));
	}

	private List<OriLine> shiftLines(final Collection<OriLine> lines,
			final double diffX, final double diffY) {

		return lines.stream()
				.map(l -> factory.createShiftedLine(l, diffX, diffY))
				.collect(Collectors.toList());
	}

}
