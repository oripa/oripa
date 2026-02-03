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
package oripa.gui.presenter.model;

import java.util.List;

import jakarta.inject.Inject;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.gui.presenter.creasepattern.ScreenUpdater;
import oripa.gui.presenter.model.logic.ModelViewFilePresentationLogic;
import oripa.gui.view.main.PainterScreenSetting;
import oripa.gui.view.model.ModelViewFrameView;

/**
 * @author OUCHI Koji
 *
 */
public class ModelViewFramePresenterFactory {

    private final ScreenUpdater mainScreenUpdater;

    private final ModelViewComponentPresenterFactory modelViewComponentPresenterFactory;

    private final ModelViewFilePresentationLogic filePresentationLogic;

    private final PainterScreenSetting mainScreenSetting;

    @Inject
    public ModelViewFramePresenterFactory(
            final ScreenUpdater mainScreenUpdater,
            final PainterScreenSetting mainScreenSetting,
            final ModelViewComponentPresenterFactory modelViewComponentPresenterFactory,
            final ModelViewFilePresentationLogic filePresentationLogic) {
        this.mainScreenUpdater = mainScreenUpdater;

        this.mainScreenSetting = mainScreenSetting;

        this.modelViewComponentPresenterFactory = modelViewComponentPresenterFactory;
        this.filePresentationLogic = filePresentationLogic;

    }

    public ModelViewFramePresenter create(
            final ModelViewFrameView view,
            final List<OrigamiModel> origamiModels,
            final double eps) {
        var screen = modelViewComponentPresenterFactory.createScreenPresenter(
                view.getModelScreenView(),
                mainScreenUpdater::updateScreen,
                eps);

        return new ModelViewFramePresenter(
                view,
                filePresentationLogic,
                mainScreenSetting,
                origamiModels,
                eps);
    }

}
