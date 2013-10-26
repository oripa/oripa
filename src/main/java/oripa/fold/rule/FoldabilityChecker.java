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
package oripa.fold.rule;

import java.util.Collection;

import oripa.fold.OriFace;
import oripa.fold.OriVertex;

/**
 * @author Koji
 *
 */
public class FoldabilityChecker {

	public boolean modelIsProbablyFoldable(Collection<OriVertex> vertices, Collection<OriFace> faces) {

		ConjunctionLoop<OriVertex> maekawaConjunction = new ConjunctionLoop<>(
				new MaekawaTheorem());
		ConjunctionLoop<OriVertex> kawasakiConjunction = new ConjunctionLoop<>(
				new KawasakiTheorem());
		
		ConjunctionLoop<OriFace> convexRuleConjunction = new ConjunctionLoop<>(
				new FaceIsConvex());

		return maekawaConjunction.holds(vertices) &&
				kawasakiConjunction.holds(vertices) &&
				convexRuleConjunction.holds(faces);
	}

	public Collection<OriVertex> findViolatingVertices(Collection<OriVertex> vertices) {
		//--------
		// test Maekawa's theorem

		ConjunctionLoop<OriVertex> maekawaConjunction = new ConjunctionLoop<>(
				new MaekawaTheorem());

		//--------
		// test Kawasaki's theorem

		ConjunctionLoop<OriVertex> kawasakiConjunction = new ConjunctionLoop<>(
				new KawasakiTheorem());

		Collection<OriVertex> violatingVertices =
				maekawaConjunction.findViolations(vertices);

		violatingVertices.addAll(
				kawasakiConjunction.findViolations(vertices));
		

		return violatingVertices;
	}


	public Collection<OriFace> findViolatingFaces(Collection<OriFace> faces) {
		//--------
		// test convex-face condition
		
		ConjunctionLoop<OriFace> convexRuleConjunction = new ConjunctionLoop<>(
				new FaceIsConvex());

		Collection<OriFace> violatingFaces = convexRuleConjunction.findViolations(faces);

		return violatingFaces;
	}
}
