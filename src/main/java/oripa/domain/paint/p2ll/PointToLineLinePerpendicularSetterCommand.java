package oripa.domain.paint.p2ll;

import oripa.domain.cptool.PointToLineLinePerpendicularAxiom;
import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.SnapPointFactory;
import oripa.domain.paint.core.ValidatablePaintCommand;

public class PointToLineLinePerpendicularSetterCommand extends ValidatablePaintCommand {

	private final PaintContext context;

	public PointToLineLinePerpendicularSetterCommand(final PaintContext context) {
		this.context = context;
	}

	@Override
	public void execute() {

		var correctVertexCount = 1;
		var correctLineCount = 2;
		validateCounts(context, correctVertexCount, correctLineCount);

		var p = context.getVertex(0);
		var s = context.getLine(0);
		var perpendicular = context.getLine(1);

		var lineOpt = new PointToLineLinePerpendicularAxiom().createFoldLine(p, s, perpendicular);

		lineOpt
				.map(line -> new SnapPointFactory().createSnapPoints(context.getCreasePattern(), line,
						context.getPointEps()))
				.ifPresent(points -> context.setSnapPoints(points));
	}

}
