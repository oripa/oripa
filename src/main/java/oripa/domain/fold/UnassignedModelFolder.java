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

import java.util.ArrayList;
import java.util.List;

import oripa.domain.fold.halfedge.OrigamiModel;

/**
 * @author OUCHI Koji
 *
 */
class UnassignedModelFolder implements Folder {
    private final FaceDisplayModifier faceDisplayModifier = new FaceDisplayModifier();
    private final SimpleFolder simpleFolder;
    private final LayerOrderEnumerator layerOrderEnumerator;

    public UnassignedModelFolder(final SimpleFolder simpleFolder,
            final LayerOrderEnumerator enumerator) {
        this.simpleFolder = simpleFolder;
        this.layerOrderEnumerator = enumerator;
    }

    @Override
    public Result fold(final OrigamiModel origamiModel, final double eps, final EstimationType estimationType) {
        simpleFolder.simpleFoldWithoutZorder(origamiModel, eps);
        faceDisplayModifier.setCurrentPositionsToDisplayPositions(origamiModel);

        if (estimationType == EstimationType.X_RAY) {
            origamiModel.setFolded(true);
            return new Result(new FoldedModel(origamiModel, List.of(), List.of()), new EstimationResultRules());
        }

        var firstOnly = estimationType == EstimationType.FIRST_ONLY;

        var assignmentEnumerator = new AssignmentEnumerator();

        var results = new ArrayList<LayerOrderEnumerator.Result>();

        assignmentEnumerator.enumerate(origamiModel,
                assignedModel -> {
                    if (firstOnly && results.stream().anyMatch(result -> !result.isEmpty())) {
                        return;
                    }
                    results.add(layerOrderEnumerator.enumerate(assignedModel, eps, firstOnly));
                });

        origamiModel.setFolded(true);

        return new Result(
                new FoldedModel(origamiModel,
                        results.stream()
                                .flatMap(result -> result.getOverlapRelations().stream())
                                .toList(),
                        results.get(0).getSubfaces()),
                results.stream()
                        .map(result -> result.getRules())
                        .reduce(new EstimationResultRules(), (a, b) -> a.or(b)));

    }
}
