package oripa.doc.core;

import java.util.Collection;
import java.util.Iterator;

import javax.vecmath.Vector2d;

import oripa.doc.value.OriLine;

public class CreasePattern implements Collection<OriLine> {

	/**
	 * Wrapper to treat vertices and line at the same time
	 * 
	 * basically default iterator is enough but it cannot
	 * remove corresponding vertices.
	 *
	 * @author Koji
	 *
	 */
	private class CreasePatternIterator implements Iterator<OriLine> {

		private Iterator<OriLine> lineIter;
		private OriLine current;

		public CreasePatternIterator(Iterator<OriLine> iter) {
			lineIter = iter;
		}
		
		@Override
		public boolean hasNext() {
			return lineIter.hasNext();
		}

		@Override
		public OriLine next() {
			current = lineIter.next();
			return current;
		}

		@Override
		public void remove() {
			lineIter.remove();
			vertices.remove(current.p0);
			vertices.remove(current.p1);
		}
		
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -6919017534440930379L;

	private LineManager     lines;
	private VerticesManager vertices;
	
	public CreasePattern(double paperSize) {
		lines    = new LineManager();
		vertices = new VerticesManager(paperSize);
	}
	
	
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
		if (lines.add(e)) {
			vertices.add(e.p0);
			vertices.add(e.p1);
			return true;
		}
		return false;
	}

	@Override
	public boolean remove(Object o) {
		OriLine l = (OriLine) o;

		if (lines.remove(o)) {
			vertices.remove(l.p0);
			vertices.remove(l.p1);
			return true;
		}

		return false;
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
		return lines.toArray(a);
	}

	@Override
	public boolean isEmpty() {

		if (lines.isEmpty()) {
			if (!vertices.isEmpty()) {
				throw new IllegalStateException("no lines but some vertices exist.");
			}
			
			return true;
		}
		return false;
	}

	@Override
	public Iterator<OriLine> iterator() {
		return new CreasePatternIterator(lines.iterator());
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return lines.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends OriLine> c) {
		
		for(OriLine line : c){
			vertices.add(line.p0);
			vertices.add(line.p1);
		}
		
		return lines.addAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		
		boolean changed = false;

		for(OriLine line : (Collection<OriLine>)c){
			changed |= remove(line);
		}
		
		return changed;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		
		for(OriLine line : lines){
			Collection<OriLine> collection = (Collection<OriLine>)c;
			//removes from this collection 
			//all of its elements that are not contained in the specified collection c.
			if(! collection.contains(line)){
				vertices.remove(line.p0);
				vertices.remove(line.p1);
				
			}
		}
		
		
		return lines.retainAll(c);
	}
	
	public Collection<Vector2d> getVerticesAround(Vector2d v){
		return vertices.getAround(v);
	}

	public Collection<Collection<Vector2d>> getVerticesArea(
			double x, double y, double distance){
		
		return vertices.getArea(x, y, distance);
	}

	/**
	 * DO NOT USE THIS.
	 * this is for junit.
	 * @return
	 */
	@Deprecated
	public VerticesManager getVerticesManager(){
		return vertices;
	}
	
}
