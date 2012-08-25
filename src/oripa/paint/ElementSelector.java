package oripa.paint;

import java.awt.BasicStroke;
import java.awt.Color;

import oripa.Config;
import oripa.geom.OriLine;

public class ElementSelector {

	public Color selectColorByPickupOrder(int order, int count){
		if(order == count - 1){
			return Color.GREEN;
		}
		
		return selectColorByLineType(Globals.inputLineType);
	}
	
	public Color selectLineColor(OriLine line){

		Color color;
		
		if(line.selected){
			color = Config.LINE_COLOR_CANDIDATE;
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
            color = Config.LINE_COLOR_AUX;
            break;
        case OriLine.TYPE_CUT:
            color = Color.BLACK;
            break;
        case OriLine.TYPE_RIDGE:
            color = Config.LINE_COLOR_RIDGE;
            break;
        case OriLine.TYPE_VALLEY:
        	color = Config.LINE_COLOR_VALLEY;
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
			stroke = Config.STROKE_CUT;
			break;
		case OriLine.TYPE_CUT:
			stroke = Config.STROKE_CUT;
			break;
		case OriLine.TYPE_RIDGE:
			stroke = Config.STROKE_RIDGE;
			break;
		case OriLine.TYPE_VALLEY:
			stroke = Config.STROKE_VALLEY;
			break;
		default:
			stroke = Config.STROKE_CUT;
		}
		
		return stroke;
	}
	
}
