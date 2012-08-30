package oripa.paint.outline;

import java.util.Collection;
import java.util.Iterator;

import javax.vecmath.Vector2d;

import oripa.geom.GeomUtil;
import oripa.paint.PairLoop;


public class IsOutsideOfTempOutlineLoop implements PairLoop.Block<Vector2d> {

	private Vector2d target;
    boolean CCWFlg;
	
	public boolean execute(
			Collection<Vector2d> outLineVertice, Vector2d v) {

		target = v;
		
		Iterator<Vector2d> iterator = outLineVertice.iterator();
		Vector2d p0 = iterator.next();
		Vector2d p1 = iterator.next();
		
		CCWFlg = GeomUtil.CCWcheck(p0, p1, target);
		
    	return PairLoop.iterateFrom(iterator, outLineVertice, this) != null;

	}
	
	@Override
	public boolean yield(Vector2d p0, Vector2d p1) {
	       
	       if (CCWFlg != GeomUtil.CCWcheck(p0, p1, target)) {
            return false;
        }

        return true;
	}

}