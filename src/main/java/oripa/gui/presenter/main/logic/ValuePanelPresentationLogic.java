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

import jakarta.inject.Inject;
import oripa.domain.paint.PaintContext;
import oripa.gui.view.main.UIPanelView;
import oripa.util.MathUtil;

/**
 * @author OUCHI Koji
 *
 */
public class ValuePanelPresentationLogic {

	private final UIPanelView view;
	private final PaintContext paintContext;

	@Inject
	public ValuePanelPresentationLogic(
			final UIPanelView view,
			final PaintContext paintContext) {

		this.view = view;
		this.paintContext = paintContext;
	}

	/**
	 * Updates text fields' format setting based on eps in context.
	 */
	public void updateValuePanelFractionDigits() {
		view.setValuePanelFractionDigits(
				computeValuePanelFractionDigits(paintContext.getPointEps()),
				computeValuePanelFractionDigits(MathUtil.angleDegreeEps()));
	}

	private int computeValuePanelFractionDigits(final double eps) {
		// 1 digit is added for precision.
		return (int) Math.floor(Math.abs(Math.log10(eps))) + 1;
	}

}
