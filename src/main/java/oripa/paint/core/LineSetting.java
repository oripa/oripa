package oripa.paint.core;

import java.awt.BasicStroke;
import java.awt.Color;

public class LineSetting {

	//   final public static Color LINE_COLOR_CUT = Color.BLACK;
	final public static Color LINE_COLOR_VALLEY = Color.BLUE;
	final public static Color LINE_COLOR_RIDGE = Color.RED;
	final public static Color LINE_COLOR_AUX = Color.LIGHT_GRAY;
	final public static Color LINE_COLOR_GRID = Color.LIGHT_GRAY;
	final public static Color LINE_COLOR_CANDIDATE = Color.GREEN;
	final public static Color LINE_COLOR_CANDIDATE2 = Color.MAGENTA;
	final public static Color LINE_COLOR_PICKED = Color.GREEN;
	final public static BasicStroke STROKE_CUT = new BasicStroke(0.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
	final public static BasicStroke STROKE_VALLEY = new BasicStroke(0.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
	final public static BasicStroke STROKE_RIDGE = new BasicStroke(0.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
	final public static BasicStroke STROKE_PICKED = new BasicStroke(0.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
	final public static BasicStroke STROKE_GRID = new BasicStroke(0.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
	final public static BasicStroke STROKE_MOVING = new BasicStroke(0.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);

	final static float dash[] = {3.0f};

	final public static BasicStroke STROKE_SELECT_BY_AREA = new BasicStroke(0.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);

	// Editing outlines (?)
	final public static BasicStroke STROKE_TMP_OUTLINE = new BasicStroke(3.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
	final public static BasicStroke MODEL_STROKE_CUT = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);

}
