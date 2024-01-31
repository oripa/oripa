package oripa.domain.paint.linetoline;

import oripa.domain.cptool.LineToLineAxiom;
import oripa.domain.cptool.PseudoLineFactory;
import oripa.domain.paint.PaintContext;
import oripa.domain.paint.core.SnapPointFactory;
import oripa.domain.paint.core.ValidatablePaintCommand;

public class LineToLineAxiomSnapPointSetterCommand extends ValidatablePaintCommand {

	private final PaintContext context;

	public LineToLineAxiomSnapPointSetterCommand(final PaintContext context) {
		this.context = context;
	}

	@Override
	public void execute() {
		final int lineCount = 2;
		final int vertexCount = 0;
		validateCounts(context, vertexCount, lineCount);

		var axiom = new LineToLineAxiom();

		var foldLines = axiom.createFoldLines(context.getLine(0), context.getLine(1), context.getPointEps());

		var snapPointFactory = new SnapPointFactory();

		var pseudoLineFactory = new PseudoLineFactory();

		var snapPoints = foldLines.stream()
				.map(line -> pseudoLineFactory.create(line, context.getCreasePattern().getPaperSize()))
				.flatMap(line -> snapPointFactory
						.createSnapPoints(context.getCreasePattern(), line, context.getPointEps()).stream())
				.toList();

		context.setSnapPoints(snapPoints);
	}

}
