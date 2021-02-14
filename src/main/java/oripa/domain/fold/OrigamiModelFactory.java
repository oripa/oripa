package oripa.domain.fold;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.cptool.ElementRemover;
import oripa.util.StopWatch;
import oripa.value.OriLine;

public class OrigamiModelFactory {
	private static final Logger logger = LoggerFactory.getLogger(OrigamiModelFactory.class);

	private final OriVerticesFactory verticesFactory = new OriVerticesFactory();
	private final OriEdgesFactory edgesFactory = new OriEdgesFactory();
	private final OriFacesFactory facesFactory = new OriFacesFactory();

	/**
	 * Constructs the half-edge based data structure which describes relation
	 * among faces and edges and store it into {@code OrigamiModel}. This is a
	 * preparation for estimating folded shape with layers: this method removes
	 * meaningless vertices.
	 *
	 * @param creasePattern
	 * @param paperSize
	 * @return A model data converted from crease pattern.
	 */
	public OrigamiModel createOrigamiModel(
			final Collection<OriLine> creasePattern, final double paperSize) {
		return this.createOrigamiModelImpl3(creasePattern, paperSize);
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
	OrigamiModel buildOrigami(
			final Collection<OriLine> creasePattern, final double paperSize) {
		OrigamiModel origamiModel = new OrigamiModel(paperSize);
		List<OriFace> faces = origamiModel.getFaces();
		List<OriEdge> edges = origamiModel.getEdges();
		List<OriVertex> vertices = origamiModel.getVertices();

		buildVertices(creasePattern, vertices);

		if (!buildFaces(vertices, faces)) {
			return origamiModel;
		}

		buildEdges(edges, faces);
		for (OriEdge e : edges) {
			e.type = e.left.tmpInt;
		}

		origamiModel.setHasModel(true);

		return origamiModel;
	}

	private void buildVertices(final Collection<OriLine> creasePatternWithoutAux,
			final Collection<OriVertex> vertices) {
		vertices.clear();
		vertices.addAll(verticesFactory.createOriVertices(creasePatternWithoutAux));
	}

	private boolean buildFaces(final Collection<OriVertex> vertices,
			final Collection<OriFace> faces) {
		faces.clear();
		return facesFactory.buildFaces(vertices, faces);
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
	 * @param paperSize
	 * @return A model data converted from crease pattern.
	 */
	private OrigamiModel createOrigamiModelImpl3(
			final Collection<OriLine> creasePattern, final double paperSize) {

		var watch = new StopWatch(true);

		var simplifiedCreasePattern = creasePattern.stream()
				.filter(line -> !line.isAux())
				.collect(Collectors.toSet());

		var remover = new ElementRemover();

		logger.debug(
				"removeMeaninglessVertices() start: " + watch.getMilliSec() + "[ms]");
		remover.removeMeaninglessVertices(simplifiedCreasePattern);
		logger.debug(
				"removeMeaninglessVertices() end: " + watch.getMilliSec() + "[ms]");

		OrigamiModel origamiModel = new OrigamiModel(paperSize);
		List<OriFace> faces = origamiModel.getFaces();
		List<OriEdge> edges = origamiModel.getEdges();
		List<OriVertex> vertices = origamiModel.getVertices();

		List<OriLine> precreases = createPrecreases(creasePattern);
		buildVertices(simplifiedCreasePattern, vertices);

		// Construct the faces
		if (!buildFaces(vertices, faces)) {
			return origamiModel;
		}

		logger.debug(
				"makeEdges() start: " + watch.getMilliSec() + "[ms]");
		buildEdges(edges, faces);
		logger.debug(
				"makeEdges() end: " + watch.getMilliSec() + "[ms]");

		for (OriEdge e : edges) {
			e.type = e.left.tmpInt;
		}
		// attach precrease lines to faces
		for (OriFace face : faces) {
			ListIterator<OriLine> iterator = precreases.listIterator();
			while (iterator.hasNext()) {
				OriLine precrease = iterator.next();
				if (OriGeomUtil.isOriLineCrossFace(face, precrease)) {
					face.precreases.add(precrease);
					iterator.remove();
				}
			}
		}
		origamiModel.setHasModel(true);

		logger.debug(
				"createOrigamiModelImpl3(): " + watch.getMilliSec() + "[ms]");
		return origamiModel;
	}

	private List<OriLine> createPrecreases(final Collection<OriLine> creasePattern) {
		return creasePattern.stream()
				.filter(line -> line.isAux())
				.map(line -> new OriLine(line))
				.collect(Collectors.toList());
	}
}
