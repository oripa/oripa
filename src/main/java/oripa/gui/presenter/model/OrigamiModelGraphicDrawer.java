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
package oripa.gui.presenter.model;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.gui.view.model.ModelDisplayMode;
import oripa.gui.view.model.ObjectGraphicDrawer;
import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
public class OrigamiModelGraphicDrawer {
    private static final Logger logger = LoggerFactory.getLogger(OrigamiModelGraphicDrawer.class);

    public void draw(final ObjectGraphicDrawer drawer,
            final OrigamiModel origamiModel,
            final OriLine scissorsLine,
            final ModelDisplayMode modelDisplayMode,
            final double scale) {
        List<OriFace> faces = origamiModel.getFaces();

        drawer.selectDefaultStroke(scale);
        if (modelDisplayMode == ModelDisplayMode.FILL_ALPHA) {
            drawer.setTranslucent(true);
        }
        for (OriFace face : faces) {
            logger.trace("face: " + face);
            switch (modelDisplayMode) {
            case FILL_ALPHA:
                drawer.selectFaceColor();
                drawer.fillFace(face.createOutlineVerticesAfterFolding());
                break;
            case FILL_NONE:
            }

            drawer.selectEdgeColor();
            face.halfedgeStream().forEach(he -> {
                var pairOpt = he.getPair();

                pairOpt.ifPresentOrElse(
                        pair -> drawer.selectFaceEdgeStroke(scale),
                        () -> drawer.selectPaperBoundaryStroke(scale));

                var position = he.getPositionForDisplay();
                var nextPosition = he.getNext().getPositionForDisplay();
                drawer.drawLine(position, nextPosition);
            });
        }

        if (scissorsLine != null) {
            drawer.setTranslucent(false);
            drawer.selectScissorsLineStroke(scale);
            drawer.selectScissorsLineColor();

            drawer.drawLine(scissorsLine);
        }

    }
}
