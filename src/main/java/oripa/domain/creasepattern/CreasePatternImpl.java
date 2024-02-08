package oripa.domain.creasepattern;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import oripa.geom.RectangleDomain;
import oripa.value.OriLine;
import oripa.vecmath.Vector2d;

/**
 * Crease pattern
 *
 * Implementation of line-and-vertices structure. Original ORIPA uses line-only
 * structure, which may cause a slow vertex search if the CP is massive.
 *
 * @author Koji
 *
 */
class CreasePatternImpl implements CreasePattern {

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
			vertices.remove(current.getP0());
			vertices.remove(current.getP1());
		}

	}

	/**
	 *
	 */
	@SuppressWarnings("unused")
	private static final long serialVersionUID = -6919017534440930379L;

	private final LineManager lines;
	private VerticesManager vertices;

	/**
	 * @param paperDomain
	 *            rectangle domain of paper.
	 */
	public CreasePatternImpl(final RectangleDomain paperDomain) {
		lines = new LineManager();
		vertices = new VerticesManager(paperDomain);
	}

	@Override
	public double getPaperSize() {
		return vertices.getDomainSize();
	}

	@Override
	public RectangleDomain getPaperDomain() {
		return vertices.getDomain();
	}

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
		if (lines.add(e)) {
			vertices.add(e.getP0());
			vertices.add(e.getP1());
			return true;
		}
		return false;
	}

	@Override
	public boolean remove(final Object o) {
		OriLine l = (OriLine) o;

		if (lines.remove(o)) {
			vertices.remove(l.getP0());
			vertices.remove(l.getP1());
			return true;
		}

		return false;
	}

	@Override
	public void clear() {
		lines.clear();
		vertices.clear();
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
	public boolean containsAll(final Collection<?> c) {
		return lines.containsAll(c);
	}

	@Override
	public boolean addAll(final Collection<? extends OriLine> c) {

		boolean added = false;

		for (var line : c) {
			added |= add(line);
		}

		return added;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean removeAll(final Collection<?> c) {

		boolean changed = false;

		for (OriLine line : (Collection<OriLine>) c) {
			changed |= remove(line);
		}

		return changed;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean retainAll(final Collection<?> c) {

		for (OriLine line : lines) {
			Collection<OriLine> collection = (Collection<OriLine>) c;
			// removes from this collection
			// all of its elements that are not contained in the specified
			// collection c.
			if (!collection.contains(line)) {
				vertices.remove(line.getP0());
				vertices.remove(line.getP1());
			}
		}

		return lines.retainAll(c);
	}

	@Override
	public Collection<Vector2d> getVerticesAround(final Vector2d v) {
		return vertices.getVerticesAround(v);
	}

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

//		lines.addAll(this);

		var d = new Vector2d(dx, dy);
		this.forEach(line -> {
			lines.add(new OriLine(line.getP0().add(d), line.getP1().add(d), line.getType()));
		});

		// rebuild vertices info
		this.clear();
		this.addAll(lines);
	}

	@Override
	public void replaceWith(final Collection<OriLine> lines) {
		clear();
		addAll(lines);
	}

	@Override
	public void refresh(final double pointEps) {
		var currentDomain = RectangleDomain.createFromSegments(this);

		if (!getPaperDomain().equals(currentDomain, pointEps) && !currentDomain.isVoid()) {
			var lines = new ArrayList<OriLine>(this);

			clear();

			vertices = new VerticesManager(currentDomain);
			addAll(lines);
		}
	}

	@Override
	public boolean cleanDuplicatedLines(final double pointEps) {
		ArrayList<OriLine> tmpLines = new ArrayList<OriLine>(size());
		for (OriLine l : this) {
			if (tmpLines.stream()
					.noneMatch(line -> line.equals(l, pointEps))) {
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

	@Override
	public String toString() {
		return lines.toString();
	}
}
