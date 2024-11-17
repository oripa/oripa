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

import java.awt.Color;
import java.io.File;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.application.FileAccessService;
import oripa.application.main.IniFileAccess;
import oripa.application.main.PaintContextModification;
import oripa.domain.cutmodel.CutModelOutlinesHolder;
import oripa.domain.paint.PaintContext;
import oripa.domain.paint.PaintDomainContext;
import oripa.file.FileHistory;
import oripa.gui.bind.state.BindingObjectFactoryFacade;
import oripa.gui.presenter.creasepattern.CreasePatternPresentationContext;
import oripa.gui.presenter.creasepattern.CreasePatternViewContext;
import oripa.gui.presenter.file.UserAction;
import oripa.gui.view.ViewScreenUpdater;
import oripa.gui.view.main.MainFrameDialogFactory;
import oripa.gui.view.main.MainFrameView;
import oripa.gui.view.main.MainViewSetting;
import oripa.gui.view.main.PainterScreenSetting;
import oripa.gui.view.main.SubFrameFactory;
import oripa.gui.view.main.ViewUpdateSupport;
import oripa.gui.view.util.ChildFrameManager;
import oripa.persistence.dao.DataAccessException;
import oripa.persistence.dao.FileType;
import oripa.persistence.doc.Doc;
import oripa.persistence.doc.exporter.CreasePatternFOLDConfig;
import oripa.project.Project;
import oripa.resource.ResourceHolder;
import oripa.resource.ResourceKey;
import oripa.resource.StringID;
import oripa.util.file.FileFactory;

/**
 * @author OUCHI Koji
 *
 */
public class MainFramePresentationLogic {
	private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final MainFrameView view;

	private final MainComponentPresenterFactory componentPresenterFactory;
	private final PainterScreenPresenter screenPresenter;
	private final UIPanelPresenter uiPanelPresenter;

	private final CreasePatternViewContext viewContext;

	private final ViewScreenUpdater screenUpdater;
	private final PainterScreenSetting screenSetting;

	private final ChildFrameManager childFrameManager;

	private final Project project;

	private final PaintContext paintContext;
	private final CutModelOutlinesHolder cutModelOutlinesHolder;

	private final IniFileAccess iniFileAccess;
	private final FileAccessService<Doc> dataFileAccess;
	private final FileHistory fileHistory;
	private final FileFactory fileFactory;

	// services
	private final PaintContextModification paintContextModification;

	private final ResourceHolder resourceHolder;

	public MainFramePresentationLogic(
			final MainFrameView view,
			final MainViewSetting viewSetting,
			final ViewUpdateSupport viewUpdateSupport,
			final MainFrameDialogFactory dialogFactory,
			final SubFrameFactory subFrameFactory,
			final PainterScreenPresenter screenPresenter,
			final UIPanelPresenter uiPanelPresenter,
			final MainComponentPresenterFactory componentPresenterFactory,
			final CreasePatternPresentationContext presentationContext,
			final ChildFrameManager childFrameManager,
			final BindingObjectFactoryFacade bindingFactory,
			final Project project,
			final PaintDomainContext domainContext,
			final PaintContextModification paintContextModification,
			final CutModelOutlinesHolder cutModelOutlinesHolder,
			final FileHistory fileHistory,
			final IniFileAccess iniFileAccess,
			final FileAccessService<Doc> dataFileAccess,
			final FileFactory fileFactory,
			final Supplier<CreasePatternFOLDConfig> foldConfigFactory,
			final ResourceHolder resourceHolder) {

		this.view = view;

		this.childFrameManager = childFrameManager;

		this.componentPresenterFactory = componentPresenterFactory;

		this.viewContext = presentationContext.getViewContext();

		this.project = project;
		this.paintContext = domainContext.getPaintContext();
		this.paintContextModification = paintContextModification;
		this.cutModelOutlinesHolder = cutModelOutlinesHolder;

		this.fileHistory = fileHistory;
		this.iniFileAccess = iniFileAccess;
		this.dataFileAccess = dataFileAccess;
		this.fileFactory = fileFactory;

		this.screenSetting = viewSetting.getPainterScreenSetting();
		this.screenUpdater = viewUpdateSupport.getViewScreenUpdater();

		this.screenPresenter = screenPresenter;

		this.uiPanelPresenter = uiPanelPresenter;

		this.resourceHolder = resourceHolder;
	}

	public void updateMRUFilesMenuItem(final int index) {
		var histories = fileHistory.getHistory();
		if (index < histories.size()) {
			view.setMRUFilesMenuItem(index, histories.get(index));
		} else {
			view.setMRUFilesMenuItem(index, "");
		}
	}

	public void exit(final Runnable doExit) {
		saveIniFile();
		doExit.run();
	}

	private void saveIniFile() {
		try {
			iniFileAccess.save(fileHistory, viewContext);
		} catch (IllegalStateException e) {
			logger.error("error when building ini file data", e);
			view.showSaveIniFileFailureErrorMessage(e);
		}
	}

	public void clear() {
		paintContextModification.clear(paintContext, cutModelOutlinesHolder);
		project.clear();

		screenSetting.setGridVisible(true);

		childFrameManager.closeAll(view);

		screenUpdater.updateScreen();
		updateTitleText();
	}

	private void updateTitleText() {

		view.setFileNameToTitle(getTitleText());
	}

	private String getTitleText() {
		var defaultFileName = resourceHolder.getString(ResourceKey.DEFAULT, StringID.Default.FILE_NAME_ID);
		var fileName = project.getDataFileName().orElse(defaultFileName);

		return fileName.isEmpty() ? defaultFileName : fileName;

	}

	/**
	 * Update file menu. Do nothing if the given {@code filePath} is null or
	 * wrong.
	 *
	 * @param filePath
	 */
	private void updateMenu() {
		var filePath = project.getDataFilePath();

		if (!project.isProjectFile()) {
			logger.debug("updating menu is canceled: {}", filePath);
			return;
		}

		fileHistory.useFile(filePath);

		view.buildFileMenu();
	}

	/**
	 * save file without origami model check
	 */
	public String saveFileUsingGUIImpl(@SuppressWarnings("unchecked") final FileType<Doc>... types) {
		var directory = fileHistory.getLastDirectory();
		var fileName = project.getDataFileName().get();

		try {

			var presenter = componentPresenterFactory.createDocFileSelectionPresenter(
					view, dataFileAccess.getFileSelectionService());

			File givenFile = fileFactory.create(
					directory,
					(fileName.isEmpty()) ? "newFile.opx" : fileName);

			var filePath = givenFile.getPath();

			var selection = (types == null || types.length == 0) ? presenter.saveUsingGUI(filePath)
					: presenter.saveUsingGUI(filePath, List.of(types));

			if (selection.action() == UserAction.CANCELED) {
				return project.getDataFilePath();
			}

			var path = selection.path();

			var doc = Doc.forSaving(paintContext.getCreasePattern(), project.getProperty());
			dataFileAccess.saveFile(doc, selection.path(), selection.type());

			return path;

		} catch (IllegalArgumentException | DataAccessException e) {
			// ignore
			return project.getDataFilePath();
		}
	}

	/**
	 * Call this method when save is done.
	 *
	 * @param data
	 * @param path
	 */
	public void afterSaveFile(final String path) {
		paintContext.creasePatternUndo().clearChanged();

		if (Project.projectFileTypeMatch(path)) {
			project.setDataFilePath(path);
		}

		updateMenu();
		updateTitleText();
	}

	/**
	 * This method tries to read data from the path.
	 *
	 * @param filePath
	 * @return file path for loaded file. {@code null} if loading is not done.
	 */
	public String loadFileImpl(final String filePath) {

		childFrameManager.closeAll(view);

		try {

			var DocOpt = dataFileAccess.loadFile(filePath);
			return DocOpt
					.map(doc -> {
						project.setProperty(doc.getProperty());
						project.setDataFilePath(filePath);

						var property = project.getProperty();
						view.setEstimationResultColors(
								convertCodeToColor(property.extractFrontColorCode()),
								convertCodeToColor(property.extractBackColorCode()));

						screenSetting.setGridVisible(false);
						paintContextModification
								.setCreasePatternToPaintContext(
										doc.getCreasePattern(), paintContext, cutModelOutlinesHolder);
						screenPresenter.updateCameraCenter();
						return filePath;
					}).orElse(null);
		} catch (DataAccessException | IllegalArgumentException e) {
			logger.error("failed to load", e);
			view.showLoadFailureErrorMessage(e);
			return project.getDataFilePath();
		}
	}

	/**
	 * Update UI.
	 *
	 * @param filePath
	 */
	public void afterLoadFile() {
		screenUpdater.updateScreen();
		updateMenu();
		updateTitleText();
		uiPanelPresenter.updateValuePanelFractionDigits();
	}

	/**
	 * Can return null because the returned value will be passed to other
	 * method.
	 *
	 * @param code
	 * @return
	 */
	private Color convertCodeToColor(final String code) {
		if (code == null) {
			return null;
		}

		try {
			return new Color(Integer.decode(code));
		} catch (NumberFormatException e) {
			return null;
		}
	}

}
