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
package oripa.gui.presenter.creasepattern;

import oripa.appstate.StatePopper;
import oripa.domain.paint.PaintContext;
import oripa.domain.paint.unselect.AllItemUnselecterCommand;
import oripa.util.Command;

/**
 * @author OUCHI Koji
 *
 */
public class UnselectAllItemsActionListener implements Runnable {
    private final MouseActionHolder actionHolder;
    private final PaintContext context;
    private final StatePopper<EditMode> statePopper;
    private final ScreenUpdater screenUpdater;

    /**
     * Constructor
     */
    public UnselectAllItemsActionListener(
            final MouseActionHolder actionHolder,
            final PaintContext aContext,
            final StatePopper<EditMode> statePopper,
            final ScreenUpdater updater) {
        this.actionHolder = actionHolder;
        context = aContext;
        this.statePopper = statePopper;
        screenUpdater = updater;
    }

    @Override
    public void run() {
        Command command = new AllItemUnselecterCommand(context);

        command.execute();

        var currentActionOpt = actionHolder.getMouseAction();

        currentActionOpt.ifPresent(currentAction -> unselectAll(currentAction));
    }

    private void unselectAll(final GraphicMouseAction currentAction) {
        currentAction.destroy(context);
        currentAction.recover(context);

        if (currentAction.getEditMode() == EditMode.COPY ||
                currentAction.getEditMode() == EditMode.CUT) {
            statePopper.run();
        }

        screenUpdater.updateScreen();
    }

}
