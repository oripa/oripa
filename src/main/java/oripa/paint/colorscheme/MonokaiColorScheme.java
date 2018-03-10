package oripa.paint.colorscheme;

import oripa.value.OriLine;

import java.awt.*;

public class MonokaiColorScheme implements ColorScheme {
	private static final Color WHITE       = new Color(0xF8F8F0);
	private static final Color LIGHT_GRAY  = new Color(0x727072);
	private static final Color MEDIUM_GRAY = new Color(0x2D2A2E);
	private static final Color DARK_GRAY   = new Color(0x221F22);
	private static final Color PINK        = new Color(0xFF6188);
	private static final Color BLUE        = new Color(0x78DCE8);
	private static final Color GREEN       = new Color(0xA9DC76);
	private static final Color PURPLE      = new Color(0xFFD569);

	@Override
	public Color getBackgroundColor() {
		return DARK_GRAY;
	}

	@Override
	public Color getPaperColor() {
		return MEDIUM_GRAY;
	}

	@Override
	public Color getColorForLine(int lineType) {
		switch (lineType) {
			case OriLine.TYPE_VALLEY:
				return BLUE;

			case OriLine.TYPE_RIDGE:
				return PINK;

			case OriLine.TYPE_NONE:
				return LIGHT_GRAY;

			case OriLine.TYPE_CUT:
				return WHITE;

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
		return LIGHT_GRAY;
	}

	@Override
	public Color getSelectionColor() {
		return PURPLE;
	}

	@Override
	public Color getCandidateColor() {
		return GREEN;
	}

	@Override
	public Color getUiOverlayColor() {
		return WHITE;
	}
}
