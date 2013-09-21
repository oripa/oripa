package oripa.doc.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import oripa.doc.value.OriLine;

/**
 * Manager of all lines.
 * 
 * @author Koji
 *
 */
public class LineManager implements Collection<OriLine> {
	
	private Set<OriLine> lines = new HashSet<>();
	
	
	@Override
	public boolean contains(Object o) {
		// TODO Auto-generated method stub
		return lines.contains(o);
	}

	@Override
	public int size() {
		return lines.size();
	}

	@Override
	public boolean add(OriLine e) {
		return lines.add(e);
	}

	@Override
	public boolean remove(Object o) {
		OriLine l = (OriLine) o;

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
	public <T> T[] toArray(T[] a) {
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
	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return lines.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends OriLine> c) {
				
		return lines.addAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		
		return lines.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		
		return lines.retainAll(c);
	}
}
