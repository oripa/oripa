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
import oripa.appstate.StatePopper;
import oripa.domain.paint.PaintContext;
import oripa.domain.paint.copypaste.SelectionOriginHolder;
import oripa.gui.presenter.creasepattern.CreasePatternViewContext;
import oripa.gui.presenter.creasepattern.EditMode;

/**
 * @author OUCHI Koji
 *
 */
public class ImportActionWapper extends CopyAndPasteAction {

	private final StateManager<EditMode> stateManager;

	public ImportActionWapper(
			final StateManager<EditMode> stateManager,
			final SelectionOriginHolder originHolder) {

		super(originHolder, new ImportAction(originHolder));

		this.stateManager = stateManager;

		setEditMode(EditMode.CUT);
	}

	@Override
	protected void recoverImpl(final PaintContext context) {
		super.recoverImpl(context);

		context.creasePatternUndo().pushUndoInfo();
	}

	@Override
	public void onRightClick(final CreasePatternViewContext viewContext, final PaintContext paintContext,
			final boolean differentAction) {
		new StatePopper<>(stateManager).run();
	}

}
