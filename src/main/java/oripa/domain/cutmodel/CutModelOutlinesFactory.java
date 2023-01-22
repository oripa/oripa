package oripa.domain.cutmodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.vecmath.Vector2d;

import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.geom.GeomUtil;
import oripa.value.OriLine;

public class CutModelOutlinesFactory {

	/**
	 * creates outline of cut origami model
	 *
	 * @param scissorsLine
	 * @param origamiModel
	 * @return
	 */
	public Collection<OriLine> createOutlines(
			final OriLine scissorsLine, final OrigamiModel origamiModel, final double pointEps) {

		Collection<OriLine> cutLines = new ArrayList<>();

		List<OriFace> faces = origamiModel.getFaces();

		for (OriFace face : faces) {
			List<Vector2d> vv = findOutlineEdgeTerminals(scissorsLine, face, pointEps);

			if (vv.size() >= 2) {
				cutLines.add(new OriLine(vv.get(0), vv.get(1), OriLine.Type.CUT_MODEL));
			}
		}

		return cutLines;
	}

	private List<Vector2d> findOutlineEdgeTerminals(final OriLine cutLine, final OriFace face, final double pointEps) {
		// line should cross 2 edges.
		List<Vector2d> crossPoints = new ArrayList<>(2);

		face.halfedgeStream().forEach(he -> {
			var position = he.getPositionForDisplay();
			var nextPosition = he.getNext().getPositionForDisplay();
			OriLine l = new OriLine(position.x, position.y,
					nextPosition.x, nextPosition.y, OriLine.Type.AUX);

			var parametersOpt = GeomUtil.solveSegmentsCrossPointVectorEquation(
					cutLine.getP0(), cutLine.getP1(), l.getP0(), l.getP1());

			parametersOpt.ifPresent(parameters -> {
				// use the parameter for a face edge.
				var param = parameters.get(1);
				Vector2d crossV = new Vector2d();
				var positionBefore = he.getPositionBeforeFolding();
				var nextPositionBefore = he.getNext().getPositionBeforeFolding();
				crossV.x = (1.0 - param) * positionBefore.getX() + param * nextPositionBefore.getX();
				crossV.y = (1.0 - param) * positionBefore.getY() + param * nextPositionBefore.getY();

				if (crossPoints.stream()
						.noneMatch(cp -> GeomUtil.areEqual(cp, crossV, pointEps))) {
					crossPoints.add(crossV);
				}
			});
		});

		return crossPoints;
	}

}
