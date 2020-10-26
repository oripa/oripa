package oripa.domain.fold;

import java.util.List;

import javax.vecmath.Vector2d;

public class FolderTool {

	public BoundBox calcFoldedBoundingBox(final List<OriFace> faces) {
		Vector2d foldedBBoxLT = new Vector2d(Double.MAX_VALUE, Double.MAX_VALUE);
		Vector2d foldedBBoxRB = new Vector2d(-Double.MAX_VALUE, -Double.MAX_VALUE);

		for (OriFace face : faces) {
			for (OriHalfedge he : face.halfedges) {
				foldedBBoxLT.x = Math.min(foldedBBoxLT.x, he.tmpVec.x);
				foldedBBoxLT.y = Math.min(foldedBBoxLT.y, he.tmpVec.y);
				foldedBBoxRB.x = Math.max(foldedBBoxRB.x, he.tmpVec.x);
				foldedBBoxRB.y = Math.max(foldedBBoxRB.y, he.tmpVec.y);
			}
		}

		return new BoundBox(foldedBBoxLT, foldedBBoxRB);
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
