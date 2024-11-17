/**
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2013-     ORIPA OSS Project  https://github.com/oripa/oripa
 * Copyright (C) 2005-2009 Jun Mitani         http://mitani.cs.tsukuba.ac.jp/

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 3 of the License; or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful;
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not; see <http://www.gnu.org/licenses/>.
 */
package oripa.gui.presenter.main;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import oripa.application.FileAccessService;
import oripa.application.main.IniFileAccess;
import oripa.application.main.PaintContextModification;
import oripa.appstate.StatePopperFactory;
import oripa.domain.cutmodel.CutModelOutlinesHolder;
import oripa.domain.paint.PaintContext;
import oripa.domain.paint.PaintDomainContext;
import oripa.file.FileHistory;
import oripa.file.InitData;
import oripa.gui.bind.state.BindingObjectFactoryFacade;
import oripa.gui.presenter.creasepattern.CreasePatternPresentationContext;
import oripa.gui.presenter.creasepattern.EditMode;
import oripa.gui.presenter.plugin.GraphicMouseActionPlugin;
import oripa.gui.view.main.MainFrameDialogFactory;
import oripa.gui.view.main.MainFrameView;
import oripa.gui.view.main.MainViewSetting;
import oripa.gui.view.main.PainterScreenSetting;
import oripa.gui.view.main.SubFrameFactory;
import oripa.gui.view.main.ViewUpdateSupport;
import oripa.gui.view.util.ChildFrameManager;
import oripa.persistence.doc.Doc;
import oripa.persistence.doc.DocFileTypes;
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
@ExtendWith(MockitoExtension.class)
class MainFramePresenterTest {

	@Mock
	MainFrameView view;

	@Mock
	ViewUpdateSupport viewUpdateSupport;

	@Mock
	MainFrameDialogFactory dialogFactory;

	@Mock
	SubFrameFactory subFrameFactory;

	@Mock
	MainComponentPresenterFactory componentPresenterFactory;

	@Mock
	ChildFrameManager childFrameManager;

	@Mock
	MainViewSetting viewSetting;

	@Mock
	BindingObjectFactoryFacade bindingFactory;

	@Mock
	Project project;

	@Mock
	PaintDomainContext domainContext;

	@Mock
	PaintContextModification paintContextModification;

	@Mock
	CutModelOutlinesHolder cutModelOutlinesHolder;

	@Mock
	CreasePatternPresentationContext presentationContext;

	@Mock
	StatePopperFactory<EditMode> statePopperFactory;

	@Mock
	FileHistory fileHistory;

	@Mock
	IniFileAccess iniFileAccess;

	@Mock
	FileAccessService<Doc> dataFileAccess;

	@Mock
	FileFactory fileFactory;

	@Mock
	List<GraphicMouseActionPlugin> plugins;

	@Mock
	Supplier<CreasePatternFOLDConfig> foldConfigFactory;

	@Mock
	ResourceHolder resourceHolder;

	@Nested
	class Constructor {

		@Nested
		class TestLoadIniFile {

			@MethodSource("createIniFileShouldBeLoadedArguments")
			@ParameterizedTest
			void iniFileShouldBeLoaded(
					final boolean isZeroLineWidth,
					final boolean isMvLineVisible,
					final boolean isAuxLineVisible,
					final boolean isVertexVisible) {
				setupResourceHolder();

				setupView();

				PainterScreenSetting screenSetting = mock();
				setupViewSetting(screenSetting);

				setupViewUpdateSupport();

				setupPresentationContext();

				setupDomainContext(mock());

				setupComponentPresenterFactory();

				setupBindingFactory();

				InitData initData = mock();
				when(initData.isZeroLineWidth()).thenReturn(isZeroLineWidth);
				when(initData.isMvLineVisible()).thenReturn(isMvLineVisible);
				when(initData.isAuxLineVisible()).thenReturn(isAuxLineVisible);
				when(initData.isVertexVisible()).thenReturn(isVertexVisible);

				setupIniFileAccess(initData);

				setupProject();

				construct();

				verify(iniFileAccess).load();

				verify(fileHistory).loadFromInitData(initData);

				verify(screenSetting).setZeroLineWidth(isZeroLineWidth);
				verify(screenSetting).setMVLineVisible(isMvLineVisible);
				verify(screenSetting).setAuxLineVisible(isAuxLineVisible);
				verify(screenSetting).setVertexVisible(isVertexVisible);
			}

			static List<Arguments> createIniFileShouldBeLoadedArguments() {
				var booleanValues = List.of(true, false);

				var args = new ArrayList<Arguments>();

				for (var zeroWidth : booleanValues) {
					for (var mvLine : booleanValues) {
						for (var auxLine : booleanValues) {
							for (var vertex : booleanValues) {
								args.add(Arguments.of(zeroWidth, mvLine, auxLine, vertex));
							}
						}
					}
				}
				return args;
			}
		}

		@Nested
		class TestModifySavingActions {
			@Captor
			ArgumentCaptor<Supplier<Object>> foldConfigCaptor;

			@Test
			void saveConfigurationOfFOLDShouldBeDone() {
				setupResourceHolder();

				setupView();

				setupViewSetting();

				setupViewUpdateSupport();

				setupPresentationContext();

				PaintContext paintContext = mock();
				when(paintContext.getPointEps()).thenReturn(1e-8);
				setupDomainContext(paintContext);

				setupComponentPresenterFactory();

				setupBindingFactory();

				setupIniFileAccess();

				CreasePatternFOLDConfig config = mock();
				setupFOLDConfigFactory(config);

				setupProject();

				construct();

				verify(dataFileAccess).setConfigToSavingAction(eq(DocFileTypes.fold()), foldConfigCaptor.capture());

				var createdConfig = foldConfigCaptor.getValue().get();

				assertEquals(config, createdConfig);

				verify(config).setEps(anyDouble());
			}
		}

		@Nested
		class TestAddPlugins {
			@Test
			void givenPluginsShouldBeAdded() {
				setupResourceHolder();

				setupView();

				setupViewSetting();

				setupViewUpdateSupport();

				setupPresentationContext();

				setupDomainContext();

				UIPanelPresenter uiPanelPresenter = mock();
				setupComponentPresenterFactory(mock(), uiPanelPresenter);

				setupBindingFactory();

				setupIniFileAccess();

				setupProject();

				construct();

				verify(uiPanelPresenter).addPlugins(plugins);
			}
		}

		@Nested
		class TestBuildFileMenu {
			@Test
			void buildFileMenuShouldBeCalled() {
				setupResourceHolder();

				setupView();

				setupViewSetting();

				setupViewUpdateSupport();

				setupPresentationContext();

				setupDomainContext();

				setupComponentPresenterFactory();

				setupBindingFactory();

				setupIniFileAccess();

				setupProject();

				construct();

				verify(view).buildFileMenu();
			}
		}

		@Nested
		class TestUpdateTitleText {
			@Captor
			ArgumentCaptor<ResourceKey> resourceKeyCaptor;

			@Captor
			ArgumentCaptor<String> textIdCaptor;

			@Test
			void titleTextShouldBeUpdatedWithDefaultText() {

				setupView();

				setupViewSetting();

				setupViewUpdateSupport();

				setupPresentationContext();

				setupDomainContext();

				setupComponentPresenterFactory();

				setupBindingFactory();

				setupIniFileAccess();

				setupProject();

				String defaultText = "title text";
				setupResourceHolder(defaultText);

				construct();

				verify(resourceHolder).getString(resourceKeyCaptor.capture(), textIdCaptor.capture());

				assertEquals(ResourceKey.DEFAULT, resourceKeyCaptor.getValue());
				assertEquals(StringID.Default.FILE_NAME_ID, textIdCaptor.getValue());

				verify(view).setFileNameToTitle(defaultText);

			}
		}

		MainFramePresenter construct() {
			return new MainFramePresenter(
					view,
					viewSetting,
					viewUpdateSupport,
					dialogFactory,
					subFrameFactory,
					componentPresenterFactory,
					presentationContext,
					childFrameManager,
					bindingFactory,
					statePopperFactory,
					project,
					domainContext,
					paintContextModification,
					cutModelOutlinesHolder,
					fileHistory,
					iniFileAccess,
					dataFileAccess,
					fileFactory,
					plugins,
					foldConfigFactory,
					resourceHolder);
		}

		void setupResourceHolder() {
			setupResourceHolder("");
		}

		void setupResourceHolder(final String value) {
			when(resourceHolder.getString(any(), anyString())).thenReturn(value);
		}

		void setupProject() {
			when(project.getDataFileName()).thenReturn(Optional.empty());
		}

		void setupView() {
			when(view.getPainterScreenView()).thenReturn(mock());
			when(view.getUIPanelView()).thenReturn(mock());
		}

		void setupViewSetting(final PainterScreenSetting screenSetting) {
			when(viewSetting.getPainterScreenSetting()).thenReturn(screenSetting);
		}

		void setupViewSetting() {
			setupViewSetting(mock());
		}

		void setupViewUpdateSupport() {
			when(viewUpdateSupport.getViewScreenUpdater()).thenReturn(mock());
		}

		void setupPresentationContext() {
			when(presentationContext.getViewContext()).thenReturn(mock());
			when(presentationContext.getActionHolder()).thenReturn(mock());

		}

		void setupDomainContext(final PaintContext paintContext) {
			when(domainContext.getPaintContext()).thenReturn(paintContext);
		}

		void setupDomainContext() {
			setupDomainContext(mock());
		}

		void setupComponentPresenterFactory(
				final PainterScreenPresenter screenPresenter,
				final UIPanelPresenter uiPanelPresenter) {
			when(componentPresenterFactory.createPainterScreenPresenter(any())).thenReturn(screenPresenter);
			when(componentPresenterFactory.createUIPanelPresenter(any())).thenReturn(uiPanelPresenter);
		}

		void setupComponentPresenterFactory() {
			setupComponentPresenterFactory(mock(), mock());
		}

		void setupBindingFactory() {
			when(bindingFactory.createState(anyString())).thenReturn(mock());
			when(bindingFactory.createState(anyString(), any(), any())).thenReturn(mock());
		}

		void setupIniFileAccess(final InitData initData) {
			when(iniFileAccess.load()).thenReturn(initData);
		}

		void setupIniFileAccess() {
			setupIniFileAccess(mock());
		}

		void setupFOLDConfigFactory(final CreasePatternFOLDConfig config) {
			when(foldConfigFactory.get()).thenReturn(config);
		}

		void setupFOLDConfigFactory() {
			setupFOLDConfigFactory(mock());
		}
	}

	@Test
	void test() {
		assertNotNull("Not yet implemented");
	}

}
