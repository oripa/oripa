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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import oripa.application.FileAccessService;
import oripa.application.main.IniFileAccess;
import oripa.application.main.PaintContextModification;
import oripa.domain.cutmodel.CutModelOutlinesHolder;
import oripa.domain.paint.PaintContext;
import oripa.domain.paint.PaintDomainContext;
import oripa.domain.projectprop.Property;
import oripa.file.FileHistory;
import oripa.file.InitData;
import oripa.gui.bind.state.BindingObjectFactoryFacade;
import oripa.gui.presenter.creasepattern.CreasePatternPresentationContext;
import oripa.gui.view.ViewScreenUpdater;
import oripa.gui.view.main.MainFrameDialogFactory;
import oripa.gui.view.main.MainFrameView;
import oripa.gui.view.main.MainViewSetting;
import oripa.gui.view.main.PainterScreenSetting;
import oripa.gui.view.main.SubFrameFactory;
import oripa.gui.view.main.ViewUpdateSupport;
import oripa.gui.view.util.ChildFrameManager;
import oripa.persistence.doc.Doc;
import oripa.persistence.doc.exporter.CreasePatternFOLDConfig;
import oripa.project.Project;
import oripa.resource.ResourceHolder;
import oripa.util.file.FileFactory;

/**
 * @author OUCHI Koji
 *
 */
@ExtendWith(MockitoExtension.class)
public class MainFramePresentationLogicTest {

	@Mock
	MainFrameView view;

	@Mock
	ViewUpdateSupport viewUpdateSupport;

	@Mock
	MainFrameDialogFactory dialogFactory;

	@Mock
	SubFrameFactory subFrameFactory;

	@Mock
	PainterScreenPresenter screenPresenter;

	@Mock
	UIPanelPresenter uiPanelPresenter;

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
	FileHistory fileHistory;

	@Mock
	IniFileAccess iniFileAccess;

	@Mock
	FileAccessService<Doc> dataFileAccess;

	@Mock
	FileFactory fileFactory;

	@Mock
	Supplier<CreasePatternFOLDConfig> foldConfigFactory;

	@Mock
	ResourceHolder resourceHolder;

	@Nested
	class TestExit {
		@Test
		public void shouldSaveInifile() {

			var presentationLogic = construct();

			Runnable doExit = mock();
			presentationLogic.exit(doExit);

			verify(iniFileAccess).save(eq(fileHistory), any());

			verify(doExit).run();
		}
	}

	@Nested
	class TestLoadFileImpl {

		@Captor
		ArgumentCaptor<Function<Doc, String>> loadFileMapperCaptor;

		@Test
		void succeeds() {

			PainterScreenSetting screenSetting = mock();
			setupViewSetting(screenSetting);

			setupViewUpdateSupport();

			setupDomainContext();

			String path = "path";
			Optional<Doc> docOpt = mock();
			when(dataFileAccess.loadFile(eq(path))).thenReturn(docOpt);

			Property currentProperty = mock();
			when(project.getProperty()).thenReturn(currentProperty);

			var presentationLogic = construct();
			presentationLogic.loadFileImpl(path);

			verify(childFrameManager).closeAll(view);

			verify(docOpt).map(loadFileMapperCaptor.capture());

			Property loadedProperty = mock();
			Doc loadedDoc = mock();
			when(loadedDoc.getProperty()).thenReturn(loadedProperty);
			when(loadedDoc.getCreasePattern()).thenReturn(mock());

			loadFileMapperCaptor.getValue().apply(loadedDoc);

			verify(dataFileAccess).loadFile(path);
			verify(project).setProperty(loadedProperty);
			verify(project).setDataFilePath(anyString());
			verify(view).setEstimationResultColors(any(), any());
			verify(screenSetting).setGridVisible(false);
			verify(paintContextModification).setCreasePatternToPaintContext(any(), any(), eq(cutModelOutlinesHolder));
			verify(screenPresenter).updateCameraCenter();

		}

	}

	MainFramePresentationLogic construct() {
		return new MainFramePresentationLogic(
				view,
				viewSetting,
				viewUpdateSupport,
				dialogFactory,
				subFrameFactory,
				screenPresenter,
				uiPanelPresenter,
				componentPresenterFactory,
				presentationContext,
				childFrameManager,
				bindingFactory,
				project,
				domainContext,
				paintContextModification,
				cutModelOutlinesHolder,
				fileHistory,
				iniFileAccess,
				dataFileAccess,
				fileFactory,
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

	void setupViewUpdateSupport(final ViewScreenUpdater screenUpdater) {
		when(viewUpdateSupport.getViewScreenUpdater()).thenReturn(screenUpdater);
	}

	void setupViewUpdateSupport() {
		setupViewUpdateSupport(mock());
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
