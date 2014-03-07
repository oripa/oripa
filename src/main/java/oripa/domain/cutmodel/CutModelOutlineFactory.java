package oripa.domain.cutmodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.vecmath.Vector2d;

import oripa.controller.paint.core.PaintConfig;
import oripa.domain.fold.OriFace;
import oripa.domain.fold.OriHalfedge;
import oripa.domain.fold.OrigamiModel;
import oripa.geom.GeomUtil;
import oripa.value.OriLine;

public class CutModelOutlineFactory {

	/**
	 * creates outline of cut origami model
	 * @param cutLine
	 * @param origamiModel
	 * @return
	 */
	public Collection<OriLine> createLines(
			OriLine cutLine, OrigamiModel origamiModel) {

		Collection<OriLine> cutLines = new ArrayList<>();

		List<OriFace> sortedFaces = origamiModel.getSortedFaces();

		for (OriFace face : sortedFaces) {
			List<Vector2d> vv = findOutlineEdgeTerminals(cutLine, face);

			if (vv.size() >= 2) {
				cutLines.add(new OriLine(vv.get(0), vv.get(1), OriLine.TYPE_CUT_MODEL));
			}
		}

		return cutLines;
	}

	
	private List<Vector2d> findOutlineEdgeTerminals(OriLine cutLine, OriFace face) {
		List<Vector2d> vv = new ArrayList<>(2);
		
		for (OriHalfedge he : face.halfedges) {
			OriLine l = new OriLine(he.positionForDisplay.x, he.positionForDisplay.y,
					he.next.positionForDisplay.x, he.next.positionForDisplay.y, PaintConfig.inputLineType);

			double params[] = new double[2];
			boolean res = getCrossPointParam(cutLine.p0, cutLine.p1, l.p0, l.p1, params);
			if (res == true &&
					params[0] > -0.001 && params[1] > -0.001 &&
					params[0] < 1.001 && params[1] < 1.001) {
				double param = params[1];

				Vector2d crossV = new Vector2d();
				crossV.x = (1.0 - param) * he.vertex.preP.x + param * he.next.vertex.preP.x;
				crossV.y = (1.0 - param) * he.vertex.preP.y + param * he.next.vertex.preP.y;

				boolean isNewPoint = true;
				for (Vector2d v2d : vv) {
					if (GeomUtil.Distance(v2d, crossV) < 1) {
						isNewPoint = false;
						break;
					}
				}
				if (isNewPoint) {
					vv.add(crossV);
				}
			}
		}

		return vv;
	}


//  Obtain the parameters for the intersection of the segments p0-p1 and q0-q1
//  The param stores the position of the intersection
//  Returns false if parallel
	private boolean getCrossPointParam(Vector2d p0, Vector2d p1, Vector2d q0, Vector2d q1, double[] param) {

      Vector2d d0 = new Vector2d(p1.x - p0.x, p1.y - p0.y);
      Vector2d d1 = new Vector2d(q1.x - q0.x, q1.y - q0.y);
      Vector2d diff = new Vector2d(q0.x - p0.x, q0.y - p0.y);
      double det = d1.x * d0.y - d1.y * d0.x;

      double epsilon = 1.0e-6;
      if (det * det > epsilon * d0.lengthSquared() * d1.lengthSquared()) {
          // Lines intersect in a single point.  Return both s and t values for
          // use by calling functions.
          double invDet = 1.0 / det;
          
          param[0] = (d1.x * diff.y - d1.y * diff.x) * invDet;
          param[1] = (d0.x * diff.y - d0.y * diff.x) * invDet;
          return true;
      }
      return false;

  }


}
