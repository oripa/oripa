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
package oripa.gui.presenter.main;

import jakarta.inject.Inject;
import oripa.application.FileSelectionService;
import oripa.domain.paint.PaintContext;
import oripa.domain.projectprop.PropertyHolder;
import oripa.gui.view.FrameView;
import oripa.gui.view.ViewScreenUpdater;
import oripa.gui.view.main.MainFrameDialogFactory;
import oripa.persistence.doc.Doc;

/**
 * @author OUCHI Koji
 *
 */
public class MainDialogPresenterFactory {
    private final MainFrameDialogFactory dialogFactory;

    private final DocFileSelectionPresenterFactory fileSelectionPresenterFactory;

    private final ViewScreenUpdater viewScreenUpdater;
    private final PaintContext paintContext;

    @Inject
    public MainDialogPresenterFactory(
            final ViewScreenUpdater viewScreenUpdater,
            final MainFrameDialogFactory dialogFactory,
            final DocFileSelectionPresenterFactory fileSelectionPresenterFactory,
            final PaintContext paintContext) {

        this.viewScreenUpdater = viewScreenUpdater;
        this.dialogFactory = dialogFactory;

        this.fileSelectionPresenterFactory = fileSelectionPresenterFactory;

        this.paintContext = paintContext;
    }

    public ArrayCopyDialogPresenter createArrayCopyDialogPresenter(
            final FrameView parent) {
        var dialog = dialogFactory.createArrayCopyDialog(parent);

        return new ArrayCopyDialogPresenter(
                dialog,
                paintContext,
                viewScreenUpdater);
    }

    public CircleCopyDialogPresenter createCircleCopyDialogPresenter(
            final FrameView parent) {
        var dialog = dialogFactory.createCircleCopyDialog(parent);

        return new CircleCopyDialogPresenter(
                dialog,
                paintContext,
                viewScreenUpdater);
    }

    public PropertyDialogPresenter createPropertyDialogPresenter(
            final FrameView parent,
            final PropertyHolder propertyHolder) {
        var dialog = dialogFactory.createPropertyDialog(parent);

        return new PropertyDialogPresenter(dialog, propertyHolder);
    }

    public DocFileSelectionPresenter createDocFileSelectionPresenter(
            final FrameView parent, final FileSelectionService<Doc> fileSelectionService) {
        return fileSelectionPresenterFactory.create(
                parent,
                fileSelectionService);
    }
}
