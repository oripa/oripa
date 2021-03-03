package oripa.domain.paint.outline;

import java.util.Collection;
import java.util.Iterator;

import javax.vecmath.Vector2d;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.paint.util.PairLoop;
import oripa.geom.GeomUtil;

public class IsOutsideOfTempOutlineLoop implements PairLoop.Block<Vector2d> {
	private static final Logger logger = LoggerFactory.getLogger(IsOutsideOfTempOutlineLoop.class);
	private static final double EPS = 1e-4;
	private Vector2d target;
	private int CCWFlg;

	public boolean execute(
			final Collection<Vector2d> outLineVertices, final Vector2d v) {

		target = v;

		Iterator<Vector2d> iterator = outLineVertices.iterator();
		Vector2d p0 = iterator.next();
		Vector2d p1 = iterator.next();

		CCWFlg = GeomUtil.CCWcheck(p0, p1, target, EPS);
		logger.trace(p0 + "," + p1 + "," + target + " -> " + CCWFlg);

		// recreate to start the loop from p1
		iterator = outLineVertices.iterator();
		iterator.next();
		return PairLoop.iterateFrom(iterator, outLineVertices, this) != null;

	}

	@Override
	public boolean yield(final Vector2d p0, final Vector2d p1) {
		var ccwValue = GeomUtil.CCWcheck(p0, p1, target, EPS);

		logger.trace(p0 + "," + p1 + "," + target + " -> " + ccwValue);

		return CCWFlg == ccwValue;
	}

}