package oripa.paint.colorscheme;

import oripa.value.OriLine;

import java.awt.*;

public class ClassicColorScheme implements ColorScheme {
	@Override
	public Color getBackgroundColor() {
		return Color.WHITE;
	}

	@Override
	public Color getPaperColor() {
		return Color.WHITE;
	}

	@Override
	public Color getColorForLine(int lineType) {
		switch (lineType) {
			case OriLine.TYPE_VALLEY:
				return Color.BLUE;

			case OriLine.TYPE_RIDGE:
				return Color.RED;

			case OriLine.TYPE_NONE:
				return Color.LIGHT_GRAY;

			case OriLine.TYPE_CUT:
				return Color.BLACK;

			default:
				return Color.BLACK;
		}
	}

	@Override
	public Stroke getStrokeForLine(int lineType, double scale) {
		return new BasicStroke((float) (0.5 / scale), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
	}

	@Override
	public Color getVertexColor() {
		return Color.BLACK;
	}

	@Override
	public Color getSelectionColor() {
		return Color.GREEN;
	}

	@Override
	public Color getCandidateColor() {
		return Color.MAGENTA;
	}

	@Override
	public Color getUiOverlayColor() {
		return Color.BLACK;
	}
}
