package oripa.domain.creasepattern;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import oripa.util.collection.CollectionUtil;
import oripa.value.OriLine;

/**
 * Manager of all lines.
 *
 * @author Koji
 *
 */
class LineManager implements Set<OriLine> {

	// The order of lines affects the position of drawn estimated model.
	// HashSet is fast to access but does not guarantee that the order is always
	// same.

	private final Set<OriLine> lines = CollectionUtil.newConcurrentHashSet();

	@Override
	public boolean contains(final Object o) {
		return lines.contains(o);
	}

	@Override
	public int size() {
		return lines.size();
	}

	@Override
	public boolean add(final OriLine e) {
		return lines.add(e);
	}

	@Override
	public boolean remove(final Object o) {
		return lines.remove(o);
	}

	@Override
	public void clear() {
		lines.clear();
	}

	@Override
	public Object[] toArray() {
		return lines.toArray();
	}

	@Override
	public <T> T[] toArray(final T[] a) {
		return lines.toArray(a);
	}

	@Override
	public boolean isEmpty() {
		return lines.isEmpty();
	}

	@Override
	public Iterator<OriLine> iterator() {
		return lines.iterator();
	}

	@Override
	public boolean containsAll(final Collection<?> c) {
		return lines.containsAll(c);
	}

	@Override
	public boolean addAll(final Collection<? extends OriLine> c) {

		return lines.addAll(c);
	}

	@Override
	public boolean removeAll(final Collection<?> c) {

		return lines.removeAll(c);
	}

	@Override
	public boolean retainAll(final Collection<?> c) {

		return lines.retainAll(c);
	}

	@Override
	public String toString() {
		return lines.toString();
	}
}
