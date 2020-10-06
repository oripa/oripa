package oripa.domain.cptool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.vecmath.Vector2d;

import oripa.geom.GeomUtil;
import oripa.value.OriLine;

/**
 * This class defines how to remove line/vertex from crease pattern.
 *
 * @author Koji
 *
 */
public class ElementRemover {
	/**
	 * remove line from crease pattern
	 *
	 * @param l
	 * @param creasePattern
	 */
	public void removeLine(
			final OriLine l, final Collection<OriLine> creasePattern) {

		creasePattern.remove(l);
		// merge the lines if possible, to prevent unnecessary vertexes
		merge2LinesAt(l.p0, creasePattern);
		merge2LinesAt(l.p1, creasePattern);
	}

	/**
	 * remove vertex from crease pattern
	 *
	 * @param v
	 * @param creasePattern
	 */
	public void removeVertex(
			final Vector2d v, final Collection<OriLine> creasePattern) {

		merge2LinesAt(v, creasePattern);
	}

	private void merge2LinesAt(
			final Vector2d p, final Collection<OriLine> creasePattern) {

		ArrayList<OriLine> sharedLines = new ArrayList<OriLine>();

		creasePattern.stream()
				.filter(line -> GeomUtil.distance(line.p0, p) < 0.001
						|| GeomUtil.distance(line.p1, p) < 0.001)
				.forEach(line -> sharedLines.add(line));

		if (sharedLines.size() != 2) {
			return;
		}

		OriLine l0 = sharedLines.get(0);
		OriLine l1 = sharedLines.get(1);

		if (l0.getType() != l1.getType()) {
			return;
		}

		// Check if the lines have the same angle
		Vector2d dir0 = new Vector2d(l0.p1.x - l0.p0.x, l0.p1.y - l0.p0.y);
		Vector2d dir1 = new Vector2d(l1.p1.x - l1.p0.x, l1.p1.y - l1.p0.y);

		dir0.normalize();
		dir1.normalize();

		if (!GeomUtil.isParallel(dir0, dir1)) {
			return;
		}

		// Merge possibility found
		Vector2d p0 = new Vector2d();
		Vector2d p1 = new Vector2d();

		if (GeomUtil.distance(l0.p0, p) < 0.001) {
			p0.set(l0.p1);
		} else {
			p0.set(l0.p0);
		}
		if (GeomUtil.distance(l1.p0, p) < 0.001) {
			p1.set(l1.p1);
		} else {
			p1.set(l1.p0);
		}

		creasePattern.remove(l0);
		creasePattern.remove(l1);
		OriLine li = new OriLine(p0, p1, l0.getType());
		creasePattern.add(li);
	}

	/**
	 * remove lines which are marked "selected" from given collection.
	 *
	 * @param creasePattern
	 *            collection of lines
	 */
	public void removeSelectedLines(
			final Collection<OriLine> creasePattern) {

		List<OriLine> selectedLines = creasePattern.stream()
				.filter(line -> line.selected)
				.collect(Collectors.toList());

		selectedLines.forEach(line -> removeLine(line, creasePattern));
	}

}
