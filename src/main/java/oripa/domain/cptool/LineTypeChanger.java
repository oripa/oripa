package oripa.domain.cptool;

import java.util.Collection;

import oripa.value.OriLine;

public class LineTypeChanger {

	/**
	 *
	 * @param l
	 * @param lines
	 * @param from
	 * @param to
	 */
	public void alterLineType(final OriLine l, final Collection<OriLine> lines,
			final TypeForChange from, final TypeForChange to) {
		if (from == TypeForChange.RIDGE && l.getType() != OriLine.Type.RIDGE) {
			return;
		}
		if (from == TypeForChange.VALLEY && l.getType() != OriLine.Type.VALLEY) {
			return;
		}

		switch (to) {
		case RIDGE:
			l.setType(OriLine.Type.RIDGE);
			break;
		case VALLEY:
			l.setType(OriLine.Type.VALLEY);
			break;
		case AUX:
			l.setType(OriLine.Type.NONE);
			break;
		case CUT:
			l.setType(OriLine.Type.CUT);
			break;
		case DELETE:
			ElementRemover remover = new ElementRemover();
			remover.removeLine(l, lines);
			break;
		case FLIP:
			if (l.getType() == OriLine.Type.RIDGE) {
				l.setType(OriLine.Type.VALLEY);
			} else if (l.getType() == OriLine.Type.VALLEY) {
				l.setType(OriLine.Type.RIDGE);
			}
			break;
		default:
			break;
		}
	}
}
