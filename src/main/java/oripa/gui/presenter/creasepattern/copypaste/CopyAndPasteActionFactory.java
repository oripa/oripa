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
package oripa.gui.presenter.creasepattern.copypaste;

import jakarta.inject.Inject;
import oripa.appstate.StatePopper;
import oripa.domain.paint.copypaste.SelectionOriginHolder;
import oripa.gui.presenter.creasepattern.EditMode;
import oripa.gui.presenter.creasepattern.GraphicMouseAction;

/**
 * @author OUCHI Koji
 *
 */
public class CopyAndPasteActionFactory {

    private final StatePopper<EditMode> statePopper;
    private final SelectionOriginHolder originHolder;

    @Inject
    public CopyAndPasteActionFactory(final StatePopper<EditMode> statePopper,
            final SelectionOriginHolder originHolder) {
        this.statePopper = statePopper;
        this.originHolder = originHolder;
    }

    public GraphicMouseAction createCopyAndPaste() {
        return new CopyAndPasteActionWrapper(statePopper, originHolder);
    }

    public GraphicMouseAction createCutAndPaste() {
        return new CutAndPasteActionWrapper(statePopper, originHolder);
    }

    public GraphicMouseAction createImport() {
        return new ImportActionWapper(statePopper, originHolder);
    }
}
