package oripa.domain.paint.core;

import java.awt.BasicStroke;
import java.awt.Color;

public class LineSetting {

	// final public static Color LINE_COLOR_CUT = Color.BLACK;
	public static final Color LINE_COLOR_VALLEY = Color.BLUE;
	public static final Color LINE_COLOR_RIDGE = Color.RED;
	public static final Color LINE_COLOR_AUX = Color.LIGHT_GRAY;
	public static final Color LINE_COLOR_GRID = Color.LIGHT_GRAY;
	public static final Color LINE_COLOR_CANDIDATE = Color.GREEN;
	public static final Color LINE_COLOR_CANDIDATE2 = Color.MAGENTA;
	public static final Color LINE_COLOR_PICKED = Color.GREEN;
	public static final Color LINE_COLOR_CUT_MODEL = Color.MAGENTA;

	public static final BasicStroke STROKE_PAPER_EDGE =
			new BasicStroke(0.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
	public static final BasicStroke STROKE_VALLEY = new BasicStroke(0.0f, BasicStroke.CAP_BUTT,
			BasicStroke.JOIN_MITER);
	public static final BasicStroke STROKE_RIDGE = new BasicStroke(0.0f, BasicStroke.CAP_BUTT,
			BasicStroke.JOIN_MITER);
	public static final BasicStroke STROKE_PICKED = new BasicStroke(0.0f, BasicStroke.CAP_BUTT,
			BasicStroke.JOIN_MITER);
	public static final BasicStroke STROKE_GRID = new BasicStroke(0.0f, BasicStroke.CAP_BUTT,
			BasicStroke.JOIN_MITER);
	public static final BasicStroke STROKE_MOVING = new BasicStroke(0.0f, BasicStroke.CAP_BUTT,
			BasicStroke.JOIN_MITER);

	static final float[] dash = { 3.0f };

	public static final BasicStroke STROKE_SELECT_BY_AREA = new BasicStroke(0.0f,
			BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);

	// Editing outlines (?)
	public static final BasicStroke STROKE_TMP_OUTLINE = new BasicStroke(3.0f,
			BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
	public static final BasicStroke STROKE_CUT_MODEL = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
			BasicStroke.JOIN_MITER);

}
