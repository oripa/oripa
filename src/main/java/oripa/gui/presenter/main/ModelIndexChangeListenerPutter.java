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

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.gui.view.estimation.EstimationResultFrameView;
import oripa.gui.view.model.ModelViewFrameView;

/**
 * @author OUCHI Koji
 *
 */
public class ModelIndexChangeListenerPutter {
	private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public void put(final ModelViewFrameView modelViewFrame, final EstimationResultFrameView resultFrame) {
		if (modelViewFrame == null || resultFrame == null) {
			return;
		}
		modelViewFrame.putModelIndexChangeListener(resultFrame,
				e -> {
					logger.debug("modelViewFrame model index change: {} -> {}", e.getOldValue(), e.getNewValue());
					resultFrame.selectModel((Integer) e.getNewValue());
				});
		resultFrame.putModelIndexChangeListener(modelViewFrame,
				e -> {
					logger.debug("resultFrame model index change: {} -> {}", e.getOldValue(), e.getNewValue());
					modelViewFrame.selectModel((Integer) e.getNewValue());
				});

	}

}
