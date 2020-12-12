package oripa.domain.cptool;

import java.util.Collection;

import oripa.value.OriLine;

public class LineSelectionModifier {
	/**
	 * reset selection mark of all lines in given collection.
	 *
	 * @param creasePattern
	 */
	public void resetSelectedOriLines(final Collection<OriLine> creasePattern) {
		creasePattern.forEach(line -> line.selected = false);
	}

	/**
	 * set {@code true} to selection mark of all lines in given collection.
	 *
	 * @param creasePattern
	 */
	public void selectAllOriLines(final Collection<OriLine> creasePattern) {
		creasePattern.stream()
				.filter(l -> !l.isBoundary())
				.forEach(l -> l.selected = true);
	}

	public int countSelectedLines(final Collection<OriLine> creasePattern) {
		return (int) creasePattern.stream().filter(l -> l.selected).count();
	}

}
