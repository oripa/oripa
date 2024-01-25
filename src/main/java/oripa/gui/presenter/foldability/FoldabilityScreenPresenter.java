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
import java.util.stream.Collectors;

import oripa.domain.cptool.OverlappingLineExtractor;
import oripa.domain.fold.foldability.FoldabilityChecker;
import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.halfedge.OriVertex;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.geom.RectangleDomain;
import oripa.gui.presenter.creasepattern.CreasePatternGraphicDrawer;
import oripa.gui.view.creasepattern.ObjectGraphicDrawer;
import oripa.gui.view.creasepattern.PaintComponentGraphics;
import oripa.gui.view.foldability.FoldabilityScreenView;
import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
public class FoldabilityScreenPresenter {
	private final FoldabilityScreenView view;
	private final OrigamiModel origamiModel;
	private final Collection<OriLine> creasePattern;
	private final boolean zeroLineWidth;

	private Collection<OriVertex> violatingVertices;
	private Collection<OriFace> violatingFaces;
	private Collection<OriLine> overlappingLines;

	private final FoldabilityChecker foldabilityChecker = new FoldabilityChecker();

	private final double pointEps;

	public FoldabilityScreenPresenter(
			final FoldabilityScreenView view,
			final OrigamiModel origamiModel,
			final Collection<OriLine> creasePattern,
			final boolean zeroLineWidth,
			final double pointEps) {
		this.view = view;

		this.origamiModel = origamiModel;
		this.creasePattern = creasePattern.stream()
				.map(line -> new OriLine(line)).collect(Collectors.toList());
		this.zeroLineWidth = zeroLineWidth;
		this.pointEps = pointEps;

		setModel();

		setListeners();
	}

	private void setModel() {

		violatingVertices = foldabilityChecker.findViolatingVertices(
				origamiModel.getVertices());

		view.setViolatingVertices(violatingVertices);

		violatingFaces = foldabilityChecker.findViolatingFaces(
				origamiModel.getFaces());

		var overlappingLineExtractor = new OverlappingLineExtractor();
		overlappingLines = overlappingLineExtractor.extract(creasePattern, pointEps);

		var domain = RectangleDomain.createFromSegments(creasePattern);
		view.updateCenterOfPaper(domain.getCenterX(), domain.getCenterY());

	}

	private void setListeners() {
		view.setPaintComponentListener(this::paintComponent);

	}

	private void paintComponent(final PaintComponentGraphics p) {

		var bufferObjDrawer = p.getBufferObjectDrawer();
		var objDrawer = p.getObjectDrawer();

		if (!zeroLineWidth) {
			bufferObjDrawer.setAntiAlias(true);
		}

		var scale = view.getScale();

		drawCreasePattern(bufferObjDrawer, scale);

		drawFoldability(bufferObjDrawer, scale);

		p.drawBufferImage();

		drawVertexViolationNames(objDrawer);
	}

	private void drawCreasePattern(final ObjectGraphicDrawer objDrawer, final double scale) {
		CreasePatternGraphicDrawer drawer = new CreasePatternGraphicDrawer();

		drawer.highlightOverlappingLines(objDrawer, overlappingLines, scale);

		drawer.drawAllLines(objDrawer, creasePattern, scale, zeroLineWidth);
		drawer.drawCreaseVertices(objDrawer, creasePattern, scale);
	}

	private void drawFoldability(final ObjectGraphicDrawer objDrawer, final double scale) {
		FoldabilityGraphicDrawer drawer = new FoldabilityGraphicDrawer();

		drawer.draw(objDrawer, origamiModel, violatingFaces, violatingVertices, scale);
	}

	private void drawVertexViolationNames(final ObjectGraphicDrawer drawer) {
		var pickedViolatingVertex = view.getPickedViolatingVertex();

		if (pickedViolatingVertex == null) {
			return;
		}

		var violationNames = foldabilityChecker.getVertexViolationNames(pickedViolatingVertex);

		drawer.drawString("error(s): " + String.join(", ", violationNames), 0, 10);
	}

	public void setViewVisible(final boolean visible) {
		view.setVisible(visible);
	}
}
