package oripa.domain.fold;

import java.util.List;
import java.util.stream.Collectors;

import javax.vecmath.Vector2d;

import oripa.geom.RectangleDomain;

public class FolderTool {
	/**
	 *
	 * @param faces
	 *            each half-edge of faces should be set
	 *            {@code positionAfterFolded}.
	 * @return
	 */
	@Deprecated
	public BoundBox calcFoldedBoundingBox(final List<OriFace> faces) {
		return new BoundBox(faces.stream()
				.flatMap(face -> face.halfedges.stream().map(he -> he.positionAfterFolded))
				.collect(Collectors.toList()));
	}

	public RectangleDomain createDomainOfFoldedModel(final List<OriFace> faces) {
		var domain = new RectangleDomain();
		domain.enlarge(faces.stream()
				.flatMap(face -> face.halfedges.stream().map(he -> he.positionAfterFolded))
				.collect(Collectors.toList()));

		return domain;
	}

	public void setFacesOutline(
			final List<OriVertex> vertices, final List<OriFace> faces,
			final boolean isSlide) {

		for (OriFace f : faces) {
			for (OriHalfedge he : f.halfedges) {
				he.positionForDisplay.set(he.vertex.p);
			}
			f.setOutline();
		}

		if (isSlide) {
			int minDepth = Integer.MAX_VALUE;
			int maxDepth = -Integer.MAX_VALUE;
			for (var f : faces) {
				minDepth = Math.min(minDepth, f.z_order);
				maxDepth = Math.max(minDepth, f.z_order);
			}

			double slideUnit = 10.0 / (maxDepth - minDepth);
			for (OriVertex v : vertices) {
				v.tmpFlg = false;
				v.tmpVec.set(v.p);
			}

			for (OriFace f : faces) {
				Vector2d faceCenter = f.getCentroidAfterFolding();
				for (OriHalfedge he : f.halfedges) {
					if (he.vertex.tmpFlg) {
						continue;
					}
					he.vertex.tmpFlg = true;

					he.vertex.tmpVec.x += slideUnit * f.z_order;
					he.vertex.tmpVec.y += slideUnit * f.z_order;

					Vector2d dirToCenter = new Vector2d(faceCenter);
					dirToCenter.sub(he.vertex.tmpVec);
					dirToCenter.normalize();
					dirToCenter.scale(6.0);
					he.vertex.tmpVec.add(dirToCenter);
				}
			}

			for (OriFace f : faces) {
				for (OriHalfedge he : f.halfedges) {
					he.positionForDisplay.set(he.vertex.tmpVec);
				}
				f.setOutline();
			}
		}
	}
}
