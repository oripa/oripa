/**
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2013-     ORIPA OSS Project  https://github.com/oripa/oripa
 * Copyright (C) 2005-2009 Jun Mitani         http://mitani.cs.tsukuba.ac.jp/

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package oripa.persistent.entity.exporter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector2d;

import oripa.domain.fold.FoldedModel;
import oripa.domain.fold.OverlapRelationList;
import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.domain.fold.origeom.OverlapRelationValues;
import oripa.geom.RectangleDomain;
import oripa.persistent.filetool.Exporter;
import oripa.persistent.svg.SVGConstants;

/**
 * @author OUCHI Koji
 *
 */
public class FoldedModelExporterSVG implements Exporter<FoldedModel> {
	private final boolean faceOrderFlip;

	/**
	 * Constructor
	 */
	public FoldedModelExporterSVG(final boolean faceOrderFlip) {
		this.faceOrderFlip = faceOrderFlip;
	}

	@Override
	public boolean export(final FoldedModel doc, final String filepath)
			throws IOException, IllegalArgumentException {
		OrigamiModel origamiModel = doc.getOrigamiModel();
		OverlapRelationList foldedModelInfo = doc.getFoldedModelInfo();
		double paperSize = origamiModel.getPaperSize();

		double scale = (SVGConstants.size - 5) / paperSize;
		double center = SVGConstants.size / 2;

		try (var fw = new FileWriter(filepath);
				var bw = new BufferedWriter(fw);) {
			Vector2d maxV = new Vector2d(-Double.MAX_VALUE,
					-Double.MAX_VALUE);
			Vector2d modelCenter = new Vector2d();

			List<OriFace> faces = origamiModel.getFaces();

			var domain = new RectangleDomain();
			for (OriFace face : faces) {
				face.halfedgeStream().forEach(he -> {
					domain.enlarge(he.getPosition());
				});
			}
			maxV.x = domain.getRight();
			maxV.y = domain.getBottom();

			modelCenter.x = domain.getCenterX();
			modelCenter.y = domain.getCenterY();
			bw.write(SVGConstants.head);
			bw.write(SVGConstants.gradient);

			var sortedFaces = sortFaces(faces, foldedModelInfo.getOverlapRelation());

			for (int i = 0; i < sortedFaces.size(); i++) {
				OriFace face = faceOrderFlip ? sortedFaces.get(i)
						: sortedFaces.get(sortedFaces.size() - i - 1);
				var points = new ArrayList<Vector2d>();
				for (var he : face.halfedgeIterable()) {
					var position = he.getPosition();
					var nextPosition = he.getNext().getPosition();
					if (position.x > maxV.x) {
						throw new IllegalArgumentException(
								"Size of vertices exceeds maximum");
					}

					double x1 = (position.x - modelCenter.x) * scale
							+ center;
					double y1 = -(position.y - modelCenter.y) * scale
							+ center;
					double x2 = (nextPosition.x - modelCenter.x)
							* scale
							+ center;
					double y2 = -(nextPosition.y - modelCenter.y)
							* scale
							+ center;
					if (!points.contains(new Vector2d(x1, y1))) {
						points.add(new Vector2d(x1, y1));
					}
					if (!points.contains(new Vector2d(x2, y2))) {
						points.add(new Vector2d(x2, y2));
					}
				}
				if (!face.isEmptyPrecreases()) {
					bw.write("<g>");
				}
				if ((!face.isFaceFront() && faceOrderFlip)
						|| (face.isFaceFront() && !faceOrderFlip)) {
					bw.write(SVGConstants.polygonStart);
				} else {
					bw.write(SVGConstants.polygonStart2);
				}
				for (Vector2d p : points) {
					bw.write(p.x + "," + p.y + " ");
				}
				bw.write(" z\" />\n");

				for (var oriLine : face.precreaseIterable()) {
					double x1 = (oriLine.p0.x - modelCenter.x) * scale
							+ center;
					double y1 = -(oriLine.p0.y - modelCenter.y) * scale
							+ center;
					double x2 = (oriLine.p1.x - modelCenter.x)
							* scale
							+ center;
					double y2 = -(oriLine.p1.y - modelCenter.y)
							* scale
							+ center;
					bw.write("<line x1=\"" + x1);
					bw.write("\" y1=\"" + y1);
					bw.write("\" x2=\"" + x2);
					bw.write("\" y2=\"" + y2);
					bw.write("\" style=\"stroke:black;stroke-width:2px;\"/>\n");

				}
				if (!face.isEmptyPrecreases()) {
					bw.write("</g>");
				}
			}
			bw.write(SVGConstants.end);
		}
		return true;

	}

	private List<OriFace> sortFaces(final List<OriFace> faces, final int[][] overlapRelation) {
		ArrayList<OriFace> sortedFaces = new ArrayList<>();
		boolean[] isSorted = new boolean[faces.size()];
		for (int i = 0; i < faces.size(); i++) {
			for (int j = 0; j < overlapRelation.length; j++) {
				if (!isSorted[j]) {
					if (canAddFace(isSorted, overlapRelation[j])) {
						isSorted[j] = true;
						sortedFaces.add(faces.get(j));
						break;
					}
				}
			}
		}

		return sortedFaces;
	}

	private boolean canAddFace(final boolean[] isSorted, final int[] overlapRelationOfJ) {
		for (int k = 0; k < isSorted.length; k++) {
			if ((!isSorted[k])
					&& overlapRelationOfJ[k] == OverlapRelationValues.LOWER) {
				return false;
			}
		}
		return true;
	}
}
