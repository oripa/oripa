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

import oripa.application.FileSelectionService;
import oripa.application.main.FileModelCheckService;
import oripa.domain.paint.PaintContext;
import oripa.domain.projectprop.PropertyHolder;
import oripa.gui.view.FrameView;
import oripa.gui.view.file.FileChooserFactory;
import oripa.gui.view.main.ArrayCopyDialogView;
import oripa.gui.view.main.CircleCopyDialogView;
import oripa.gui.view.main.PropertyDialogView;
import oripa.gui.view.main.ViewUpdateSupport;
import oripa.persistence.doc.Doc;
import oripa.util.file.ExtensionCorrector;
import oripa.util.file.FileFactory;

/**
 * @author OUCHI Koji
 *
 */
public class MainDialogPresenterFactory {
	private final FileChooserFactory fileChooserFactory;
	private final ViewUpdateSupport viewUpdateSupport;
	private final PaintContext paintContext;
	private final FileModelCheckService fileModelCheckService;
	private final FileFactory fileFactory;
	private final ExtensionCorrector extensionCorrector;

	public MainDialogPresenterFactory(
			final FileChooserFactory fileChooserFactory,
			final ViewUpdateSupport viewUpdateSupport,
			final PaintContext paintContext,
			final FileModelCheckService fileModelCheckService,
			final FileFactory fileFactory,
			final ExtensionCorrector extensionCorrector) {

		this.fileChooserFactory = fileChooserFactory;
		this.viewUpdateSupport = viewUpdateSupport;

		this.paintContext = paintContext;

		this.fileModelCheckService = fileModelCheckService;
		this.fileFactory = fileFactory;

		this.extensionCorrector = extensionCorrector;

	}

	public ArrayCopyDialogPresenter createArrayCopyDialogPresenter(
			final ArrayCopyDialogView view) {
		return new ArrayCopyDialogPresenter(
				view,
				paintContext,
				viewUpdateSupport.getViewScreenUpdater());
	}

	public CircleCopyDialogPresenter createCircleCopyDialogPresenter(
			final CircleCopyDialogView view) {
		return new CircleCopyDialogPresenter(
				view,
				paintContext,
				viewUpdateSupport.getViewScreenUpdater());
	}

	public PropertyDialogPresenter createPropertyDialogPresenter(
			final PropertyDialogView view,
			final PropertyHolder propertyHolder) {
		return new PropertyDialogPresenter(view, propertyHolder);
	}

	public DocFileSelectionPresenter createDocFileSelectionPresenter(
			final FrameView parent, final FileSelectionService<Doc> fileSelectionService) {
		return new DocFileSelectionPresenter(
				parent,
				fileChooserFactory,
				fileModelCheckService,
				fileFactory,
				fileSelectionService,
				extensionCorrector);
	}
}
