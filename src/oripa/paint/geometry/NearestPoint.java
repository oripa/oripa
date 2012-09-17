package oripa.paint.geometry;

import javax.vecmath.Vector2d;

public class NearestPoint {
	public Vector2d point = new Vector2d();
	public double distance = Double.MAX_VALUE;

	public NearestPoint() {
	}
	
	public NearestPoint(NearestPoint p) {
		if(p != null){
			point.set(p.point);
			distance = p.distance;
		}
	}
}
