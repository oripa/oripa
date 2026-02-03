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

import java.util.function.Consumer;
import java.util.function.Supplier;

import jakarta.inject.Inject;
import oripa.domain.paint.PaintContext;
import oripa.gui.bind.state.BindingObjectFactoryFacade;
import oripa.gui.presenter.creasepattern.DeleteSelectedLinesActionListener;
import oripa.gui.presenter.creasepattern.MouseActionHolder;
import oripa.gui.presenter.creasepattern.ScreenUpdater;
import oripa.gui.presenter.creasepattern.SelectAllLineActionListener;
import oripa.gui.presenter.creasepattern.UnselectAllItemsActionListener;
import oripa.resource.StringID;

/**
 * @author OUCHI Koji
 *
 */
public class MainFramePaintMenuListenerFactory {

    final PaintContext paintContext;
    final MouseActionHolder mouseActionHolder;
    final BindingObjectFactoryFacade bindingFactory;

    @Inject
    public MainFramePaintMenuListenerFactory(
            final PaintContext paintContext,
            final MouseActionHolder mouseActionHolder,
            final BindingObjectFactoryFacade bindingFactory

    ) {
        this.paintContext = paintContext;
        this.mouseActionHolder = mouseActionHolder;
        this.bindingFactory = bindingFactory;

    }

    public Runnable createChangeOutlineButtonListener() {
        var changeOutlineState = bindingFactory.createState(StringID.EDIT_CONTOUR_ID);
        return changeOutlineState::performActions;
    }

    public Runnable createCopyAndPasteButtonListener(final Runnable showCopyPasteErrorMessage) {
        Supplier<Boolean> detectCopyPasteError = this::detectCopyPasteError;
        var copyPasteState = bindingFactory.createState(StringID.COPY_PASTE_ID,
                detectCopyPasteError, showCopyPasteErrorMessage);
        return copyPasteState::performActions;
    }

    public Runnable createCutAndPasteButtonListener(final Runnable showCopyPasteErrorMessage) {
        Supplier<Boolean> detectCopyPasteError = this::detectCopyPasteError;
        var cutPasteState = bindingFactory.createState(StringID.CUT_PASTE_ID,
                detectCopyPasteError, showCopyPasteErrorMessage);
        return cutPasteState::performActions;
    }

    private boolean detectCopyPasteError() {
        return paintContext.countSelectedLines() == 0;
    }

    public Runnable createSelectAllLineActionListener() {
        var selectAllState = bindingFactory.createState(StringID.SELECT_ALL_LINE_ID);
        var selectAllListener = new SelectAllLineActionListener(paintContext);

        return () -> {
            selectAllState.performActions();
            selectAllListener.run();
        };
    }

    public Runnable createUnselectAllItemsActionListener(final ScreenUpdater screenUpdater) {
        var statePopper = bindingFactory.createStatePopperForState();
        return new UnselectAllItemsActionListener(mouseActionHolder, paintContext, statePopper,
                screenUpdater);

    }

    public Runnable createDeleteSelectedLinesActionListener(final ScreenUpdater screenUpdater) {
        return new DeleteSelectedLinesActionListener(paintContext, screenUpdater);
    }

    public Runnable createImportButtonListener(final Consumer<Runnable> importFileUsingGUI) {
        var state = bindingFactory.createState(StringID.IMPORT_CP_ID);
        return () -> importFileUsingGUI.accept(state::performActions);
    }
}
