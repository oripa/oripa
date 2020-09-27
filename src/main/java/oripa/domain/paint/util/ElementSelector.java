package oripa.domain.paint.util;

import java.awt.BasicStroke;
import java.awt.Color;

import oripa.value.OriLine;

public class ElementSelector {

	public Color getColor(final OriLine.Type lineType) {
		Color color;

		switch (lineType) {
		case NONE:
			color = Color.LIGHT_GRAY;
			break;
		case CUT:
			color = Color.BLACK;
			break;
		case RIDGE:
			color = Color.RED;
			break;
		case VALLEY:
			color = Color.BLUE;
			break;
		case CUT_MODEL:
			color = Color.MAGENTA;
			break;
		default:
			color = Color.BLACK;
		}

		return color;
	}

	public BasicStroke createStroke(final OriLine.Type lineType, final double scale) {
		BasicStroke stroke;
		switch (lineType) {
		case NONE:
		case CUT:
		case RIDGE:
		case VALLEY:
			stroke = new BasicStroke(createThinLineWidth(scale), BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_MITER);
			break;
		case CUT_MODEL:
			stroke = new BasicStroke(createThickLineWidth(scale), BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_MITER);
			break;
		default:
			stroke = new BasicStroke(createThinLineWidth(scale), BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_MITER);
		}

		return stroke;
	}

	public float createVeryThickLineWidth(final double scale) {
		return 10.0f / (float) scale;
	}

	public float createThickLineWidth(final double scale) {
		return 4.0f / (float) scale;
	}

	public float createThinLineWidth(final double scale) {
		return 1.5f / (float) scale;
	}

	public Color getSelectedItemColor() {
		return Color.GREEN;
	}

	public BasicStroke createSelectedLineStroke(final double scale) {
		return new BasicStroke(createThinLineWidth(scale), BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER);
	}

	public Color getCandidateItemColor() {
		return Color.GREEN;
	}

	public BasicStroke createCandidateLineStroke(final double scale) {
		return new BasicStroke(createThinLineWidth(scale), BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER);
	}

	public Color getEditingOutlineColor() {
		return Color.GREEN;
	}

	public Color getAssistLineColor() {
		return Color.MAGENTA;
	}

	public BasicStroke createEditingOutlineStroke(final double scale) {
		return new BasicStroke(createThickLineWidth(scale), BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER);
	}

	public Color getAreaSelectionColor() {
		return Color.BLACK;
	}

	public BasicStroke createAreaSelectionStroke(final double scale) {
		final float[] dash = { 3.0f };
		return new BasicStroke(createThinLineWidth(scale),
				BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
	}

	public Color getOverlappingLineHighlightColor() {
		return new Color(255, 0, 255, 30);
	}

	public BasicStroke createOverlappingLineHighlightStroke(final double scale) {
		return new BasicStroke(createVeryThickLineWidth(scale), BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER);
	}
}
