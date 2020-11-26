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
package oripa.domain.fold;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

import oripa.util.collection.ConjunctionLoop;
import oripa.util.collection.Rule;

/**
 * @author Koji
 *
 */
public class FoldabilityChecker {

	private enum VertexRule {
		MAEKAWA(new MaekawaTheorem(), "Maekawa"),
		KAWASAKI(new KawasakiTheorem(),	"Kawasaki"),
		BIG_LITTLE_BIG(new BigLittleBigLemma(), "Big-little-big");

		private final Rule<OriVertex> rule;
		private final String name;
		private final ConjunctionLoop<OriVertex> conjunction;

		private VertexRule(final Rule<OriVertex> rule, final String name) {
			this.rule = rule;
			this.name = name;
			conjunction = new ConjunctionLoop<>(rule);
		}

		public Rule<OriVertex> getRule() {
			return rule;
		}

		public String getName() {
			return name;
		}

		public ConjunctionLoop<OriVertex> getConjunction() {
			return conjunction;
		}
	}

	private final ConjunctionLoop<OriFace> convexRuleConjunction = new ConjunctionLoop<>(
			new FaceIsConvex());

	public boolean modelIsProbablyFoldable(final Collection<OriVertex> vertices,
			final Collection<OriFace> faces) {

		return Arrays.asList(VertexRule.values()).stream()
				.allMatch(rule -> rule.getConjunction().holds(vertices)) &&
				convexRuleConjunction.holds(faces);
	}

	public Collection<OriVertex> findViolatingVertices(final Collection<OriVertex> vertices) {
		var violatingVertices = new HashSet<OriVertex>();

		Arrays.asList(VertexRule.values())
				.forEach(rule -> violatingVertices
						.addAll(rule.getConjunction().findViolations(vertices)));

		return violatingVertices;
	}

	public Collection<String> getVertexViolationNames(final OriVertex vertex) {
		return Arrays.asList(VertexRule.values()).stream()
				.filter(rule -> rule.getRule().violates(vertex))
				.map(rule -> rule.getName())
				.collect(Collectors.toList());
	}

	public Collection<OriFace> findViolatingFaces(final Collection<OriFace> faces) {
		// --------
		// test convex-face condition

		ConjunctionLoop<OriFace> convexRuleConjunction = new ConjunctionLoop<>(
				new FaceIsConvex());

		Collection<OriFace> violatingFaces = convexRuleConjunction.findViolations(faces);

		return violatingFaces;
	}
}
