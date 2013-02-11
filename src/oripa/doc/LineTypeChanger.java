package oripa.doc;

import java.util.Collection;

import oripa.doc.command.RemoveElement;
import oripa.geom.OriLine;

//public 
class LineTypeChanger {

	public void alterLineType(OriLine l, Collection<OriLine> lines,
    		TypeForChange from, TypeForChange to) {
        if (from == TypeForChange.RIDGE  && l.typeVal != OriLine.TYPE_RIDGE) {
            return;
        }
        if (from == TypeForChange.VALLEY && l.typeVal != OriLine.TYPE_VALLEY) {
            return;
        }

        switch (to) {
            case RIDGE:
                l.typeVal = OriLine.TYPE_RIDGE;
                break;
            case VALLEY:
                l.typeVal = OriLine.TYPE_VALLEY;
                break;
            case AUX:
                l.typeVal = OriLine.TYPE_NONE;
                break;
            case CUT:
                l.typeVal = OriLine.TYPE_CUT;
                break;
            case DELETE:
                RemoveElement remover = new RemoveElement();
            	remover.removeLine(l, lines);
                break;
            case FLIP: {
                if (l.typeVal == OriLine.TYPE_RIDGE) {
                    l.typeVal = OriLine.TYPE_VALLEY;
                } else if (l.typeVal == OriLine.TYPE_VALLEY) {
                    l.typeVal = OriLine.TYPE_RIDGE;
                }

            }
        }
    }
}
