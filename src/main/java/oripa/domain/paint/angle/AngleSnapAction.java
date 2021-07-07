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
package oripa.domain.paint.angle;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.vecmath.Vector2d;

import oripa.domain.paint.PaintContextInterface;
import oripa.domain.paint.core.GraphicMouseAction;
import oripa.domain.paint.geometry.NearestItemFinder;

/**
 * @author OUCHI Koji
 *
 */
public class AngleSnapAction extends GraphicMouseAction {

//	private final AngleStepSetting setting;

	/**
	 * Constructor
	 */
	public AngleSnapAction() {
		super();

		setActionState(new SelectingStartPoint());
	}

	/*
	 * (non Javadoc)
	 *
	 * @see
	 * oripa.domain.paint.core.GraphicMouseAction#destroy(oripa.domain.paint.
	 * PaintContextInterface)
	 */
	@Override
	public void destroy(final PaintContextInterface context) {
		super.destroy(context);
	}

	/*
	 * (non Javadoc)
	 *
	 * @see
	 * oripa.domain.paint.core.GraphicMouseAction#recoverImpl(oripa.domain.paint
	 * .PaintContextInterface)
	 */
	@Override
	protected void recoverImpl(final PaintContextInterface context) {
		setActionState(new SelectingStartPoint());
	}

	/*
	 * (non Javadoc)
	 *
	 * @see
	 * oripa.domain.paint.core.GraphicMouseAction#onMove(oripa.domain.paint.
	 * PaintContextInterface, java.awt.geom.AffineTransform, boolean)
	 */
	@Override
	public Vector2d onMove(final PaintContextInterface context, final AffineTransform affine,
			final boolean differentAction) {
		if (context.getVertexCount() == 0) {
			return super.onMove(context, affine, differentAction);
		}

		var crossPoint = NearestItemFinder.getNearestInAngleSnapCrossPoints(context);
		context.setCandidateVertexToPick(crossPoint);
		return crossPoint;
	}

	/*
	 * (non Javadoc)
	 *
	 * @see
	 * oripa.domain.paint.core.GraphicMouseAction#onPress(oripa.domain.paint.
	 * PaintContextInterface, java.awt.geom.AffineTransform, boolean)
	 */
	@Override
	public void onPress(final PaintContextInterface context, final AffineTransform affine,
			final boolean differentAction) {

	}

	/*
	 * (non Javadoc)
	 *
	 * @see
	 * oripa.domain.paint.core.GraphicMouseAction#onDrag(oripa.domain.paint.
	 * PaintContextInterface, java.awt.geom.AffineTransform, boolean)
	 */
	@Override
	public void onDrag(final PaintContextInterface context, final AffineTransform affine,
			final boolean differentAction) {

	}

	/*
	 * (non Javadoc)
	 *
	 * @see
	 * oripa.domain.paint.core.GraphicMouseAction#onRelease(oripa.domain.paint.
	 * PaintContextInterface, java.awt.geom.AffineTransform, boolean)
	 */
	@Override
	public void onRelease(final PaintContextInterface context, final AffineTransform affine,
			final boolean differentAction) {

	}

	/*
	 * (non Javadoc)
	 *
	 * @see
	 * oripa.domain.paint.core.GraphicMouseAction#onDraw(java.awt.Graphics2D,
	 * oripa.domain.paint.PaintContextInterface)
	 */
	@Override
	public void onDraw(final Graphics2D g2d, final PaintContextInterface context) {
		if (context.getVertexCount() == 1) {
			drawSnapPoints(g2d, context);
		}

		super.onDraw(g2d, context);
		drawTemporaryLine(g2d, context);
		drawPickCandidateVertex(g2d, context);
	}

	private void drawSnapPoints(final Graphics2D g2d, final PaintContextInterface context) {
		var selector = getElementSelector();
		g2d.setColor(selector.getAssistLineColor());

		context.getAngleSnapCrossPoints()
				.forEach(p -> drawVertex(g2d, context, p));
	}

}
