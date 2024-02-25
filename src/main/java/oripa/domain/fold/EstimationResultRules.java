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

import java.util.List;
import java.util.stream.Stream;

import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.origeom.EstimationResult;
import oripa.util.rule.Rule;

public class EstimationResultRules {
	private EstimationResult estimationResult;

	private List<Rule<OriFace>> rules = List.of();

	public EstimationResultRules() {
		estimationResult = EstimationResult.NOT_CHANGED;
	}

	public EstimationResultRules(final EstimationResult result) {
		estimationResult = result;
	}

	public void addMVAssignmentViolation(final List<OriFace> violatingFaces) {
		rules = Stream.concat(
				rules.stream(),
				Stream.of(new LayerOrderRule("MV", violatingFaces)))
				.toList();
	}

	public void addTransitivityViolation(final List<OriFace> violatingFaces) {
		rules = Stream.concat(
				rules.stream(),
				Stream.of(new LayerOrderRule("transitivity", violatingFaces)))
				.toList();
	}

	public void addPenetrationViolation(final List<OriFace> violatingFaces) {
		rules = Stream.concat(
				rules.stream(),
				Stream.of(new LayerOrderRule("penetration", violatingFaces)))
				.toList();
	}

	public void addStackCondition4FacesViolation(final List<OriFace> violatingFaces) {
		rules = Stream.concat(
				rules.stream(),
				Stream.of(new LayerOrderRule("stack4Faces", violatingFaces)))
				.toList();
	}

	public void addCover3FacesViolation(final List<OriFace> violatingFaces) {
		rules = Stream.concat(
				rules.stream(),
				Stream.of(new LayerOrderRule("cover3Faces", violatingFaces)))
				.toList();
	}

	public void addCover4FacesViolation(final List<OriFace> violatingFaces) {
		rules = Stream.concat(
				rules.stream(),
				Stream.of(new LayerOrderRule("cover4Faces", violatingFaces)))
				.toList();
	}

	EstimationResult getEstimationResult() {
		return estimationResult;
	}

	void setEstimationResult(final EstimationResult result) {
		estimationResult = result;
	}

	public boolean isUnfoldable() {
		return estimationResult == EstimationResult.UNFOLDABLE;
	}

	public List<Rule<OriFace>> getAllRules() {
		return rules;
	}

	public EstimationResultRules or(final EstimationResultRules result) {
		if (estimationResult.or(result.estimationResult) == EstimationResult.UNFOLDABLE) {
			var ret = new EstimationResultRules(EstimationResult.UNFOLDABLE);
			ret.rules = Stream.concat(rules.stream(), result.rules.stream()).toList();
			return ret;
		}

		if (estimationResult.or(result.estimationResult) == EstimationResult.CHANGED) {
			return new EstimationResultRules(EstimationResult.CHANGED);
		}

		return new EstimationResultRules(EstimationResult.NOT_CHANGED);
	}

	public List<String> getViolationNames(final OriFace violatingFace) {
		return getAllRules().stream()
				.filter(rule -> rule.violates(violatingFace))
				.map(Rule::toString)
				.toList();
	}

	@Override
	public String toString() {
		return String.join(",", getAllRules().stream().map(Rule::toString).toList());
	}
}