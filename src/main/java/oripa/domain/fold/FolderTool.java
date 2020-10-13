package oripa.domain.fold;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.vecmath.Vector2d;

import oripa.geom.GeomUtil;
import oripa.geom.Line;
import oripa.value.OriLine;

public class FolderTool {

	BoundBox calcFoldedBoundingBox(final List<OriFace> faces) {
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

	boolean isLineCrossFace4(final OriFace face, final OriHalfedge heg, final double size) {
		Vector2d p1 = heg.positionAfterFolded;
		Vector2d p2 = heg.next.positionAfterFolded;
		Vector2d dir = new Vector2d();
		dir.sub(p2, p1);
		Line heLine = new Line(p1, dir);

		for (OriHalfedge he : face.halfedges) {
			// About the relation of contours (?)

			// Check if the line is on the countour of the face
			if (GeomUtil.distancePointToLine(he.positionAfterFolded, heLine) < 1
					&& GeomUtil.distancePointToLine(he.next.positionAfterFolded, heLine) < 1) {
				return false;
			}
		}

		Vector2d preCrossPoint = null;
		for (OriHalfedge he : face.halfedges) {
			// Checks if the line crosses any of the edges of the face
			Vector2d cp = GeomUtil.getCrossPoint(he.positionAfterFolded,
					he.next.positionAfterFolded, heg.positionAfterFolded,
					heg.next.positionAfterFolded);
			if (cp == null) {
				continue;
			}

			if (preCrossPoint == null) {
				preCrossPoint = cp;
			} else {
				if (GeomUtil.distance(cp, preCrossPoint) > size * 0.001) {
					return true;
				}
			}
		}

		// Checkes if the line is in the interior of the face
		if (isOnFace(face, heg.positionAfterFolded, size)) {
			return true;
		}
		if (isOnFace(face, heg.next.positionAfterFolded, size)) {
			return true;
		}

		return false;
	}

	private boolean isOnFace(final OriFace face, final Vector2d v, final double size) {

		int heNum = face.halfedges.size();

		// Return false if the vector is on the contour of the face
		for (int i = 0; i < heNum; i++) {
			OriHalfedge he = face.halfedges.get(i);
			if (GeomUtil.distancePointToSegment(v, he.positionAfterFolded,
					he.next.positionAfterFolded) < size * 0.001) {
				return false;
			}
		}

		OriHalfedge baseHe = face.halfedges.get(0);
		boolean baseFlg = GeomUtil.CCWcheck(baseHe.positionAfterFolded,
				baseHe.next.positionAfterFolded, v);

		for (int i = 1; i < heNum; i++) {
			OriHalfedge he = face.halfedges.get(i);
			if (GeomUtil.CCWcheck(he.positionAfterFolded, he.next.positionAfterFolded,
					v) != baseFlg) {
				return false;
			}
		}

		return true;
	}

	// Turn the model over
	public void filpAll(final OrigamiModel origamiModel) {
		Vector2d maxV = new Vector2d(-Double.MAX_VALUE, -Double.MAX_VALUE);
		Vector2d minV = new Vector2d(Double.MAX_VALUE, Double.MAX_VALUE);

		List<OriFace> faces = origamiModel.getFaces();
		for (OriFace face : faces) {
			face.z_order = -face.z_order;
			for (OriHalfedge he : face.halfedges) {
				maxV.x = Math.max(maxV.x, he.vertex.p.x);
				maxV.y = Math.max(maxV.y, he.vertex.p.y);
				minV.x = Math.min(minV.x, he.vertex.p.x);
				minV.y = Math.min(minV.y, he.vertex.p.y);
			}
		}

		double centerX = (maxV.x + minV.x) / 2;

		faces.stream().flatMap(f -> f.halfedges.stream()).forEach(he -> {
			he.positionForDisplay.x = 2 * centerX - he.positionForDisplay.x;
		});

		faces.forEach(face -> {
			face.faceFront = !face.faceFront;
			face.setOutline();
		});

		Collections.sort(faces, new FaceOrderComparator());

		Collections.reverse(origamiModel.getSortedFaces());

	}

	//
	public void setFacesOutline(
			final List<OriVertex> vertices, final List<OriFace> faces,
			final boolean isSlide) {
		int minDepth = Integer.MAX_VALUE;
		int maxDepth = -Integer.MAX_VALUE;

		for (OriFace f : faces) {
			minDepth = Math.min(minDepth, f.z_order);
			maxDepth = Math.max(minDepth, f.z_order);
			for (OriHalfedge he : f.halfedges) {
				he.positionForDisplay.set(he.vertex.p);
			}
			f.setOutline();
		}

		if (isSlide) {
			double slideUnit = 10.0 / (maxDepth - minDepth);
			for (OriVertex v : vertices) {
				v.tmpFlg = false;
				v.tmpVec.set(v.p);
			}

			for (OriFace f : faces) {
				Vector2d faceCenter = new Vector2d();
				for (OriHalfedge he : f.halfedges) {
					faceCenter.add(he.vertex.p);
				}
				faceCenter.scale(1.0 / f.halfedges.size());

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

	public boolean cleanDuplicatedLines(final Collection<OriLine> creasePattern) {
		System.out.println("pre cleanDuplicatedLines " + creasePattern.size());
		ArrayList<OriLine> tmpLines = new ArrayList<OriLine>(creasePattern.size());
		for (OriLine l : creasePattern) {
			boolean bSame = false;
			// Test if the line is already in tmpLines to prevent duplication
			for (OriLine line : tmpLines) {
				if (GeomUtil.isSameLineSegment(line, l)) {
					bSame = true;
					break;
				}
			}
			if (bSame) {
				continue;
			}
			tmpLines.add(l);
		}

		if (creasePattern.size() == tmpLines.size()) {
			return false;
		}

		creasePattern.clear();
		creasePattern.addAll(tmpLines);
		System.out.println("after cleanDuplicatedLines " + creasePattern.size());

		return true;
	}

}
