package oripa.controller.paint.util;

import java.awt.BasicStroke;
import java.awt.Color;

import oripa.controller.paint.core.LineSetting;
import oripa.controller.paint.core.PaintConfig;
import oripa.value.OriLine;

public class ElementSelector {

	public Color selectColorByPickupOrder(int order, int count){
		if(order == count - 1){
			return Color.GREEN;
		}

		return selectColorByLineType(PaintConfig.inputLineType);
	}

	public Color selectLineColor(OriLine line){

		Color color;

		if(line.selected){
			color = LineSetting.LINE_COLOR_CANDIDATE;
		}
		else {
			color = selectColorByLineType(line.typeVal);
		}

		return color;

	}


	public Color selectColorByLineType(int lineType){
		Color color;

		switch (lineType) {
		case OriLine.TYPE_NONE:
			color = LineSetting.LINE_COLOR_AUX;
			break;
		case OriLine.TYPE_CUT:
			color = Color.BLACK;
			break;
		case OriLine.TYPE_RIDGE:
			color = LineSetting.LINE_COLOR_RIDGE;
			break;
		case OriLine.TYPE_VALLEY:
			color = LineSetting.LINE_COLOR_VALLEY;
			break;
		case OriLine.TYPE_CUT_MODEL:
			color = LineSetting.LINE_COLOR_CUT_MODEL;
			break;
		default:
			color = Color.BLACK;
		}

		return color;
	}

	public BasicStroke selectStroke(int lineType){
		BasicStroke stroke;
		switch (lineType) {
		case OriLine.TYPE_NONE:
			stroke = LineSetting.STROKE_PAPER_EDGE;
			break;
		case OriLine.TYPE_CUT:
			stroke = LineSetting.STROKE_PAPER_EDGE;
			break;
		case OriLine.TYPE_RIDGE:
			stroke = LineSetting.STROKE_RIDGE;
			break;
		case OriLine.TYPE_VALLEY:
			stroke = LineSetting.STROKE_VALLEY;
			break;
		case OriLine.TYPE_CUT_MODEL:
			stroke = LineSetting.STROKE_TMP_OUTLINE;
			break;
		default:
			stroke = LineSetting.STROKE_PAPER_EDGE;
		}

		return stroke;
	}

}
