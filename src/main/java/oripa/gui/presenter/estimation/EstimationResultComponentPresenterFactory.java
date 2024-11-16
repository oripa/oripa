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
package oripa.gui.presenter.estimation;

import java.util.function.Consumer;

import oripa.application.estimation.FoldedModelFileAccessServiceFactory;
import oripa.gui.view.estimation.EstimationResultUIView;
import oripa.gui.view.file.FileChooserFactory;

/**
 * @author OUCHI Koji
 *
 */
public class EstimationResultComponentPresenterFactory {
	private final FileChooserFactory fileChooserFactory;
	private final FoldedModelFileSelectionPresenterFactory fileSelectionPresenterFactory;

	private final FoldedModelFileAccessServiceFactory fileAccessServiceFactory;

	public EstimationResultComponentPresenterFactory(
			final FileChooserFactory fileChooserFactory,
			final FoldedModelFileSelectionPresenterFactory fileSelectionPresenterFactory,
			final FoldedModelFileAccessServiceFactory fileAccessServiceFactory) {
		this.fileChooserFactory = fileChooserFactory;
		this.fileSelectionPresenterFactory = fileSelectionPresenterFactory;
		this.fileAccessServiceFactory = fileAccessServiceFactory;
	}

	public EstimationResultUIPresenter createEstimationResultUIPresenter(
			final EstimationResultUIView view,
			final String lastFilePath,
			final Consumer<String> lastFilePathChangeListener) {
		return new EstimationResultUIPresenter(
				view,
				fileChooserFactory,
				fileSelectionPresenterFactory,
				fileAccessServiceFactory,
				lastFilePath,
				lastFilePathChangeListener);

	}
}
