package oripa.paint.colorscheme;

import oripa.value.OriLine;

import java.awt.*;

public class SolarizedDarkColorScheme implements ColorScheme {
	private static final Color WHITE       = new Color(0x002B36);
	private static final Color CREAM       = new Color(0x073642);
	private static final Color LIGHT_GRAY  = new Color(0x839496);
	private static final Color MEDIUM_GRAY = new Color(0x586E75);
	private static final Color RED         = new Color(0xDC322F);
	private static final Color BLUE        = new Color(0x268BD2);
	private static final Color YELLOW      = new Color(0xB58900);
	private static final Color CYAN        = new Color(0x2AA198);

	@Override
	public Color getBackgroundColor() {
		return CREAM;
	}

	@Override
	public Color getPaperColor() {
		return WHITE;
	}

	@Override
	public Color getColorForLine(int lineType) {
		switch (lineType) {
			case OriLine.TYPE_VALLEY:
				return BLUE;

			case OriLine.TYPE_RIDGE:
				return RED;

			case OriLine.TYPE_NONE:
				return MEDIUM_GRAY;

			case OriLine.TYPE_CUT:
				return LIGHT_GRAY;

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
		return MEDIUM_GRAY;
	}

	@Override
	public Color getSelectionColor() {
		return YELLOW;
	}

	@Override
	public Color getCandidateColor() {
		return CYAN;
	}

	@Override
	public Color getUiOverlayColor() {
		return MEDIUM_GRAY;
	}
}
