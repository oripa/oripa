package oripa.domain.paint.util;

import java.util.Iterator;
import java.util.Optional;

public class PairLoop {

	/**
	 * loop of (begin, begin+1), ... , (n-2, n-1), (n-1, 0).
	 *
	 * @param begin
	 * @param elements
	 *            Loop target
	 * @param block
	 *            What to do for the pairs
	 * @return The first element of the last pair, in other word, i of (i, i+1).
	 *         empty if {@code block} never made a break by returning false.
	 */
	public static <Element> Optional<Element> iterateFrom(final Iterator<Element> begin,
			final Iterable<Element> elements, final Block<Element> block) {

		Iterator<Element> iterator = begin;
		Element e1 = iterator.next();

		while (iterator.hasNext()) {
			Element e2 = iterator.next();

			if (block.yield(e1, e2) == false) {
				return Optional.of(e1);
			}

			e1 = e2;
		}

		if (block.yield(e1, elements.iterator().next()) == false) {
			return Optional.of(e1);
		}

		return Optional.empty();

	}

	/**
	 * loop of (0, 1), (1, 2) ... (n-2, n-1), (n-1, 0) for n elements.
	 *
	 * @param elements
	 *            Loop target
	 * @param block
	 *            What to do for the pairs
	 * @return The first element of the last pair, in other word, i of (i, i+1).
	 *         empty if {@code block} never made a break by returning false.
	 */
	public static <Element> Optional<Element> iterateAll(
			final Iterable<Element> elements, final Block<Element> block) {
		return iterateFrom(elements.iterator(), elements, block);
	}

	/**
	 * loop of first {@code count} pairs. This method does not iterate to (n-1,
	 * 0) pair.
	 *
	 * @param elements
	 *            Loop target
	 * @param count
	 *            the times of iteration
	 * @param block
	 *            What to do for the pairs
	 * @return The first element of the last pair, in other word, i of (i, i+1).
	 *         empty if {@code block} never made a break by returning false.
	 */

	public static <Element> Optional<Element> iterateWithCount(
			final Iterable<Element> elements, int count, final Block<Element> block) {

		if (count <= 0) {
			throw new IllegalArgumentException("count should be larger than 0");
		}

		Iterator<Element> iterator = elements.iterator();
		Element e1 = iterator.next();

		while (iterator.hasNext() && count > 0) {
			Element e2 = iterator.next();

			if (block.yield(e1, e2) == false) {
				return Optional.of(e1);
			}

			e1 = e2;
			count--;
		}

		return Optional.empty();

	}

	/**
	 * This interface is to describe what to do with pair-element loop. The
	 * function yield() must return true to go next loop.
	 *
	 * @author OUCHI Koji
	 *
	 * @param <Element>
	 */
	@FunctionalInterface
	public interface Block<Element> {
		/**
		 * this method must return true to go next.
		 *
		 * @param element
		 * @param nextElement
		 * @return true if the loop continues, otherwise false.
		 */
		boolean yield(Element element, Element nextElement);
	}

}
