package oripa.doc.command;

import java.util.ArrayList;
import java.util.Collection;

import javax.vecmath.Vector2d;

import oripa.geom.GeomUtil;
import oripa.geom.OriLine;

public class RemoveElement {
	public void removeLine(OriLine l, Collection<OriLine> creasePattern) {
		creasePattern.remove(l);
		// merge the lines if possible, to prevent unnecessary vertexes
		merge2LinesAt(l.p0, creasePattern);
		merge2LinesAt(l.p1, creasePattern);
	}

	public void removeVertex(Vector2d v, Collection<OriLine> creasePattern) {
		merge2LinesAt(v, creasePattern);
	}
	private void merge2LinesAt(Vector2d p, Collection<OriLine> creasePattern) {
		ArrayList<OriLine> sharedLines = new ArrayList<OriLine>();
		for (OriLine line : creasePattern) {
			if (GeomUtil.Distance(line.p0, p) < 0.001 || GeomUtil.Distance(line.p1, p) < 0.001) {
				sharedLines.add(line);
			}
		}

		if (sharedLines.size() != 2) {
			return;
		}

		OriLine l0 = sharedLines.get(0);
		OriLine l1 = sharedLines.get(1);

		if (l0.typeVal != l1.typeVal) {
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

		if (GeomUtil.Distance(l0.p0, p) < 0.001) {
			p0.set(l0.p1);
		} else {
			p0.set(l0.p0);
		}
		if (GeomUtil.Distance(l1.p0, p) < 0.001) {
			p1.set(l1.p1);
		} else {
			p1.set(l1.p0);
		}

		creasePattern.remove(l0);
		creasePattern.remove(l1);
		OriLine li = new OriLine(p0, p1, l0.typeVal);
		creasePattern.add(li);
	}

}
