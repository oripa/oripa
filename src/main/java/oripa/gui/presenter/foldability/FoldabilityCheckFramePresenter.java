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
package oripa.gui.presenter.foldability;

import java.util.Collection;

import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.gui.view.foldability.FoldabilityCheckFrameView;
import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
public class FoldabilityCheckFramePresenter {

	private final FoldabilityCheckFrameView view;

	public FoldabilityCheckFramePresenter(
			final FoldabilityCheckFrameView view,
			final OrigamiModel origamiModel,
			final Collection<OriLine> creasePattern,
			final boolean zeroLineWidth,
			final double pointEps) {

		this.view = view;

		var screenPresenter = new FoldabilityScreenPresenter(
				view.getFoldabilityScreenView(),
				origamiModel,
				creasePattern,
				zeroLineWidth,
				pointEps);
	}

	public void setViewVisible(final boolean visible) {
		view.setVisible(visible);
	}
}
