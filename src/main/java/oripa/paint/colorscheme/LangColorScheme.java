package oripa.paint.colorscheme;

import oripa.value.OriLine;

import java.awt.*;

public class LangColorScheme implements ColorScheme {
	private static final Color BLACK       = new Color(0x231F20);
	private static final Color BLUE        = new Color(0x3488A8);
	private static final Color CREAM     = new Color(0xECDFB8);
	private static final Color WHITE        = new Color(0xFFFFFF);
	private static final Color MAGENTA     = new Color(0xD33682);
	private static final Color GREEN = new Color(0xA9DC76);

	@Override
	public Color getBackgroundColor() {
		return WHITE;
	}

	@Override
	public Color getPaperColor() {
		return CREAM;
	}

	@Override
	public Color getColorForLine(int lineType) {
		switch (lineType) {
			case OriLine.TYPE_VALLEY:
				return BLUE;

			case OriLine.TYPE_RIDGE:
				return BLACK;

			case OriLine.TYPE_NONE:
				return BLACK;

			case OriLine.TYPE_CUT:
				return BLACK;

			default:
				return Color.BLACK;
		}
	}

	@Override
	public Stroke getStrokeForLine(int lineType, double scale) {
		switch (lineType) {
			case OriLine.TYPE_VALLEY:
				float dash[] = {3.0f};
				return new BasicStroke((float) (1.0 / scale), BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10f, dash, 0);

			case OriLine.TYPE_NONE:
				return new BasicStroke((float) (0.25 / scale), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);

			case OriLine.TYPE_CUT:
				return new BasicStroke((float) (2.0 / scale), BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);

			default:
				return new BasicStroke((float) (1.0 / scale), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
		}
	}

	@Override
	public Color getVertexColor() {
		return BLACK;
	}

	@Override
	public Color getSelectionColor() {
		return MAGENTA;
	}

	@Override
	public Color getCandidateColor() {
		return GREEN;
	}

	@Override
	public Color getUiOverlayColor() {
		return BLACK;
	}
}
