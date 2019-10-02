package oripa.fold;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.vecmath.Vector2d;

import oripa.geom.GeomUtil;
import oripa.geom.Line;
import oripa.value.OriLine;

public class FolderTool {

	//	public void setCrossLine(List<OriLine> crossLines, OriLine line, List<OriFace> sortedFaces) {
	//		crossLines.clear();
	//		for (OriFace face : sortedFaces) {
	//			ArrayList<Vector2d> vv = new ArrayList<Vector2d>();
	//			int crossCount = 0;
	//			for (OriHalfedge he : face.halfedges) {
	//				OriLine l = new OriLine(he.positionForDisplay.x, he.positionForDisplay.y,
	//						he.next.positionForDisplay.x, he.next.positionForDisplay.y, PaintConfig.inputLineType);
	//
	//				double params[] = new double[2];
	//				boolean res = GeomUtil.getCrossPointParam(line.p0, line.p1, l.p0, l.p1, params);
	//				if (res == true && params[0] > -0.001 && params[1] > -0.001 && params[0] < 1.001 && params[1] < 1.001) {
	//					double param = params[1];
	//					crossCount++;
	//
	//					Vector2d crossV = new Vector2d();
	//					crossV.x = (1.0 - param) * he.vertex.preP.x + param * he.next.vertex.preP.x;
	//					crossV.y = (1.0 - param) * he.vertex.preP.y + param * he.next.vertex.preP.y;
	//
	//					boolean isNewPoint = true;
	//					for (Vector2d v2d : vv) {
	//						if (GeomUtil.Distance(v2d, crossV) < 1) {
	//							isNewPoint = false;
	//							break;
	//						}
	//					}
	//					if (isNewPoint) {
	//						vv.add(crossV);
	//					}
	//				}
	//			}
	//
	//			if (vv.size() >= 2) {
	//				crossLines.add(new OriLine(vv.get(0), vv.get(1), PaintConfig.inputLineType));
	//			}
	//		}
	//
	//	}
	
	
	
	
		BoundBox calcFoldedBoundingBox(List<OriFace> faces) {
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

		boolean isLineCrossFace4(OriFace face, OriHalfedge heg, double size) {
			Vector2d p1 = heg.positionAfterFolded;
			Vector2d p2 = heg.next.positionAfterFolded;
			Vector2d dir = new Vector2d();
			dir.sub(p2, p1);
			Line heLine = new Line(p1, dir);
		
			for (OriHalfedge he : face.halfedges) {
				// About the relation of contours (?)
		
				// Check if the line is on the countour of the face
				if (GeomUtil.DistancePointToLine(he.positionAfterFolded, heLine) < 1
						&& GeomUtil.DistancePointToLine(he.next.positionAfterFolded, heLine) < 1) {
					return false;
				}
			}
		
			Vector2d preCrossPoint = null;
			for (OriHalfedge he : face.halfedges) {
				// Checks if the line crosses any of the edges of the face
				Vector2d cp = GeomUtil.getCrossPoint(he.positionAfterFolded, he.next.positionAfterFolded, heg.positionAfterFolded, heg.next.positionAfterFolded);
				if (cp == null) {
					continue;
				}
		
				if (preCrossPoint == null) {
					preCrossPoint = cp;
				} else {
					if (GeomUtil.Distance(cp, preCrossPoint) > size * 0.001) {
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

		private boolean isOnFace(OriFace face, Vector2d v, double size) {
		
			int heNum = face.halfedges.size();
		
			// Return false if the vector is on the contour of the face
			for (int i = 0; i < heNum; i++) {
				OriHalfedge he = face.halfedges.get(i);
				if (GeomUtil.DistancePointToSegment(v, he.positionAfterFolded, he.next.positionAfterFolded) < size * 0.001) {
					return false;
				}
			}
		
			OriHalfedge baseHe = face.halfedges.get(0);
			boolean baseFlg = GeomUtil.CCWcheck(baseHe.positionAfterFolded, baseHe.next.positionAfterFolded, v);
		
			for (int i = 1; i < heNum; i++) {
				OriHalfedge he = face.halfedges.get(i);
				if (GeomUtil.CCWcheck(he.positionAfterFolded, he.next.positionAfterFolded, v) != baseFlg) {
					return false;
				}
			}
		
			return true;
		}

		// Turn the model over
		public void filpAll(OrigamiModel origamiModel) {
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
		
			for (OriFace face : faces) {
				for (OriHalfedge he : face.halfedges) {
					he.positionForDisplay.x = 2 * centerX - he.positionForDisplay.x;
				}
			}
		
			for (OriFace face : faces) {
				face.faceFront = !face.faceFront;
				face.setOutline();
			}
		
			Collections.sort(faces, new FaceOrderComparator());
		
			Collections.reverse(origamiModel.getSortedFaces());
		
		
		}

		// 
		public void setFacesOutline(
				List<OriVertex> vertices, List<OriFace> faces, 
				boolean isSlide) {
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

		public boolean cleanDuplicatedLines(Collection<OriLine> creasePattern) {
			System.out.println("pre cleanDuplicatedLines " + creasePattern.size());
			ArrayList<OriLine> tmpLines = new ArrayList<>(creasePattern.size());
			for (OriLine l : creasePattern) {
				OriLine ll = l;

				boolean bSame = false;
				// Test if the line is already in tmpLines to prevent duplicity
				for (OriLine line : tmpLines) {
					if (GeomUtil.isSameLineSegment(line, ll)) {
						bSame = true;
						break;
					}
				}
				if (bSame) {
					continue;
				}
				tmpLines.add(ll);
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
