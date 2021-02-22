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
package oripa.domain.fold.foldability;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.halfedge.OriVertex;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.util.StopWatch;
import oripa.util.rule.Rule;
import oripa.util.rule.SingleRuleParallelConjunction;

/**
 * @author Koji
 *
 */
public class FoldabilityChecker {
	private static final Logger logger = LoggerFactory.getLogger(FoldabilityChecker.class);

	private enum VertexRule {
		MAEKAWA(new MaekawaTheorem(), "Maekawa"),
		KAWASAKI(new KawasakiTheorem(), "Kawasaki"),
		BIG_LITTLE_BIG(new BigLittleBigLemma(), "Big-little-big"),
		GEN_BIG_LITTLE_BIG(new GeneralizedBigLittleBigLemma(), "gen. Big-little-big");

		private final Rule<OriVertex> rule;
		private final String name;
		private final SingleRuleParallelConjunction<OriVertex> conjunction;

		private VertexRule(final Rule<OriVertex> rule, final String name) {
			this.rule = rule;
			this.name = name;
			conjunction = new SingleRuleParallelConjunction<>(rule);
		}

		public Rule<OriVertex> getRule() {
			return rule;
		}

		public String getName() {
			return name;
		}

		public SingleRuleParallelConjunction<OriVertex> getConjunction() {
			return conjunction;
		}
	}

	private final SingleRuleParallelConjunction<OriFace> convexRuleConjunction = new SingleRuleParallelConjunction<>(
			new FaceIsConvex());

	private boolean testLocalFlatFoldability(final Collection<OriVertex> vertices, final Collection<OriFace> faces) {

		return Arrays.asList(VertexRule.values()).parallelStream()
				.allMatch(rule -> rule.getConjunction().holds(vertices)) && convexRuleConjunction.holds(faces);
	}

	/**
	 * Tests local flat foldability of each vertex and face.
	 *
	 * @param origamiModel
	 * @return true if the given {@code origamiModel} is locally flat foldable.
	 */
	public boolean testLocalFlatFoldability(final OrigamiModel origamiModel) {
		return testLocalFlatFoldability(origamiModel.getVertices(), origamiModel.getFaces());
	}

	public Collection<OriVertex> findViolatingVertices(final Collection<OriVertex> vertices) {
		var watch = new StopWatch(true);

		var result = Arrays.asList(VertexRule.values()).parallelStream()
				.flatMap(rule -> rule.getConjunction().findViolations(vertices).parallelStream())
				.collect(Collectors.toList());

		logger.debug("findViolatingVertices: " + watch.getMilliSec() + "[ms]");

		return result;
	}

	public Collection<String> getVertexViolationNames(final OriVertex vertex) {
		return Arrays.asList(VertexRule.values()).stream()
				.filter(rule -> rule.getRule().violates(vertex))
				.map(rule -> rule.getName())
				.collect(Collectors.toList());
	}

	public Collection<OriFace> findViolatingFaces(final Collection<OriFace> faces) {
		var watch = new StopWatch(true);

		var result = convexRuleConjunction.findViolations(faces);

		logger.debug("findViolatingFaces: " + watch.getMilliSec() + "[ms]");

		return result;
	}
}
