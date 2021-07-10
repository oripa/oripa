package oripa.domain.creasepattern;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.vecmath.Vector2d;

import oripa.geom.GeomUtil;
import oripa.geom.RectangleDomain;
import oripa.value.OriLine;

/**
 * Crease pattern
 *
 * Implementation of line-and-vertices structure. Original ORIPA uses line-only
 * structure, which may cause a slow vertex search if the CP is massive.
 *
 * @author Koji
 *
 */
class CreasePatternImpl implements CreasePatternInterface {

	/**
	 * Wrapper to treat vertices and line at the same time
	 *
	 * basically default iterator is enough but it cannot remove corresponding
	 * vertices.
	 *
	 * @author Koji
	 *
	 */
	private class CreasePatternIterator implements Iterator<OriLine> {

		private final Iterator<OriLine> lineIter;
		private OriLine current;

		public CreasePatternIterator(final Iterator<OriLine> iter) {
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
	@SuppressWarnings("unused")
	private static final long serialVersionUID = -6919017534440930379L;

	private LineManager lines;
	private VerticesManager vertices;
	private final RectangleDomain paperDomain;
	private final double paperSize;

	@SuppressWarnings("unused")
	private CreasePatternImpl() {
		paperSize = 0;
		paperDomain = null;
	}

	/**
	 * @param paperDomain
	 *            rectangle domain of paper.
	 */
	public CreasePatternImpl(final RectangleDomain paperDomain) {
		this.paperDomain = paperDomain;
		paperSize = paperDomain.maxWidthHeight();

		lines = new LineManager();
		vertices = new VerticesManager(
				paperSize, paperDomain.getLeft(), paperDomain.getTop());
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.domain.creasepattern.CreasePatternInterface#getPaperSize()
	 */
	@Override
	public double getPaperSize() {
		return paperSize;
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.domain.creasepattern.CreasePatternInterface#getPaperDomain()
	 */
	@Override
	public RectangleDomain getPaperDomain() {
		return paperDomain;
	}

	/*
	 * (non Javadoc)
	 *
	 * @see
	 * oripa.domain.creasepattern.CreasePatternInterface#contains(java.lang.
	 * Object)
	 */
	@Override
	public boolean contains(final Object o) {
		return lines.contains(o);
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.domain.creasepattern.CreasePatternInterface#size()
	 */
	@Override
	public int size() {
		return lines.size();
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.domain.creasepattern.CreasePatternInterface#add(oripa.value.
	 * OriLine)
	 */
	@Override
	public boolean add(final OriLine e) {
		if (lines.add(e)) {
			vertices.add(e.p0);
			vertices.add(e.p1);
			return true;
		}
		return false;
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.domain.creasepattern.CreasePatternInterface#remove(java.lang.
	 * Object)
	 */
	@Override
	public boolean remove(final Object o) {
		OriLine l = (OriLine) o;

		if (lines.remove(o)) {
			vertices.remove(l.p0);
			vertices.remove(l.p1);
			return true;
		}

		return false;
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.domain.creasepattern.CreasePatternInterface#clear()
	 */
	@Override
	public void clear() {
		lines.clear();
		vertices.clear();
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.domain.creasepattern.CreasePatternInterface#toArray()
	 */
	@Override
	public Object[] toArray() {
		return lines.toArray();
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.domain.creasepattern.CreasePatternInterface#toArray(T[])
	 */
	@Override
	public <T> T[] toArray(final T[] a) {
		return lines.toArray(a);
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.domain.creasepattern.CreasePatternInterface#isEmpty()
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

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.domain.creasepattern.CreasePatternInterface#iterator()
	 */
	@Override
	public Iterator<OriLine> iterator() {
		return new CreasePatternIterator(lines.iterator());
	}

	/*
	 * (non Javadoc)
	 *
	 * @see
	 * oripa.domain.creasepattern.CreasePatternInterface#containsAll(java.util.
	 * Collection)
	 */
	@Override
	public boolean containsAll(final Collection<?> c) {
		return lines.containsAll(c);
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.domain.creasepattern.CreasePatternInterface#addAll(java.util.
	 * Collection)
	 */
	@Override
	public boolean addAll(final Collection<? extends OriLine> c) {

		boolean added = false;

		for (var line : c) {
			added |= add(line);
		}

		return added;
	}

	/*
	 * (non Javadoc)
	 *
	 * @see
	 * oripa.domain.creasepattern.CreasePatternInterface#removeAll(java.util.
	 * Collection)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean removeAll(final Collection<?> c) {

		boolean changed = false;

		for (OriLine line : (Collection<OriLine>) c) {
			changed |= remove(line);
		}

		return changed;
	}

	/*
	 * (non Javadoc)
	 *
	 * @see
	 * oripa.domain.creasepattern.CreasePatternInterface#retainAll(java.util.
	 * Collection)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean retainAll(final Collection<?> c) {

		for (OriLine line : lines) {
			Collection<OriLine> collection = (Collection<OriLine>) c;
			// removes from this collection
			// all of its elements that are not contained in the specified
			// collection c.
			if (!collection.contains(line)) {
				vertices.remove(line.p0);
				vertices.remove(line.p1);
			}
		}

		return lines.retainAll(c);
	}

	/*
	 * (non Javadoc)
	 *
	 * @see
	 * oripa.domain.creasepattern.CreasePatternInterface#getVerticesAround(javax
	 * .vecmath.Vector2d)
	 */
	@Override
	public Collection<Vector2d> getVerticesAround(final Vector2d v) {
		return vertices.getVerticesAround(v);
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.domain.creasepattern.CreasePatternInterface#getVerticesInArea(
	 * double, double, double)
	 */
	@Override
	public Collection<Collection<Vector2d>> getVerticesInArea(
			final double x, final double y, final double distance) {

		return vertices.getVerticesInArea(x, y, distance);
	}

	@Deprecated
	public NearVerticesGettable getVerticesManager() {
		return vertices;
	}

	@Override
	public void move(final double dx, final double dy) {
		var lines = new ArrayList<OriLine>();

		lines.addAll(this);

		lines.forEach(line -> {
			line.p0.x += dx;
			line.p0.y += dy;
			line.p1.x += dx;
			line.p1.y += dy;
		});

		// rebuild vertices info
		this.clear();
		this.addAll(lines);
	}

	/*
	 * (non Javadoc)
	 *
	 * @see
	 * oripa.domain.creasepattern.CreasePatternInterface#removeDuplicatedLines()
	 */
	@Override
	public boolean cleanDuplicatedLines() {
		ArrayList<OriLine> tmpLines = new ArrayList<OriLine>(size());
		for (OriLine l : this) {
			if (tmpLines.stream()
					.noneMatch(line -> GeomUtil.isSameLineSegment(line, l))) {
				tmpLines.add(l);
			}
		}

		if (size() == tmpLines.size()) {
			return false;
		}

		clear();
		addAll(tmpLines);

		return true;
	}
}
