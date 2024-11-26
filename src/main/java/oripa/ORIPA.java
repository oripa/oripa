/**
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2005-2009 Jun Mitani http://mitani.cs.tsukuba.ac.jp/

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

package oripa;

import javax.swing.SwingUtilities;

import com.google.inject.Guice;

import oripa.application.main.DocFileAccess;
import oripa.application.main.IniFileAccess;
import oripa.application.main.PaintContextService;
import oripa.cli.CommandLineInterfaceMain;
import oripa.domain.paint.PaintContext;
import oripa.domain.paint.byvalue.ByValueContext;
import oripa.file.FileHistory;
import oripa.gui.bind.state.BindingObjectFactoryFacade;
import oripa.gui.presenter.creasepattern.CreasePatternViewContext;
import oripa.gui.presenter.creasepattern.MouseActionHolder;
import oripa.gui.presenter.creasepattern.TypeForChangeContext;
import oripa.gui.presenter.main.MainDialogPresenterFactory;
import oripa.gui.presenter.main.MainFramePresenter;
import oripa.gui.presenter.main.PainterScreenPresenter;
import oripa.gui.presenter.main.UIPanelPresenter;
import oripa.gui.presenter.main.logic.*;
import oripa.gui.view.ViewScreenUpdater;
import oripa.gui.view.main.KeyProcessing;
import oripa.gui.view.main.MainFrameSetting;
import oripa.gui.view.main.MainFrameView;
import oripa.gui.view.main.PainterScreenSetting;
import oripa.gui.view.main.SubFrameFactory;
import oripa.gui.view.main.UIPanelSetting;
import oripa.gui.view.util.ChildFrameManager;
import oripa.inject.BindingModule;
import oripa.inject.CreasePatternPresenterModule;
import oripa.inject.FileAccessServiceModule;
import oripa.inject.FileHistoryModule;
import oripa.inject.MainViewOripaModule;
import oripa.inject.MainViewSwingModule;
import oripa.inject.PaintDomainModule;
import oripa.project.Project;
import oripa.resource.ResourceHolder;
import oripa.util.file.FileFactory;

public class ORIPA {
	public static void main(final String[] args) {

		if (args.length > 0) {
			new CommandLineInterfaceMain().run(args);
			return;
		}

		SwingUtilities.invokeLater(() -> {
			// Construction of the main frame

			var injector = Guice.createInjector(
//					new PluginModule(),
					new MainViewSwingModule(),
					new MainViewOripaModule(),
					new BindingModule(),
					new CreasePatternPresenterModule(),
					new FileAccessServiceModule(),
					new PaintDomainModule(),
					new FileHistoryModule());

			// tentative variables. To be deleted.
			var mouseActionHolder = injector.getInstance(MouseActionHolder.class);
			var mainScreenUpdater = injector.getInstance(ViewScreenUpdater.class);
			var keyProcessing = injector.getInstance(KeyProcessing.class);
			var mainFrameSetting = injector.getInstance(MainFrameSetting.class);
			var uiPanelSetting = injector.getInstance(UIPanelSetting.class);

			var mainFrame = injector.getInstance(MainFrameView.class);
			mainFrame.initializeFrameBounds();

			var mainScreenSetting = injector.getInstance(PainterScreenSetting.class);

			// Construct the presenter

			var paintContext = injector.getInstance(PaintContext.class);
			var byValueContext = injector.getInstance(ByValueContext.class);

			var creasePatternViewContext = injector.getInstance(CreasePatternViewContext.class);
			var typeForChangeContext = injector.getInstance(TypeForChangeContext.class);

			var childFrameManager = new ChildFrameManager();

			var pluginLoader = new PluginLoader();

			// injection is configured but apply later
			var plugins = pluginLoader.loadMouseActionPlugins(
					mainFrameSetting,
					uiPanelSetting);

			var bindingFactory = injector.getInstance(BindingObjectFactoryFacade.class);

			var fileFactory = injector.getInstance(FileFactory.class);

			var subFrameFactory = injector.getInstance(SubFrameFactory.class);

			var subFramePresenterFactory = injector.getInstance(
					SubFramePresenterFactory.class);

			var docFileAccess = injector.getInstance(DocFileAccess.class);

			var modelComputationFacadeFactory = injector.getInstance(
					ModelComputationFacadeFactory.class);
			var modelIndexChangeListenerPutter = new ModelIndexChangeListenerPutter();

			var mainDialogPresenterFactory = injector.getInstance(
					MainDialogPresenterFactory.class);

			var fileHistory = injector.getInstance(FileHistory.class);
			var iniFileAccess = injector.getInstance(IniFileAccess.class);

			var project = new Project();

			var paintContextService = injector.getInstance(
					PaintContextService.class);

			var resourceHolder = injector.getInstance(ResourceHolder.class);

			var screenPresenter = injector.getInstance(
					PainterScreenPresenter.class);

			var uiPanelView = mainFrame.getUIPanelView();

			var gridDivNumPresentationLogic = injector.getInstance(
					GridDivNumPresentationLogic.class);

			var paintMenuListenerRegistration = new UIPanelPaintMenuListenerRegistration(
					uiPanelView,
					bindingFactory,
					keyProcessing,
					paintContext,
					typeForChangeContext,
					byValueContext);

			var subFramePresentationLogic = new SubFramePresentationLogic(
					uiPanelView,
					subFrameFactory,
					subFramePresenterFactory,
					modelIndexChangeListenerPutter,
					modelComputationFacadeFactory,
					paintContext);

			var valuePanelPresentationLogic = new ValuePanelPresentationLogic(uiPanelView, paintContext);

			var uiPanelPresenter = new UIPanelPresenter(
					uiPanelView,
					subFramePresentationLogic,
					paintMenuListenerRegistration,
					gridDivNumPresentationLogic,
					valuePanelPresentationLogic,
					typeForChangeContext,
					mainScreenSetting);

			var fileAccessPresentationLogic = new FileAccessPresentationLogic(
					mainFrame,
					childFrameManager,
					screenPresenter,
					mainScreenSetting,
					paintContextService,
					project,
					docFileAccess);

			var iniFileAccessPresentationLogic = new IniFileAccessPresentationLogic(
					mainFrame,
					mainScreenSetting,
					creasePatternViewContext,
					iniFileAccess,
					fileHistory);

			var clearActionPresentationLogic = new ClearActionPresentationLogic(
					mainFrame,
					mainScreenUpdater,
					mainScreenSetting,
					childFrameManager,
					project,
					paintContextService);

			var undoRedoPresentationLogic = new UndoRedoPresentationLogic(
					mainScreenUpdater,
					mouseActionHolder,
					paintContext);

			var mainFrameFilePresentationLogic = new MainFrameFilePresentationLogic(
					mainFrame,
					mainDialogPresenterFactory,
					fileAccessPresentationLogic,
					project,
					docFileAccess,
					fileHistory,
					fileFactory);

			var presentationLogic = new MainFramePresentationLogic(
					mainFrame,
					screenPresenter,
					uiPanelPresenter,
					mainFrameFilePresentationLogic,
					clearActionPresentationLogic,
					undoRedoPresentationLogic,
					iniFileAccessPresentationLogic,
					project,
					fileHistory,
					resourceHolder);

			var paintMenuListenerFactory = new MainFramePaintMenuListenerFactory(
					paintContext,
					mouseActionHolder,
					bindingFactory);

			var presenter = new MainFramePresenter(
					mainFrame,
					presentationLogic,
					mainDialogPresenterFactory,
					paintMenuListenerFactory,
					project,
					paintContextService,
					plugins);
			presenter.setViewVisible(true);

//			if (Config.FOR_STUDY) {
//				int modelFrameWidth = 400;
//				int modelFrameHeight = 400;
//				modelFrame3D = new ModelViewFrame3D();
//				modelFrame3D.setBounds(0, 0,
//						modelFrameWidth * 2, modelFrameHeight * 2);
//				modelFrame3D.setVisible(true);
//			}
		});
	}

}
