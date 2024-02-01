package oripa.domain.cptool;

import java.util.Collection;

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
	 * removes the given line and adds to {@code lines} a copied line with type
	 * change.
	 *
	 * @param l
	 * @param type
	 * @param lines
	 */
	private void setType(final OriLine l, final OriLine.Type type, final Collection<OriLine> lines) {

		lines.remove(l);

		lines.add(new OriLine(l.getP0(), l.getP1(), type));
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
