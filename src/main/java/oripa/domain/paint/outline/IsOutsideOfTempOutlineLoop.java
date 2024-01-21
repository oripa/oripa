package oripa.domain.paint.outline;

import java.util.Collection;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.paint.util.PairLoop;
import oripa.geom.GeomUtil;
import oripa.vecmath.Vector2d;

public class IsOutsideOfTempOutlineLoop {
	private static final Logger logger = LoggerFactory.getLogger(IsOutsideOfTempOutlineLoop.class);

	public boolean execute(
			final Collection<Vector2d> outLineVertices, final Vector2d target) {
		Iterator<Vector2d> iterator = outLineVertices.iterator();
		Vector2d p0 = iterator.next();
		Vector2d p1 = iterator.next();

		int ccwFlag = GeomUtil.CCWcheck(p0, p1, target);
		logger.trace(p0 + "," + p1 + "," + target + " -> " + ccwFlag);

		// recreate to start the loop from p1
		iterator = outLineVertices.iterator();
		iterator.next(); // waste p0
		return PairLoop.iterateFrom(iterator, outLineVertices, (p, q) -> {
			var ccwValue = GeomUtil.CCWcheck(p, q, target);
			logger.trace(p + "," + q + "," + target + " -> " + ccwValue);
			return ccwFlag == ccwValue;
		}) != null;

	}
}