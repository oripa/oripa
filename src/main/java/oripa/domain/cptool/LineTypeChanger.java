package oripa.domain.cptool;

import java.util.Collection;
import java.util.stream.Collectors;

import oripa.value.OriLine;

public class LineTypeChanger {

	private boolean isTarget(final OriLine l, final TypeForChange from) {
		if (from == TypeForChange.MOUNTAIN && l.getType() != OriLine.Type.MOUNTAIN) {
			return false;
		}
		if (from == TypeForChange.VALLEY && l.getType() != OriLine.Type.VALLEY) {
			return false;
		}
		if (from == TypeForChange.AUX && l.getType() != OriLine.Type.AUX) {
			return false;
		}
		if (from == TypeForChange.CUT && l.getType() != OriLine.Type.CUT) {
			return false;
		}

		return true;
	}

	/**
	 *
	 * @param l
	 * @param lines
	 * @param from
	 * @param to
	 */
	public void alterLineType(final OriLine l, final Collection<OriLine> lines,
			final TypeForChange from, final TypeForChange to) {
		if (!isTarget(l, from)) {
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

	public void alterLineTypes(final Collection<OriLine> toBeChanged,
			final Collection<OriLine> lines,
			final TypeForChange from, final TypeForChange to) {
		var filtered = toBeChanged.stream()
				.filter(line -> isTarget(line, from))
				.collect(Collectors.toList());

		if (to == TypeForChange.DELETE) {
			var remover = new ElementRemover();
			remover.removeLines(filtered, lines);
			return;
		}

		filtered.forEach(line -> alterLineType(line, lines, from, to));
	}
}
