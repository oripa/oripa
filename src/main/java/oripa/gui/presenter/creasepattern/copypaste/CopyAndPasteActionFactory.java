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

import oripa.appstate.StateManager;
import oripa.domain.paint.copypaste.SelectionOriginHolder;
import oripa.gui.presenter.creasepattern.EditMode;
import oripa.gui.presenter.creasepattern.GraphicMouseAction;

/**
 * @author OUCHI Koji
 *
 */
public class CopyAndPasteActionFactory {

	private final StateManager<EditMode> stateManager;
	private final SelectionOriginHolder originHolder;

	public CopyAndPasteActionFactory(final StateManager<EditMode> stateManager,
			final SelectionOriginHolder originHolder) {
		this.stateManager = stateManager;
		this.originHolder = originHolder;
	}

	public GraphicMouseAction createCopyAndPaste() {
		return new CopyAndPasteActionWrapper(stateManager, false, originHolder);
	}

	public GraphicMouseAction createCutAndPaste() {
		return new CopyAndPasteActionWrapper(stateManager, true, originHolder);
	}
}
