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
package oripa.gui.presenter.estimation.logic;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;
import oripa.application.estimation.FoldedModelFileAccessServiceFactory;
import oripa.gui.presenter.file.UserAction;
import oripa.gui.view.FrameView;
import oripa.gui.view.estimation.DefaultColors;
import oripa.gui.view.estimation.EstimationResultUIView;
import oripa.gui.view.util.ColorUtil;
import oripa.persistence.entity.FoldedModelEntity;
import oripa.persistence.entity.FoldedModelFileTypes;
import oripa.persistence.entity.exporter.FoldedModelPictureConfig;
import oripa.persistence.entity.exporter.FoldedModelSVGConfig;

/**
 * @author OUCHI Koji
 *
 */
public class EstimationResultFilePresentationLogic {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final FoldedModelFileSelectionPresenterFactory fileSelectionPresenterFactory;

    private final FoldedModelFileAccessServiceFactory fileAccessServiceFactory;

    @Inject
    public EstimationResultFilePresentationLogic(
            final FoldedModelFileSelectionPresenterFactory fileSelectionPresenterFactory,
            final FoldedModelFileAccessServiceFactory fileAccessFactory) {

        this.fileSelectionPresenterFactory = fileSelectionPresenterFactory;

        this.fileAccessServiceFactory = fileAccessFactory;
    }

    /**
     * open export dialog for current folded estimation
     */
    public String export(
            final EstimationResultUIView view,
            final String lastFilePath) {
        try {
            var fileAccessService = fileAccessServiceFactory.create(view.isFaceOrderFlipped());

            fileAccessService.setConfigToSavingAction(
                    FoldedModelFileTypes.svg(), () -> createSVGConfig(view));
            fileAccessService.setConfigToSavingAction(
                    FoldedModelFileTypes.flippedSvg(), () -> createSVGConfig(view));

            fileAccessService.setConfigToSavingAction(
                    FoldedModelFileTypes.picture(), () -> createPictureConfig(view));

            var foldedModel = view.getModel();

            var entity = new FoldedModelEntity(foldedModel, view.getOverlapRelationIndex());

            var presenter = fileSelectionPresenterFactory.create(
                    (FrameView) view.getTopLevelView(), fileAccessService.getFileSelectionService());

            var selection = presenter.saveUsingGUI(lastFilePath);

            if (selection.action() == UserAction.CANCELED) {
                return lastFilePath;
            }

            fileAccessService.saveFile(entity, selection.path(), selection.type());

            return selection.path();

//			lastFilePath = selection.path();
//
//			lastFilePathChangeListener.accept(lastFilePath);

        } catch (Exception ex) {
            logger.error("error: ", ex);
            view.showExportErrorMessage(ex);

            return lastFilePath;
        }
    }

    public FoldedModelSVGConfig createSVGConfig(final EstimationResultUIView view) {
        var svgConfig = new FoldedModelSVGConfig();

        svgConfig.setFaceStrokeWidth(view.getSVGFaceStrokeWidth());
        svgConfig.setPrecreaseStrokeWidth(view.getSVGPrecreaseStrokeWidth());

        svgConfig.setFrontFillColorCode(ColorUtil.convertColorToCode(view.getFrontColor()));
        svgConfig.setBackFillColorCode(ColorUtil.convertColorToCode(view.getBackColor()));

        return svgConfig;
    }

    private FoldedModelPictureConfig createPictureConfig(final EstimationResultUIView view) {
        var pictureConfig = new FoldedModelPictureConfig();

        pictureConfig.setAmbientOcclusion(view.isFaceShade());
        pictureConfig.setFillFaces(view.isFillFace());
        pictureConfig.setDrawEdges(view.isDrawEdges());
        pictureConfig.setFaceOrderFlipped(view.isFaceOrderFlipped());

        pictureConfig.setColors(view.isUseColor() ? view.getFrontColor() : DefaultColors.WHITE,
                view.isUseColor() ? view.getBackColor() : DefaultColors.WHITE);

        pictureConfig.setDistortionMethod(view.getDistortionMethod());
        pictureConfig.setDistortionParameter(view.getDistortionParameter());
        pictureConfig.setVertexDepths(view.getVertexDepths());
        pictureConfig.setEps(view.getEps());

        pictureConfig.setRotateAngle(view.getRotateAngle());

        return pictureConfig;
    }

}
