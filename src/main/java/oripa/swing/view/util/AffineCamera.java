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
package oripa.swing.view.util;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**
 * @author OUCHI Koji
 *
 */
public class AffineCamera {
	private final AffineTransform affineTransform = new AffineTransform();
	private double cameraX, cameraY;
	private double transXOfPaper, transYOfPaper;
	private double centerXOfPaper, centerYOfPaper;
	private double scale;

	public AffineCamera() {

	}

	private AffineTransform updateAffineTransform() {
		affineTransform.setToIdentity();
		affineTransform.translate(cameraX, cameraY);
		affineTransform.scale(scale, scale);
		affineTransform.translate(transXOfPaper - centerXOfPaper, transYOfPaper - centerYOfPaper);

		return affineTransform;
	}

	public AffineTransform getAffineTransform() {
		return affineTransform;
	}

	public AffineTransform updateScale(final double scale) {
		this.scale = scale;

		return updateAffineTransform();
	}

	public double getScale() {
		return scale;
	}

	public AffineTransform updateCameraPosition(final double x, final double y) {
		cameraX = x;
		cameraY = y;

		return updateAffineTransform();
	}

	public AffineTransform updateTranslateOfPaper(final double dx, final double dy) {
		transXOfPaper = dx;
		transYOfPaper = dy;

		return updateAffineTransform();
	}

	public double getTranslateXOfPaper() {
		return transXOfPaper;
	}

	public double getTranslateYOfPaper() {
		return transYOfPaper;
	}

	public AffineTransform updateCenterOfPaper(final double cx, final double cy) {
		centerXOfPaper = cx;
		centerYOfPaper = cy;

		return updateAffineTransform();
	}

	/**
	 *
	 * @param e
	 * @param preMousePoint
	 * @return Affine transform for translating. {@code null} if the mouse event
	 *         is not for translating.
	 */
	public AffineTransform updateTranslateByMouseDragged(
			final MouseEvent e, final Point2D preMousePoint) {

		// move camera
		if (MouseUtility.isRightButtonEvent(e) ||
				(MouseUtility.isLeftButtonEvent(e) && MouseUtility.isShiftKeyDown(e))) {
			double transX = getTranslateXOfPaper()
					+ (e.getX() - preMousePoint.getX()) / getScale();
			double transY = getTranslateYOfPaper()
					+ (e.getY() - preMousePoint.getY()) / getScale();
			return updateTranslateOfPaper(transX, transY);
		}

		return null;
	}

	/**
	 *
	 * @param e
	 * @param preMousePoint
	 * @return Affine transform for scaling. {@code null} if the mouse event is
	 *         not for scaling.
	 */
	public AffineTransform updateScaleByMouseDragged(
			final MouseEvent e, final Point2D preMousePoint) {

		// zoom
		if (MouseUtility.isLeftButtonEvent(e) &&
				MouseUtility.isControlKeyDown(e)) {

			double moved = e.getX() - preMousePoint.getX() + e.getY()
					- preMousePoint.getY();
			double scale = getScale() + moved / 150.0;
			if (scale < 0.01) {
				scale = 0.01;
			}
			return updateScale(scale);
		}

		return null;
	}

	public AffineTransform updateScaleByMouseWheel(final MouseWheelEvent e) {
		double rate = (100.0 - e.getWheelRotation() * 5) / 100.0;
		return updateScale(getScale() * rate);
	}

}
