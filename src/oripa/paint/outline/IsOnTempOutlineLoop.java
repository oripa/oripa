package oripa.paint.outline;

import java.util.Collection;

import javax.vecmath.Vector2d;

import oripa.ORIPA;
import oripa.geom.GeomUtil;
import oripa.geom.Line;
import oripa.paint.PairLoop;

public class IsOnTempOutlineLoop implements PairLoop.Block<Vector2d> {

	private Vector2d target;
	
	public Vector2d execute(
			Collection<Vector2d> outlineVertices, Vector2d v) {

		target = v;
    	return PairLoop.iterateAll(outlineVertices, this);

	}
	
	@Override
	public boolean yield(Vector2d p0, Vector2d p1) {
        if (GeomUtil.DistancePointToLine(target, new Line(p0, new Vector2d(p1.x - p0.x, p1.y - p0.y))) < ORIPA.doc.size * 0.001) {
            return false;
        }

        return true;
	}

}
