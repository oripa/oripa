package oripa.domain.cptool;

import java.util.Collection;
import java.util.Set;

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
			final TypeForChange from, final TypeForChange to, final double pointEps) {
		if (!isTarget(l, from)) {
			return;
		}

		if (to.getOriLineType() == null) {
			switch (to) {
			case DELETE:
				remover.removeLine(l, lines, pointEps);
				break;
			case FLIP:
				if (l.getType() == OriLine.Type.MOUNTAIN) {
					setType(l, OriLine.Type.VALLEY, lines);
				} else if (l.getType() == OriLine.Type.VALLEY) {
					setType(l, OriLine.Type.MOUNTAIN, lines);
				}
				break;
			default:
				break;
			}
			return;
		}

		setType(l, to.getOriLineType(), lines);
	}

	/**
	 * We need to remove and add element to be updated if {@code lines} is a
	 * hash set.
	 *
	 * @param l
	 * @param type
	 * @param lines
	 */
	private void setType(final OriLine l, final OriLine.Type type, final Collection<OriLine> lines) {
		if (lines instanceof Set) {
			lines.remove(l);
		}
		l.setType(type);
		if (lines instanceof Set) {
			lines.add(l);
		}
	}

	public void alterLineTypes(final Collection<OriLine> toBeChanged,
			final Collection<OriLine> lines,
			final TypeForChange from, final TypeForChange to,
			final double pointEps) {
		var filtered = toBeChanged.stream()
				.filter(line -> isTarget(line, from))
				.toList();

		if (to == TypeForChange.DELETE) {
			remover.removeLines(filtered, lines, pointEps);
			return;
		}

		filtered.forEach(line -> alterLineType(line, lines, from, to, pointEps));
	}
}
