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
package oripa.cli;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;
import oripa.application.FileAccessService;
import oripa.domain.fold.Folder;
import oripa.domain.fold.FolderFactory;
import oripa.domain.fold.TestedOrigamiModelFactory;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.persistence.doc.Doc;
import oripa.persistence.entity.FoldedModelEntity;
import oripa.persistence.entity.exporter.FoldedModelAllExporterFOLD;
import oripa.persistence.entity.exporter.FoldedModelSingleExporterFOLD;

/**
 * @author OUCHI Koji
 *
 */
public class CommandLineFolder {
    private static final Logger logger = LoggerFactory.getLogger(CommandLineFolder.class);

    private final FileAccessService<Doc> creasePatternFileAccess;

    @Inject
    public CommandLineFolder(
            final FileAccessService<Doc> creasePatternFileAccess) {
        this.creasePatternFileAccess = creasePatternFileAccess;
    }

    public void fold(final String inputFilePath, final boolean any, final boolean split, final String outputFilePath,
            final double pointEps) {

        if (!outputFilePath.endsWith(".fold")) {
            throw new IllegalArgumentException("Output format is not supported. acceptable format: fold");
        }

        try {
            var creasePattern = creasePatternFileAccess.loadFile(inputFilePath).get().getCreasePattern();
            var modelFactory = new TestedOrigamiModelFactory();

            List<OrigamiModel> origamiModels = modelFactory.createOrigamiModels(creasePattern, pointEps);

            if (origamiModels.size() > 1) {
                throw new IllegalArgumentException("Input should be a single model.");
            }

            var origamiModel = origamiModels.get(0);

            if (!origamiModel.isLocallyFlatFoldable()) {
                throw new IllegalArgumentException("Input crease pattern is not locally flat foldable.");
            }

            var folder = new FolderFactory().create(origamiModel.getModelType());
            var foldedModel = any
                    ? folder.fold(origamiModel, pointEps, Folder.EstimationType.FIRST_ONLY).foldedModel()
                    : folder.fold(origamiModel, pointEps, Folder.EstimationType.FULL).foldedModel();

            if (split) {
                var digitLength = Integer.toString(foldedModel.getFoldablePatternCount()).length();
                for (int i = 0; i < foldedModel.getFoldablePatternCount(); i++) {
                    var paddedNumber = "0".repeat(digitLength - Integer.toString(i).length()) + i;
                    var outputName = outputFilePath.replaceFirst("[.]fold$", "." + paddedNumber + ".fold");

                    var foldedModelExporter = new FoldedModelSingleExporterFOLD();
                    foldedModelExporter.export(
                            new FoldedModelEntity(foldedModel, i), outputName, null);
                }
            } else {
                var foldedModelExporter = new FoldedModelAllExporterFOLD();
                foldedModelExporter.export(
                        new FoldedModelEntity(foldedModel), outputFilePath, null);
            }

        } catch (Exception e) {
            logger.error("folding error", e);
        }
    }
}
