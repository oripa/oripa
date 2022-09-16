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
package oripa.gui.presenter.model;

import javax.vecmath.Vector2d;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.domain.cutmodel.CutModelOutlinesFactory;
import oripa.domain.cutmodel.CutModelOutlinesHolder;
import oripa.gui.view.model.ModelGraphics;
import oripa.gui.view.model.ModelViewScreenView;
import oripa.gui.view.util.CallbackOnUpdate;
import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
public class ModelViewScreenPresenter {
	private static final Logger logger = LoggerFactory.getLogger(ModelViewScreenPresenter.class);

	private final ModelViewScreenView view;

	// private OrigamiModel origamiModel;
	private OriLine scissorsLine = null;

	private final CutModelOutlinesHolder lineHolder;
	private final CallbackOnUpdate onUpdateScissorsLine;

	public ModelViewScreenPresenter(
			final ModelViewScreenView view,
			final CutModelOutlinesHolder lineHolder,
			final CallbackOnUpdate onUpdateScissorsLine) {
		this.view = view;

		this.lineHolder = lineHolder;
		this.onUpdateScissorsLine = onUpdateScissorsLine;

		addListeners();
	}

	private void addListeners() {
		view.setPaintComponentListener(this::paintComponent);
		view.setScissorsLineChangeListener(this::recalcScissorsLine);
		view.setCallbackOnUpdateScissorsLine(onUpdateScissorsLine);
	}

	public void paintComponent(final ModelGraphics m) {

		var origamiModel = view.getModel();

		if (origamiModel == null) {
			logger.info("null origamiModel.");
			return;
		}

		if (!origamiModel.hasModel()) {
			logger.info("origamiModel does not have a model data.");
			return;
		}

		var objDrawer = m.getBufferObjectDrawer();

		var drawer = new OrigamiModelGraphicDrawer();

		drawer.draw(objDrawer, origamiModel, view.isScissorsLineVisible() ? scissorsLine : null,
				view.getModelDisplayMode(),
				view.getScale());

		m.drawBufferImage();
	}

	private void recalcScissorsLine() {
		scissorsLine = new OriLine();

		var scissorsLineAngleDegree = view.getScissorsLineAngleDegree();
		var scissorsLinePosition = view.getScissorsLinePosition();
		var modelCenter = view.getModelCenter();

		Vector2d dir = new Vector2d(Math.cos(Math.PI * scissorsLineAngleDegree / 180.0),
				Math.sin(Math.PI * scissorsLineAngleDegree / 180.0));
		scissorsLine.p0.set(modelCenter.x - dir.x * 300, modelCenter.y - dir.y * 300);
		scissorsLine.p1.set(modelCenter.x + dir.x * 300, modelCenter.y + dir.y * 300);
		Vector2d moveVec = new Vector2d(-dir.y, dir.x);
		moveVec.normalize();
		moveVec.scale(scissorsLinePosition);
		scissorsLine.p0.add(moveVec);
		scissorsLine.p1.add(moveVec);

		var factory = new CutModelOutlinesFactory();
		lineHolder.setOutlines(factory.createOutlines(scissorsLine, view.getModel()));

		view.repaint();

		onUpdateScissorsLine.onUpdate();
	}

}
