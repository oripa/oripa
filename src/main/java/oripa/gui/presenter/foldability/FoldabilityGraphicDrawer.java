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
package oripa.gui.presenter.foldability;

import java.util.Collection;
import java.util.List;

import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.halfedge.OriVertex;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.gui.view.creasepattern.ObjectGraphicDrawer;

/**
 * @author OUCHI Koji
 *
 */
public class FoldabilityGraphicDrawer {
	public void draw(
			final ObjectGraphicDrawer drawer,
			final OrigamiModel origamiModel,
			final Collection<OriFace> violatingFaces,
			final Collection<OriVertex> violatingVertices,
			final double scale) {
		List<OriFace> faces = origamiModel.getFaces();

		for (OriFace face : faces) {
			if (violatingFaces.contains(face)) {
				drawer.selectViolatingFaceColor();
			} else {
				drawer.selectNormalFaceColor();
			}
			drawer.fillFace(face.createOutlineVerticesBeforeFolding());
		}

		drawer.selectViolatingVertexColor();
		for (OriVertex v : violatingVertices) {
			drawer.selectViolatingVertexSize(scale);
			var position = v.getPositionBeforeFolding();
			drawer.drawVertex(position);
		}

//	if (bDrawFaceID) {
//		g2d.setColor(Color.BLACK);
//		for (OriFace face : faces) {
//			g2d.drawString("" + face.getFaceID(), (int) face.getCentroidBeforeFolding().x,
//					(int) face.getCentroidBeforeFolding().y);
//		}
//	}
//
//	if (Constants.FOR_STUDY) {
//		List<OriVertex> vertices = origamiModel.getVertices();
//		paintForStudy(g2d, faces, vertices);
//	}
	}

//private void paintForStudy(final Graphics2D g2d, final Collection<OriFace> faces,
//		final Collection<OriVertex> vertices) {
//	g2d.setColor(new Color(255, 210, 220));
//	for (OriFace face : faces) {
//		// switch the if statement by comment out?
//		if (face.getIndexForStack() == 0) {
//			g2d.setColor(Color.RED);
//			g2d.fill(face.getOutlineBeforeFolding());
//		} else {
//			g2d.setColor(face.colorForDebug);
//		}
//
//		if (violatingFaces.contains(face)) {
//			g2d.setColor(Color.RED);
//		} else {
//			if (face.isFaceFront()) {
//				g2d.setColor(new Color(255, 200, 200));
//			} else {
//				g2d.setColor(new Color(200, 200, 255));
//			}
//		}
//
//		g2d.fill(face.getOutlineBeforeFolding());
//	}
//}

}
