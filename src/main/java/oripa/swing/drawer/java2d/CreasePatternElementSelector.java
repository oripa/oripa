package oripa.swing.drawer.java2d;

import java.awt.BasicStroke;
import java.awt.Color;

import oripa.value.OriLine;

public class CreasePatternElementSelector {

	public Color getColor(final OriLine.Type lineType) {

		return switch (lineType) {
		case AUX -> Color.LIGHT_GRAY;
		case CUT -> Color.BLACK;
		case MOUNTAIN -> Color.RED;
		case VALLEY -> Color.BLUE;
		case UNASSIGNED -> Color.ORANGE;
		case CUT_MODEL -> Color.MAGENTA;
		default -> Color.BLACK;
		};
	}

	public BasicStroke createStroke(final OriLine.Type lineType, final double scale,
			final boolean zeroWidth) {
		return switch (lineType) {
		case AUX, CUT, MOUNTAIN, VALLEY, UNASSIGNED -> new BasicStroke(
				createThinLineWidth(scale, zeroWidth),
				BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER);
		case CUT_MODEL -> new BasicStroke(
				createThickLineWidth(scale),
				BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER);
		default -> new BasicStroke(
				createThinLineWidth(scale, zeroWidth),
				BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER);
		};
	}

	private float createVeryThickLineWidth(final double scale) {
		return 10.0f / (float) scale;
	}

	private float createThickLineWidth(final double scale) {
		return 4.0f / (float) scale;
	}

	private float createThinLineWidth(final double scale, final boolean zeroWidth) {
		if (zeroWidth) {
			return 0;
		}
		return 1.5f / (float) scale;
	}

	public Color getSelectedItemColor() {
		return Color.GREEN;
	}

	public BasicStroke createSelectedLineStroke(final double scale, final boolean zeroWidth) {
		return new BasicStroke(createThinLineWidth(scale, zeroWidth), BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER);
	}

	public Color getCandidateItemColor() {
		return Color.GREEN;
	}

	public BasicStroke createCandidateLineStroke(final double scale, final boolean zeroWidth) {
		return new BasicStroke(createThinLineWidth(scale, zeroWidth), BasicStroke.CAP_BUTT,
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
		return new BasicStroke(createThinLineWidth(scale, false),
				BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
	}

	public Color getOverlappingLineHighlightColor() {
		return new Color(255, 0, 255, 30);
	}

	public BasicStroke createOverlappingLineHighlightStroke(final double scale) {
		return new BasicStroke(createVeryThickLineWidth(scale), BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER);
	}

	public Color getNormalVertexColor() {
		return Color.BLACK;
	}

	public double createNormalVertexSize(final double scale) {
		return 6.0 / scale;
	}

	public Color getViolatingVertexColor() {
		return Color.RED;
	}

	public Color getNormalFaceColor() {
		return new Color(255, 210, 210);
	}

	public Color getViolatingFaceColor() {
		return Color.MAGENTA;
	}

	public double createViolatingVertexSize(final double scale) {
		return 16.0 / scale;
	}

	public double createMouseActionVertexSize(final double scale) {
		return 10.0 / scale;
	}
}
