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
		creasePattern.forEach(line -> line.setSelected(false));
	}

	/**
	 * set {@code true} to selection mark of all lines in given collection
	 * except boundary lines.
	 *
	 * @param creasePattern
	 */
	public void selectAllOriLines(final Collection<OriLine> creasePattern) {
		creasePattern.stream()
				.filter(l -> !l.isBoundary())
				.forEach(l -> l.setSelected(true));
	}

	/**
	 * Counts the lines in the given collection.
	 *
	 * @param creasePattern
	 * @return the number of lines in the {@code creasePattern}.
	 */
	public int countSelectedLines(final Collection<OriLine> creasePattern) {
		return (int) creasePattern.stream().filter(l -> l.isSelected()).count();
	}

}
