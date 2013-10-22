package oripa.sheetcut;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.vecmath.Vector2d;

import oripa.fold.OriFace;
import oripa.fold.OriHalfedge;
import oripa.fold.OrigamiModel;
import oripa.geom.GeomUtil;
import oripa.paint.core.PaintConfig;
import oripa.value.OriLine;

public class SheetCutOutlineFactory {

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
				cutLines.add(new OriLine(vv.get(0), vv.get(1), PaintConfig.inputLineType));
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
			boolean res = GeomUtil.getCrossPointParam(cutLine.p0, cutLine.p1, l.p0, l.p1, params);
			if (res == true && params[0] > -0.001 && params[1] > -0.001 && params[0] < 1.001 && params[1] < 1.001) {
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
}
