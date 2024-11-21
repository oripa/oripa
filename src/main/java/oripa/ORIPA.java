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

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.function.Supplier;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import oripa.application.FileAccessService;
import oripa.application.estimation.FoldedModelFileAccessServiceFactory;
import oripa.application.main.IniFileAccess;
import oripa.application.main.PaintContextModification;
import oripa.appstate.StatePopperFactory;
import oripa.cli.CommandLineInterfaceMain;
import oripa.domain.cutmodel.DefaultCutModelOutlinesHolder;
import oripa.domain.fold.FolderFactory;
import oripa.domain.fold.TestedOrigamiModelFactory;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.domain.paint.PaintContextFactory;
import oripa.domain.paint.PaintDomainContext;
import oripa.domain.paint.byvalue.ByValueContextImpl;
import oripa.domain.paint.copypaste.SelectionOriginHolderImpl;
import oripa.file.FileHistory;
import oripa.file.InitDataFileReader;
import oripa.file.InitDataFileWriter;
import oripa.gui.bind.state.BindingObjectFactoryFacade;
import oripa.gui.bind.state.EditModeStateManager;
import oripa.gui.bind.state.PaintBoundStateFactory;
import oripa.gui.bind.state.PluginPaintBoundStateFactory;
import oripa.gui.presenter.creasepattern.ComplexActionFactory;
import oripa.gui.presenter.creasepattern.CreasePatternPresentationContext;
import oripa.gui.presenter.creasepattern.CreasePatternViewContextFactory;
import oripa.gui.presenter.creasepattern.EditOutlineActionFactory;
import oripa.gui.presenter.creasepattern.MouseActionHolder;
import oripa.gui.presenter.creasepattern.MouseActionSetterFactory;
import oripa.gui.presenter.creasepattern.TypeForChangeContext;
import oripa.gui.presenter.creasepattern.copypaste.CopyAndPasteActionFactory;
import oripa.gui.presenter.estimation.FoldedModelFileSelectionPresenterFactory;
import oripa.gui.presenter.main.FileAccessPresentationLogic;
import oripa.gui.presenter.main.MainComponentPresenterFactory;
import oripa.gui.presenter.main.MainFramePresentationLogic;
import oripa.gui.presenter.main.MainFramePresenter;
import oripa.gui.presenter.main.ModelComputationFacadeFactory;
import oripa.gui.presenter.main.ModelIndexChangeListenerPutter;
import oripa.gui.presenter.main.SubFramePresenterFactory;
import oripa.gui.presenter.main.SwitcherBetweenPasteAndChangeOrigin;
import oripa.gui.presenter.model.ModelViewComponentPresenterFactory;
import oripa.gui.presenter.model.OrigamiModelFileSelectionPresenterFactory;
import oripa.gui.view.ViewScreenUpdaterFactory;
import oripa.gui.view.main.MainViewSetting;
import oripa.gui.view.main.ViewUpdateSupport;
import oripa.gui.view.util.ChildFrameManager;
import oripa.gui.viewsetting.main.KeyProcessingImpl;
import oripa.gui.viewsetting.main.MainFrameSettingImpl;
import oripa.gui.viewsetting.main.PainterScreenSettingImpl;
import oripa.gui.viewsetting.main.UIPanelSettingImpl;
import oripa.persistence.dao.FileDAO;
import oripa.persistence.doc.Doc;
import oripa.persistence.doc.DocFileSelectionSupportSelectorFactory;
import oripa.persistence.doc.exporter.CreasePatternFOLDConfig;
import oripa.persistence.entity.FoldedModelFileSelectionSupportSelectorFactory;
import oripa.persistence.entity.OrigamiModelFileSelectionSupportSelectorFactory;
import oripa.project.Project;
import oripa.resource.Constants;
import oripa.resource.ResourceHolder;
import oripa.swing.view.estimation.EstimationResultSwingFrameFactory;
import oripa.swing.view.file.FileChooserSwingFactory;
import oripa.swing.view.foldability.FoldabilityCheckSwingFrameFactory;
import oripa.swing.view.main.ArrayCopyDialogFactory;
import oripa.swing.view.main.CircleCopyDialogFactory;
import oripa.swing.view.main.MainFrame;
import oripa.swing.view.main.MainFrameSwingDialogFactory;
import oripa.swing.view.main.PropertyDialogFactory;
import oripa.swing.view.main.SubSwingFrameFactory;
import oripa.swing.view.model.ModelViewSwingFrameFactory;
import oripa.util.file.ExtensionCorrector;
import oripa.util.file.FileFactory;

public class ORIPA {
	public static void main(final String[] args) {

		if (args.length > 0) {
			new CommandLineInterfaceMain().run(args);
			return;
		}

		SwingUtilities.invokeLater(() -> {
			int uiPanelWidth = 0;// 150;

			int mainFrameWidth = 1000;
			int mainFrameHeight = 800;

			int appTotalWidth = mainFrameWidth + uiPanelWidth;
			int appTotalHeight = mainFrameHeight;

			// Construction of the main frame

			var screenUpdaterFactory = new ViewScreenUpdaterFactory();

			var screenUpdater = screenUpdaterFactory.create();
			var mouseActionHolder = new MouseActionHolder();
			var keyProcessing = new KeyProcessingImpl(
					new SwitcherBetweenPasteAndChangeOrigin(mouseActionHolder),
					screenUpdater);
			var viewUpdateSupport = new ViewUpdateSupport(screenUpdater, keyProcessing);

			var mainViewSetting = new MainViewSetting(
					new MainFrameSettingImpl(),
					new PainterScreenSettingImpl(),
					new UIPanelSettingImpl());
			var mainFrame = new MainFrame(mainViewSetting, viewUpdateSupport);
			mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			// Configure position and size of the frame

			Toolkit toolkit = mainFrame.getToolkit();
			Dimension dim = toolkit.getScreenSize();
			int originX = (int) (dim.getWidth() / 2 - appTotalWidth / 2);
			int originY = (int) (dim.getHeight() / 2 - appTotalHeight / 2);

			mainFrame.setBounds(originX + uiPanelWidth, originY, mainFrameWidth, mainFrameHeight);

			// Construct the presenter

			var domainContext = new PaintDomainContext(
					new PaintContextFactory().createContext(),
					new SelectionOriginHolderImpl(),
					new ByValueContextImpl());
			var paintContext = domainContext.getPaintContext();
			var presentationContext = new CreasePatternPresentationContext(
					new CreasePatternViewContextFactory().createContext(),
					mouseActionHolder,
					new TypeForChangeContext());

			var dialogFactory = new MainFrameSwingDialogFactory(
					new ArrayCopyDialogFactory(),
					new CircleCopyDialogFactory(),
					new PropertyDialogFactory());

			var childFrameManager = new ChildFrameManager();

			var pluginLoader = new PluginLoader();

			var plugins = pluginLoader.loadMouseActionPlugins(mainViewSetting.getMainFrameSetting(),
					mainViewSetting.getUiPanelSetting());

			var stateManager = new EditModeStateManager();

			var setterFactory = new MouseActionSetterFactory(
					mouseActionHolder, screenUpdater::updateScreen, paintContext);

			var statePopperFactory = new StatePopperFactory<>(stateManager);
			var stateFactory = new PaintBoundStateFactory(
					stateManager,
					setterFactory,
					mainViewSetting,
					new ComplexActionFactory(
							new EditOutlineActionFactory(statePopperFactory.createForState(), mouseActionHolder),
							new CopyAndPasteActionFactory(statePopperFactory.createForState(),
									domainContext.getSelectionOriginHolder()),
							domainContext.getByValueContext(),
							presentationContext.getTypeForChangeContext()));

			var bindingFactory = new BindingObjectFactoryFacade(stateFactory, setterFactory,
					new PluginPaintBoundStateFactory(stateManager, setterFactory));

			var fileFactory = new FileFactory();

			var subFrameFactory = new SubSwingFrameFactory(
					new FoldabilityCheckSwingFrameFactory(childFrameManager),
					new ModelViewSwingFrameFactory(mainViewSetting.getPainterScreenSetting(),
							childFrameManager),
					new EstimationResultSwingFrameFactory(childFrameManager));
			var fileChooserFactory = new FileChooserSwingFactory();

			var cutModelOutlinesHolder = new DefaultCutModelOutlinesHolder();

			var origamiModelFileAccessService = new FileAccessService<OrigamiModel>(
					new FileDAO<OrigamiModel>(
							new OrigamiModelFileSelectionSupportSelectorFactory().create(fileFactory),
							fileFactory));
			var foldedModelFileAccessFactory = new FoldedModelFileAccessServiceFactory(
					new FoldedModelFileSelectionSupportSelectorFactory(), fileFactory);

			var extensionCorrector = new ExtensionCorrector();

			var foldedModelfileSelectionPresenterFactory = new FoldedModelFileSelectionPresenterFactory(
					fileChooserFactory,
					fileFactory,
					extensionCorrector);

			var origamiModelfileSelectionPresenterFactory = new OrigamiModelFileSelectionPresenterFactory(
					fileChooserFactory,
					fileFactory,
					extensionCorrector);

			var modelViewComponentPresenterFactory = new ModelViewComponentPresenterFactory(cutModelOutlinesHolder);

			var subFramePresenterFactory = new SubFramePresenterFactory(
					fileChooserFactory,
					mainViewSetting.getPainterScreenSetting(),
					foldedModelfileSelectionPresenterFactory,
					foldedModelFileAccessFactory,
					modelViewComponentPresenterFactory,
					origamiModelFileAccessService,
					origamiModelfileSelectionPresenterFactory,
					cutModelOutlinesHolder,
					fileFactory);

			var docFileAccessService = new FileAccessService<Doc>(
					new FileDAO<>(
							new DocFileSelectionSupportSelectorFactory().create(fileFactory),
							fileFactory));

			var modelComputationFacadeFactory = new ModelComputationFacadeFactory(
					new TestedOrigamiModelFactory(),
					new FolderFactory());
			var modelIndexChangeListenerPutter = new ModelIndexChangeListenerPutter();
			var modelFactory = new TestedOrigamiModelFactory();
			var mainComponentPresenterFactory = new MainComponentPresenterFactory(
					mainViewSetting.getPainterScreenSetting(),
					subFrameFactory,
					subFramePresenterFactory,
					modelIndexChangeListenerPutter,
					fileChooserFactory,
					modelComputationFacadeFactory,
					presentationContext,
					statePopperFactory,
					viewUpdateSupport,
					domainContext,
					cutModelOutlinesHolder,
					bindingFactory,
					modelFactory,
					fileFactory,
					extensionCorrector);

			var fileHistory = new FileHistory(Constants.MRUFILE_NUM, fileFactory);
			var iniFileAccess = new IniFileAccess(
					new InitDataFileReader(), new InitDataFileWriter());

			var project = new Project();

			var paintContextModification = new PaintContextModification();

			Supplier<CreasePatternFOLDConfig> foldConfigFactory = () -> new CreasePatternFOLDConfig();

			var resourceHolder = ResourceHolder.getInstance();

			var screenPresenter = mainComponentPresenterFactory
					.createPainterScreenPresenter(mainFrame.getPainterScreenView());
			var uiPanelPresenter = mainComponentPresenterFactory
					.createUIPanelPresenter(mainFrame.getUIPanelView());

			var fileAccessPresentationLogic = new FileAccessPresentationLogic(
					mainFrame,
					childFrameManager,
					screenPresenter,
					mainViewSetting.getPainterScreenSetting(),
					paintContextModification,
					paintContext,
					cutModelOutlinesHolder,
					project,
					docFileAccessService);

			var presentationLogic = new MainFramePresentationLogic(
					mainFrame,
					mainViewSetting.getPainterScreenSetting(),
					screenUpdater,
					dialogFactory,
					subFrameFactory,
					screenPresenter,
					uiPanelPresenter,
					mainComponentPresenterFactory,
					fileAccessPresentationLogic,
					presentationContext.getViewContext(),
					childFrameManager,
					bindingFactory,
					project,
					paintContext,
					paintContextModification,
					cutModelOutlinesHolder,
					fileHistory,
					iniFileAccess,
					docFileAccessService,
					fileFactory,
					resourceHolder);

			var presenter = new MainFramePresenter(
					mainFrame,
					dialogFactory,
					subFrameFactory,
					presentationLogic,
					mainComponentPresenterFactory,
					mouseActionHolder,
					bindingFactory,
					statePopperFactory,
					project,
					paintContext,
					docFileAccessService,
					plugins,
					foldConfigFactory);
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
