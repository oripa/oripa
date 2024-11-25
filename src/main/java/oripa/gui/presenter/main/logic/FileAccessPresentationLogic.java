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
package oripa.gui.presenter.main.logic;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.application.FileAccessService;
import oripa.application.main.PaintContextModification;
import oripa.gui.presenter.main.PainterScreenPresenter;
import oripa.gui.view.main.MainFrameView;
import oripa.gui.view.main.PainterScreenSetting;
import oripa.gui.view.util.ChildFrameManager;
import oripa.gui.view.util.ColorUtil;
import oripa.persistence.dao.DataAccessException;
import oripa.persistence.dao.FileType;
import oripa.persistence.doc.Doc;
import oripa.project.Project;

/**
 * @author OUCHI Koji
 *
 */
public class FileAccessPresentationLogic {
	private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final MainFrameView view;

	private final PainterScreenPresenter screenPresenter;

	private final PainterScreenSetting screenSetting;

	private final ChildFrameManager childFrameManager;

	private final PaintContextModification paintContextModification;

	private final FileAccessService<Doc> dataFileAccess;

	private final Project project;

	public FileAccessPresentationLogic(
			final MainFrameView view,
			final ChildFrameManager childFrameManager,
			final PainterScreenPresenter screenPresenter,
			final PainterScreenSetting screenSetting,
			final PaintContextModification paintContextModification,
			final Project project,
			final FileAccessService<Doc> dataFileAccess) {
		this.view = view;
		this.childFrameManager = childFrameManager;

		this.screenPresenter = screenPresenter;
		this.screenSetting = screenSetting;

		this.paintContextModification = paintContextModification;

		this.project = project;

		this.dataFileAccess = dataFileAccess;

	}

	/**
	 * Saves file. Shows dialog when error.
	 *
	 * @param path
	 * @param type
	 * @return
	 * @throws DataAccessException
	 * @throws IllegalArgumentException
	 */
	public String saveFile(final String path, final FileType<Doc> type)
			throws DataAccessException, IllegalArgumentException {
		try {
			var doc = Doc.forSaving(paintContextModification.getCreasePattern(), project.getProperty());
			dataFileAccess.saveFile(doc, path, type);

		} catch (DataAccessException | IllegalArgumentException e) {
			logger.error("Failed to save", e);
			view.showSaveFailureErrorMessage(e);
			throw e;
		}

		return path;

	}

	/**
	 * This method tries to read data from the path.
	 *
	 * @param filePath
	 * @return file path for loaded file. {@code null} if loading is not done.
	 */
	public String loadFile(final String filePath) {

		childFrameManager.closeAll(view);

		try {

			var docOpt = dataFileAccess.loadFile(filePath);
			return docOpt
					.map(doc -> {
						project.setProperty(doc.getProperty());
						project.setDataFilePath(filePath);

						var property = project.getProperty();
						view.setEstimationResultColors(
								ColorUtil.convertCodeToColor(property.extractFrontColorCode()),
								ColorUtil.convertCodeToColor(property.extractBackColorCode()));

						screenSetting.setGridVisible(false);
						paintContextModification
								.setCreasePatternToPaintContext(
										doc.getCreasePattern());
						screenPresenter.updateCameraCenter();
						return filePath;
					}).orElse(null);
		} catch (DataAccessException | IllegalArgumentException e) {
			logger.error("failed to load", e);
			view.showLoadFailureErrorMessage(e);
			return project.getDataFilePath();
		}
	}

	public void importFile(final String path) {

		var docOpt = dataFileAccess.loadFile(path);
		docOpt.ifPresent(
				doc -> paintContextModification.setToImportedLines(doc.getCreasePattern()));

	}

}
