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

import java.util.Collection;

import oripa.util.collection.ConjunctionLoop;

/**
 * @author Koji
 *
 */
public class FoldabilityChecker {
	private final ConjunctionLoop<OriVertex> maekawaConjunction = new ConjunctionLoop<>(
			new MaekawaTheorem());
	private final ConjunctionLoop<OriVertex> kawasakiConjunction = new ConjunctionLoop<>(
			new KawasakiTheorem());
	private final ConjunctionLoop<OriVertex> bigLittleBigConjunction = new ConjunctionLoop<>(
			new BigLittleBigLemma());

	private final ConjunctionLoop<OriFace> convexRuleConjunction = new ConjunctionLoop<>(
			new FaceIsConvex());

	public boolean modelIsProbablyFoldable(final Collection<OriVertex> vertices,
			final Collection<OriFace> faces) {

		return maekawaConjunction.holds(vertices) &&
				kawasakiConjunction.holds(vertices) &&
				bigLittleBigConjunction.holds(vertices) &&
				convexRuleConjunction.holds(faces);
	}

	public Collection<OriVertex> findViolatingVertices(final Collection<OriVertex> vertices) {
		Collection<OriVertex> violatingVertices = maekawaConjunction.findViolations(vertices);

		violatingVertices.addAll(
				kawasakiConjunction.findViolations(vertices));

		violatingVertices.addAll(
				bigLittleBigConjunction.findViolations(vertices));

		return violatingVertices;
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
