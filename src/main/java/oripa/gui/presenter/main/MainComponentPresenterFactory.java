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
import oripa.appstate.StatePopperFactory;
import oripa.domain.cutmodel.CutModelOutlinesHolder;
import oripa.domain.fold.TestedOrigamiModelFactory;
import oripa.domain.paint.PaintDomainContext;
import oripa.domain.projectprop.PropertyHolder;
import oripa.gui.bind.state.BindingObjectFactoryFacade;
import oripa.gui.presenter.creasepattern.CreasePatternPresentationContext;
import oripa.gui.presenter.creasepattern.EditMode;
import oripa.gui.view.FrameView;
import oripa.gui.view.file.FileChooserFactory;
import oripa.gui.view.main.ArrayCopyDialogView;
import oripa.gui.view.main.CircleCopyDialogView;
import oripa.gui.view.main.PainterScreenSetting;
import oripa.gui.view.main.PainterScreenView;
import oripa.gui.view.main.PropertyDialogView;
import oripa.gui.view.main.SubFrameFactory;
import oripa.gui.view.main.UIPanelView;
import oripa.gui.view.main.ViewUpdateSupport;
import oripa.persistence.doc.Doc;
import oripa.util.file.ExtensionCorrector;
import oripa.util.file.FileFactory;

/**
 * @author OUCHI Koji
 *
 */
public class MainComponentPresenterFactory {
	private final SubFrameFactory subFrameFactory;
	private final SubFramePresenterFactory subFramePresenterFactory;
	private final ModelComputationFacadeFactory modelComputationFacadeFactory;
	private final FileChooserFactory fileChooserFactory;
	private final StatePopperFactory<EditMode> statePopperFactory;
	private final ViewUpdateSupport viewUpdateSupport;
	private final CreasePatternPresentationContext presentationContext;
	private final PaintDomainContext domainContext;
	private final CutModelOutlinesHolder cutModelOutlinesHolder;
	private final BindingObjectFactoryFacade bindingFactory;
	private final TestedOrigamiModelFactory modelFactory;
	private final FileFactory fileFactory;
	private final PainterScreenSetting mainScreenSetting;
	private final ExtensionCorrector extensionCorrector;

	public MainComponentPresenterFactory(
			final PainterScreenSetting mainScreenSetting,
			final SubFrameFactory subFrameFactory,
			final SubFramePresenterFactory subFramePresenterFactory,
			final FileChooserFactory fileChooserFactory,
			final ModelComputationFacadeFactory computationFacadeFactory,
			final CreasePatternPresentationContext presentationContext,
			final StatePopperFactory<EditMode> statePopperFactory,
			final ViewUpdateSupport viewUpdateSupport,
			final PaintDomainContext domainContext,
			final CutModelOutlinesHolder cutModelOutlinesHolder,
			final BindingObjectFactoryFacade bindingFactory,
			final TestedOrigamiModelFactory modelFactory,
			final FileFactory fileFactory,
			final ExtensionCorrector extensionCorrector) {

		this.subFrameFactory = subFrameFactory;
		this.subFramePresenterFactory = subFramePresenterFactory;
		this.modelComputationFacadeFactory = computationFacadeFactory;
		this.fileChooserFactory = fileChooserFactory;
		this.statePopperFactory = statePopperFactory;
		this.viewUpdateSupport = viewUpdateSupport;
		this.presentationContext = presentationContext;
		this.domainContext = domainContext;
		this.cutModelOutlinesHolder = cutModelOutlinesHolder;
		this.bindingFactory = bindingFactory;
		this.modelFactory = modelFactory;
		this.fileFactory = fileFactory;
		this.mainScreenSetting = mainScreenSetting;
		this.extensionCorrector = extensionCorrector;

	}

	public UIPanelPresenter createUIPanelPresenter(
			final UIPanelView view) {

		return new UIPanelPresenter(
				view,
				subFrameFactory,
				subFramePresenterFactory,
				modelComputationFacadeFactory,
				statePopperFactory,
				viewUpdateSupport,
				presentationContext,
				domainContext,
				cutModelOutlinesHolder,
				bindingFactory,
				mainScreenSetting);
	}

	public PainterScreenPresenter createPainterScreenPresenter(
			final PainterScreenView view) {
		return new PainterScreenPresenter(
				view,
				viewUpdateSupport,
				presentationContext,
				domainContext.getPaintContext(),
				cutModelOutlinesHolder);
	}

	public ArrayCopyDialogPresenter createArrayCopyDialogPresenter(
			final ArrayCopyDialogView view) {
		return new ArrayCopyDialogPresenter(
				view,
				domainContext.getPaintContext(),
				viewUpdateSupport.getViewScreenUpdater());
	}

	public CircleCopyDialogPresenter createCircleCopyDialogPresenter(
			final CircleCopyDialogView view) {
		return new CircleCopyDialogPresenter(
				view,
				domainContext.getPaintContext(),
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
				modelFactory,
				fileFactory,
				fileSelectionService,
				extensionCorrector);
	}
}
