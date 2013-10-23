package oripa.paint.creasepattern;

import java.util.Collection;
import java.util.Iterator;

import javax.vecmath.Vector2d;

import oripa.paint.CreasePatternInterface;
import oripa.paint.geometry.NearVerticesGettable;
import oripa.value.OriLine;

/**
 * Crease pattern
 * 
 * Implementation of line-and-vertices structure.
 * Original ORIPA uses line-only structure, which
 * may cause a slow vertex search if the CP is massive.
 * 
 * @author Koji
 *
 */
public class CreasePattern implements CreasePatternInterface {

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
	private double paperSize = 400;
	
	@SuppressWarnings("unused")
	private CreasePattern(){}
	
	public CreasePattern(double paperSize) {
		lines    = new LineManager();
		vertices = new VerticesManager(paperSize);

		this.paperSize = paperSize;
	}
	
	/* (non Javadoc)
	 * @see oripa.paint.creasepattern.CreasePatternInterface#changePaperSize(double)
	 */
	@Override
	public void changePaperSize(double paperSize) {
		this.paperSize = paperSize;
		vertices.changePaperSize(paperSize);
	}

	/* (non Javadoc)
	 * @see oripa.paint.creasepattern.CreasePatternInterface#getPaperSize()
	 */
	@Override
	public double getPaperSize() {
		return paperSize;
	}
	
	/* (non Javadoc)
	 * @see oripa.paint.creasepattern.CreasePatternInterface#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(Object o) {
		// TODO Auto-generated method stub
		return lines.contains(o);
	}

	/* (non Javadoc)
	 * @see oripa.paint.creasepattern.CreasePatternInterface#size()
	 */
	@Override
	public int size() {
		return lines.size();
	}

	/* (non Javadoc)
	 * @see oripa.paint.creasepattern.CreasePatternInterface#add(oripa.value.OriLine)
	 */
	@Override
	public boolean add(OriLine e) {
		if (lines.add(e)) {
			vertices.add(e.p0);
			vertices.add(e.p1);
			return true;
		}
		return false;
	}

	/* (non Javadoc)
	 * @see oripa.paint.creasepattern.CreasePatternInterface#remove(java.lang.Object)
	 */
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

	/* (non Javadoc)
	 * @see oripa.paint.creasepattern.CreasePatternInterface#clear()
	 */
	@Override
	public void clear() {
		lines.clear();
		vertices.clear();
	}


	/* (non Javadoc)
	 * @see oripa.paint.creasepattern.CreasePatternInterface#toArray()
	 */
	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return lines.toArray();
	}

	/* (non Javadoc)
	 * @see oripa.paint.creasepattern.CreasePatternInterface#toArray(T[])
	 */
	@Override
	public <T> T[] toArray(T[] a) {
		return lines.toArray(a);
	}

	/* (non Javadoc)
	 * @see oripa.paint.creasepattern.CreasePatternInterface#isEmpty()
	 */
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

	/* (non Javadoc)
	 * @see oripa.paint.creasepattern.CreasePatternInterface#iterator()
	 */
	@Override
	public Iterator<OriLine> iterator() {
		return new CreasePatternIterator(lines.iterator());
	}

	/* (non Javadoc)
	 * @see oripa.paint.creasepattern.CreasePatternInterface#containsAll(java.util.Collection)
	 */
	@Override
	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return lines.containsAll(c);
	}

	/* (non Javadoc)
	 * @see oripa.paint.creasepattern.CreasePatternInterface#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(Collection<? extends OriLine> c) {
		
		for(OriLine line : c){
			vertices.add(line.p0);
			vertices.add(line.p1);
		}
		
		return lines.addAll(c);
	}

	/* (non Javadoc)
	 * @see oripa.paint.creasepattern.CreasePatternInterface#removeAll(java.util.Collection)
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		
		boolean changed = false;

		for(OriLine line : (Collection<OriLine>)c){
			changed |= remove(line);
		}
		
		return changed;
	}

	/* (non Javadoc)
	 * @see oripa.paint.creasepattern.CreasePatternInterface#retainAll(java.util.Collection)
	 */
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
	
	/* (non Javadoc)
	 * @see oripa.paint.creasepattern.CreasePatternInterface#getVerticesAround(javax.vecmath.Vector2d)
	 */
	@Override
	public Collection<Vector2d> getVerticesAround(Vector2d v){
		return vertices.getVerticesAround(v);
	}

	/* (non Javadoc)
	 * @see oripa.paint.creasepattern.CreasePatternInterface#getVerticesInArea(double, double, double)
	 */
	@Override
	public Collection<Collection<Vector2d>> getVerticesInArea(
			double x, double y, double distance){
		
		return vertices.getVerticesInArea(x, y, distance);
	}

	@Deprecated
	public NearVerticesGettable getVerticesManager(){
		return vertices;
	}
	
}
