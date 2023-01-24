/**
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2005-2009 Jun Mitani http://mitani.cs.tsukuba.ac.jp/

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
package oripa.domain.fold.halfedge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.cptool.ElementRemover;
import oripa.domain.fold.origeom.OriGeomUtil;
import oripa.geom.RectangleDomain;
import oripa.util.StopWatch;
import oripa.value.OriLine;

public class OrigamiModelFactory {
	private static final Logger logger = LoggerFactory.getLogger(OrigamiModelFactory.class);

	private final OriVerticesFactory verticesFactory = new OriVerticesFactory();
	private final OriEdgesFactory edgesFactory = new OriEdgesFactory();
	private final OriFacesFactory facesFactory = new OriFacesFactory();

	private final ElementRemover remover = new ElementRemover();

	private final ModelComponentExtractor componentExtractor = new ModelComponentExtractor();

	/**
	 * Constructs the half-edge based data structure which describes relation
	 * among faces and edges and store it into {@code OrigamiModel}. This is a
	 * preparation for estimating folded shape with layers: this method removes
	 * meaningless vertices.
	 *
	 * @param creasePattern
	 * @return A model data converted from crease pattern.
	 */
	public OrigamiModel createOrigamiModel(
			final Collection<OriLine> creasePattern, final double pointEps) {
		return this.createOrigamiModelImpl3(creasePattern, pointEps);
	}

	public List<OrigamiModel> createOrigamiModels(
			final Collection<OriLine> creasePattern, final double pointEps) {
		return createOrigamiModelsImpl(creasePattern, pointEps);
	}

	/**
	 * Constructs the half-edge based data structure which describes relation
	 * among faces and edges and store it into {@code OrigamiModel}. This method
	 * simply constructs the data structure and does not execute other
	 * operations like cleaning up given crease pattern. So there may be some
	 * error in the returned data.
	 *
	 * @param creasePattern
	 * @param paperSize
	 * @return A model data converted from crease pattern.
	 */
	// TODO: change as: throw error if creation failed.
	public OrigamiModel buildOrigamiForSubfaces(
			final Collection<OriLine> creasePattern, final double paperSize, final double pointEps) {
		OrigamiModel origamiModel = new OrigamiModel(paperSize);
		List<OriFace> faces = origamiModel.getFaces();
		List<OriEdge> edges = origamiModel.getEdges();
		List<OriVertex> vertices = origamiModel.getVertices();

		buildVertices(creasePattern, vertices, pointEps);

		if (!buildFaces(vertices, faces, pointEps)) {
			return origamiModel;
		}

		buildEdges(edges, faces);

		origamiModel.setHasModel(true);

		return origamiModel;
	}

	private void buildVertices(final Collection<OriLine> creasePatternWithoutAux,
			final Collection<OriVertex> vertices, final double pointEps) {
		vertices.clear();
		vertices.addAll(verticesFactory.createOriVertices(creasePatternWithoutAux, pointEps));
	}

	private boolean buildFaces(final Collection<OriVertex> vertices,
			final Collection<OriFace> faces, final double pointEps) {
		faces.clear();
		return facesFactory.buildFaces(vertices, faces, pointEps);
	}

	private void buildEdges(final List<OriEdge> edges, final List<OriFace> faces) {
		edges.clear();
		edges.addAll(edgesFactory.createOriEdges(faces));
	}

	/**
	 * Constructs the half-edge based data structure which describes relation
	 * among faces and edges and store it into {@code OrigamiModel}. This is a
	 * preparation for estimating folded shape with layers: this method removes
	 * meaningless vertices.
	 *
	 * @param creasePattern
	 * @return A model data converted from crease pattern.
	 */
	private OrigamiModel createOrigamiModelImpl3(
			final Collection<OriLine> creasePattern, final double pointEps) {

		var watch = new StopWatch(true);

		var simplifiedCreasePattern = createCreasePatternWithoutPrecreases(creasePattern);

		List<OriLine> precreases = createPrecreases(creasePattern);

		logger.debug(
				"removeMeaninglessVertices() start: " + watch.getMilliSec() + "[ms]");
		remover.removeMeaninglessVertices(simplifiedCreasePattern, pointEps);
		logger.debug(
				"removeMeaninglessVertices() end: " + watch.getMilliSec() + "[ms]");

		var vertices = new ArrayList<OriVertex>();
		buildVertices(simplifiedCreasePattern, vertices, pointEps);
		OrigamiModel origamiModel = create(vertices, precreases, pointEps);

		logger.debug(
				"createOrigamiModelImpl3(): " + watch.getMilliSec() + "[ms]");
		return origamiModel;
	}

	private List<OrigamiModel> createOrigamiModelsImpl(
			final Collection<OriLine> creasePattern, final double pointEps) {

		var watch = new StopWatch(true);

		var simplifiedCreasePattern = createCreasePatternWithoutPrecreases(creasePattern);

		List<OriLine> precreases = createPrecreases(creasePattern);

		logger.debug(
				"removeMeaninglessVertices() start: " + watch.getMilliSec() + "[ms]");
		remover.removeMeaninglessVertices(simplifiedCreasePattern, pointEps);
		logger.debug(
				"removeMeaninglessVertices() end: " + watch.getMilliSec() + "[ms]");

		var boundaryCreasePattern = simplifiedCreasePattern.stream()
				.filter(line -> line.isBoundary())
				.collect(Collectors.toList());

		var wholeVertices = new ArrayList<OriVertex>();
		buildVertices(simplifiedCreasePattern, wholeVertices, pointEps);

		var boundaryVertices = new ArrayList<OriVertex>();
		buildVertices(boundaryCreasePattern, boundaryVertices, pointEps);

		var boundaryFaces = facesFactory.createBoundaryFaces(boundaryVertices);

		var origamiModels = new ArrayList<OrigamiModel>();

		for (var boundaryFace : boundaryFaces) {
			var modelVertices = componentExtractor.extractByBoundary(
					wholeVertices, boundaryFace, pointEps);
			var modelPrecreases = componentExtractor.extractByBoundary(
					precreases, boundaryFace, pointEps);

			origamiModels.add(create(modelVertices, modelPrecreases, pointEps));
		}

		logger.debug("create origami models: {}", origamiModels);

		return origamiModels;
	}

	private OrigamiModel create(final List<OriVertex> modelVertices, final List<OriLine> modelPrecreases,
			final double pointEps) {
		OrigamiModel origamiModel = new OrigamiModel(computePaperSize(modelVertices));
		List<OriFace> faces = origamiModel.getFaces();
		List<OriEdge> edges = origamiModel.getEdges();

		origamiModel.setVertices(modelVertices);

		List<OriVertex> vertices = origamiModel.getVertices();

		// Construct the faces
		if (!buildFaces(vertices, faces, pointEps)) {
			return origamiModel;
		}

		buildEdges(edges, faces);

		// attach precrease lines to faces
		for (OriFace face : faces) {
			face.addAllPrecreases(modelPrecreases.stream()
					.filter(precrease -> OriGeomUtil.isSegmentIncludedInFace(face, precrease, pointEps))
					.collect(Collectors.toList()));
		}
		origamiModel.setHasModel(true);

		return origamiModel;
	}

	private double computePaperSize(final List<OriVertex> vertices) {

		var domain = new RectangleDomain();

		domain.enlarge(vertices.stream()
				.map(v -> v.getPosition())
				.collect(Collectors.toList()));

		return domain.maxWidthHeight();
	}

	private Set<OriLine> createCreasePatternWithoutPrecreases(final Collection<OriLine> creasePattern) {
		return creasePattern.stream()
				.filter(line -> !line.isAux())
				.collect(Collectors.toSet());
	}

	private List<OriLine> createPrecreases(final Collection<OriLine> creasePattern) {
		return creasePattern.stream()
				.filter(line -> line.isAux())
				.map(line -> new OriLine(line))
				.collect(Collectors.toList());
	}
}
