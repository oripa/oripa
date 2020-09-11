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
package oripa.bind.state.action;

import oripa.domain.paint.GraphicMouseActionInterface;
import oripa.domain.paint.MouseActionHolder;
import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.ScreenUpdaterInterface;

/**
 * @author OUCHI Koji
 *
 */
public class PaintActionSetterFactory {
	private final MouseActionHolder actionHolder;
	private final ScreenUpdaterInterface screenUpdater;
	private final PaintContextInterface context;

	/**
	 * Constructor
	 */
	public PaintActionSetterFactory(
			final MouseActionHolder actionHolder,
			final ScreenUpdaterInterface screenUpdater,
			final PaintContextInterface context) {
		this.actionHolder = actionHolder;
		this.screenUpdater = screenUpdater;
		this.context = context;
	}

	public PaintActionSetter create(final GraphicMouseActionInterface mouseAction) {
		return new PaintActionSetter(actionHolder, mouseAction, screenUpdater, context);
	}
}
