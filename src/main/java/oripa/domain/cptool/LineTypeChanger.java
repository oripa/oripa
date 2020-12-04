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
		if (from == TypeForChange.MOUNTAIN && l.getType() != OriLine.Type.MOUNTAIN) {
			return;
		}
		if (from == TypeForChange.VALLEY && l.getType() != OriLine.Type.VALLEY) {
			return;
		}

		switch (to) {
		case MOUNTAIN:
			l.setType(OriLine.Type.MOUNTAIN);
			break;
		case VALLEY:
			l.setType(OriLine.Type.VALLEY);
			break;
		case AUX:
			l.setType(OriLine.Type.AUX);
			break;
		case CUT:
			l.setType(OriLine.Type.CUT);
			break;
		case DELETE:
			ElementRemover remover = new ElementRemover();
			remover.removeLine(l, lines);
			break;
		case FLIP:
			if (l.getType() == OriLine.Type.MOUNTAIN) {
				l.setType(OriLine.Type.VALLEY);
			} else if (l.getType() == OriLine.Type.VALLEY) {
				l.setType(OriLine.Type.MOUNTAIN);
			}
			break;
		default:
			break;
		}
	}
}
