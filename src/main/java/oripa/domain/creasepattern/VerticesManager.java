package oripa.domain.creasepattern;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import oripa.geom.RectangleDomain;
import oripa.util.collection.CollectionUtil;
import oripa.vecmath.Vector2d;

/**
 * For a fast access to vertex
 *
 * @author koji
 *
 */
class VerticesManager implements NearVerticesGettable {

	/*
	 * divides paper equally in order to localize access to vertices
	 */
	public static final int divNum = 32;

	private final double interval;

	private final RectangleDomain domain;

	/**
	 * the index of divided paper area. A given point is converted to the index
	 * it should belongs to.
	 *
	 * @author Koji
	 *
	 */
	private class AreaPosition {
		public int x, y;

		/**
		 * doubles point to index
		 */
		public AreaPosition(final Vector2d v) {
			x = toDiv(v.getX(), domainLeft());
			y = toDiv(v.getY(), domainTop());
		}
	}

	/**
	 * Computes a index on one axis.
	 *
	 * @param p
	 * @return
	 */
	private int toDiv(final double p, final double p0) {
		int div = (int) ((p - p0) / interval);

		if (div < 0) {
			return 0;
		}

		if (div >= divNum) {
			return divNum - 1;
		}

		return div;
	}

	/**
	 * [div_x][div_y] is a set of vertices in the divided area.
	 */
	@SuppressWarnings("unchecked")
	private final Set<Vector2d>[][] vertices = new Set[divNum][divNum];

	/**
	 * count existence of same values.
	 */
	private final Map<Vector2d, AtomicInteger> counts = new ConcurrentHashMap<>();

	/**
	 * Constructor to initialize fields.
	 *
	 * @param domain
	 *            rectangle domain that will include vertices to be managed.
	 *            Degradation of performance will happen if many vertices are
	 *            out of the domain.
	 */
	public VerticesManager(final RectangleDomain domain) {
		this.domain = domain;

		interval = getDomainSize() / divNum;

		// allocate memory for each area
		for (int x = 0; x < divNum; x++) {
			for (int y = 0; y < divNum; y++) {
				vertices[x][y] = CollectionUtil.newConcurrentHashSet();
			}
		}

	}

	public RectangleDomain getDomain() {
		return domain;
	}

	public double getDomainSize() {
		return domain.maxWidthHeight();
	}

	private double domainLeft() {
		return domain.getLeft();
	}

	private double domainTop() {
		return domain.getTop();
	}

	double getInterval() {
		return interval;
	}

	/**
	 * remove all vertices.
	 */
	public void clear() {
		for (int x = 0; x < divNum; x++) {
			for (int y = 0; y < divNum; y++) {
				vertices[x][y].clear();
			}
		}
		counts.clear();
	}

	/**
	 * return vertices in the specified area.
	 *
	 * @param ap
	 *            index of area.
	 * @return vertices in the specified area.
	 */
	private Set<Vector2d> getVertices(final AreaPosition ap) {
		return vertices[ap.x][ap.y];
	}

	/**
	 * add given vertex to appropriate area.
	 *
	 * @param v
	 *            vertex to be managed by this object.
	 */
	public void add(final Vector2d v) {

		Set<Vector2d> vertices = getVertices(new AreaPosition(v));

		// v is a new value
		if (vertices.add(v)) {
			counts.put(v, new AtomicInteger(1));
			return;
		}

		// count duplication.
		counts.get(v).incrementAndGet();
	}

	@Override
	public Collection<Vector2d> getVerticesAround(final Vector2d v) {
		AreaPosition ap = new AreaPosition(v);
		return getVertices(ap);
	}

	/**
	 * remove the given vertex from this object.
	 *
	 * @param v
	 */
	public void remove(final Vector2d v) {
		AreaPosition ap = new AreaPosition(v);
		var count = counts.get(v);

		// should never happen.
		if (count.get() <= 0) {
			throw new IllegalStateException("Nothing to remove");
		}

		// No longer same vertices exists.
		if (count.get() == 1) {
			getVertices(ap).remove(v);
			counts.remove(v);
			return;
		}

		// decrement existence.
		count.decrementAndGet();
	}

	@Override
	public Collection<Collection<Vector2d>> getVerticesInArea(
			final double x, final double y, final double distance) {

		Collection<Collection<Vector2d>> result = new LinkedList<>();

		int leftDiv = toDiv(x - distance, domainLeft());
		int rightDiv = toDiv(x + distance, domainLeft());
		int topDiv = toDiv(y - distance, domainTop());
		int bottomDiv = toDiv(y + distance, domainTop());

		for (int xDiv = leftDiv; xDiv <= rightDiv; xDiv++) {
			for (int yDiv = topDiv; yDiv <= bottomDiv; yDiv++) {
				result.add(vertices[xDiv][yDiv]);
			}
		}

		return result;
	}

	public boolean isEmpty() {
		for (Set<Vector2d>[] vertexSets : vertices) {
			for (Set<Vector2d> vertexSet : vertexSets) {
				if (!vertexSet.isEmpty()) {
					return false;
				}
			}
		}

		return true;
	}
}
