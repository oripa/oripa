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

import java.util.function.Consumer;

import oripa.gui.presenter.creasepattern.GraphicMouseAction;
import oripa.gui.presenter.creasepattern.MouseActionHolder;
import oripa.gui.presenter.creasepattern.copypaste.CopyAndPasteAction;

/**
 * @author OUCHI Koji
 *
 */
public class SwitcherBetweenPasteAndChangeOrigin implements Consumer<Boolean> {
	private final MouseActionHolder mouseActionHolder;

	public SwitcherBetweenPasteAndChangeOrigin(final MouseActionHolder mouseActionHolder) {
		this.mouseActionHolder = mouseActionHolder;
	}

	/**
	 * Switches mouse action if it is an instance of {@link CopyAndPasteAction}.
	 * If {@code changingOrigin} is {@code true}, then this object sets
	 * change-origin action, otherwise paste action.
	 *
	 * @param changingOrigin
	 *            {@code true} for changing origin, {@code false} for pasting.
	 */
	@Override
	public void accept(final Boolean changingOrigin) {
		GraphicMouseAction action = mouseActionHolder.getMouseAction().get();
		if (action instanceof CopyAndPasteAction casted) {
			casted.changeAction(changingOrigin);
		}
	}
}
