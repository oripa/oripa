package oripa.doc;

import oripa.geom.OriLine;

//public 
class LineTypeChanger {

	public void alterLineType(OriLine l, Doc doc,
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
                doc.removeLine(l);
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
