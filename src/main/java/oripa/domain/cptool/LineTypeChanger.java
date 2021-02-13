package oripa.domain.cptool;

import java.util.Collection;
import java.util.stream.Collectors;

import oripa.value.OriLine;

public class LineTypeChanger {
	private final ElementRemover remover = new ElementRemover();

	private boolean isTarget(final OriLine l, final TypeForChange from) {
		return from == TypeForChange.EMPTY || from.getOriLineType() == l.getType();
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

		if (to.getOriLineType() == null) {
			switch (to) {
			case DELETE:
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
			return;
		}

		l.setType(to.getOriLineType());
	}

	public void alterLineTypes(final Collection<OriLine> toBeChanged,
			final Collection<OriLine> lines,
			final TypeForChange from, final TypeForChange to) {
		var filtered = toBeChanged.stream()
				.filter(line -> isTarget(line, from))
				.collect(Collectors.toList());

		if (to == TypeForChange.DELETE) {
			remover.removeLines(filtered, lines);
			return;
		}

		filtered.forEach(line -> alterLineType(line, lines, from, to));
	}
}
