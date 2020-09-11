package oripa.domain.creasepattern;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import oripa.value.OriLine;

/**
 * Manager of all lines.
 *
 * @author Koji
 *
 */
class LineManager implements Collection<OriLine> {

	// The order of lines affects the position of drawn estimated model.
	// HashSet is fast to access but does not guarantee that the order is always
	// same.

	private final Set<OriLine> lines = Collections.newSetFromMap(
			new ConcurrentHashMap<OriLine, Boolean>());
	// private Set<OriLine> lines = new TreeSet<>();

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
		// TODO Auto-generated method stub
		return lines.toArray();
	}

	@Override
	public <T> T[] toArray(final T[] a) {
		// TODO Auto-generated method stub
		return lines.toArray(a);
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return lines.isEmpty();
	}

	@Override
	public Iterator<OriLine> iterator() {
		// TODO Auto-generated method stub
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
}
