package oripa.domain.paint.util;

import java.awt.BasicStroke;
import java.awt.Color;

import oripa.value.OriLine;

public class ElementSelector {

	public Color selectColorByLineType(final OriLine.Type lineType) {
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
			stroke = new BasicStroke(1.5f / (float) scale, BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_MITER);
			break;
		case CUT_MODEL:
			stroke = new BasicStroke(4.0f / (float) scale, BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_MITER);
			break;
		default:
			stroke = new BasicStroke(1.5f / (float) scale, BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_MITER);
		}

		return stroke;
	}

	public Color getSelectedItemColor() {
		return Color.GREEN;
	}

	public BasicStroke createSelectedLineStroke(final double scale) {
		return new BasicStroke(1.5f / (float) scale, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
	}

	public Color getCandidateItemColor() {
		return Color.GREEN;
	}

	public BasicStroke createCandidateLineStroke(final double scale) {
		return new BasicStroke(1.5f / (float) scale, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
	}

	// FIXME: not to be in this class.
	public Color getCutModelColorForModelView() {
		return Color.RED;
	}

	// FIXME: not to be in this class.
	public BasicStroke createCutModelLineStrokeForModelView(final double scale) {
		return new BasicStroke(1.5f / (float) scale, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
	}

	public Color getEditingOutlineColor() {
		return Color.GREEN;
	}

	public BasicStroke createEditingOutlineStroke(final double scale) {
		return new BasicStroke(4.0f / (float) scale, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
	}

	public Color getAreaSelectionColor() {
		return Color.BLACK;
	}

	public BasicStroke createAreaSelectionStroke(final double scale) {
		final float[] dash = { 3.0f };
		return new BasicStroke(1.5f / (float) scale,
				BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
	}
}
