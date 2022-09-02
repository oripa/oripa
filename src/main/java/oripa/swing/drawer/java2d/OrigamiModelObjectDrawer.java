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
package oripa.swing.drawer.java2d;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.util.List;

import javax.vecmath.Vector2d;

import oripa.gui.view.model.ObjectGraphicDrawer;
import oripa.value.OriLine;

/**
 * @author OUCHI Koji
 *
 */
public class OrigamiModelObjectDrawer implements ObjectGraphicDrawer {
	private final Graphics2D g2d;

	private final OrigamiModelElementSelector selector = new OrigamiModelElementSelector();
	private final GraphicItemConverter converter = new GraphicItemConverter();

	public OrigamiModelObjectDrawer(final Graphics2D g2d) {
		this.g2d = g2d;
	}

	@Override
	public void selectScissorsLineColor() {
		g2d.setColor(selector.getScissorsLineColorForModelView());
	}

	@Override
	public void selectEdgeColor() {
		g2d.setColor(selector.getEdgeColor());
	}

	@Override
	public void setTranslucent(final boolean translucent) {
		if (translucent) {
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
		} else {
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		}
	}

	@Override
	public void selectFaceColor() {
		g2d.setColor(selector.getFaceColor());
	}

	@Override
	public void selectDefaultStroke(final double scale) {
		g2d.setStroke(selector.createDefaultStroke(scale));
	}

	@Override
	public void selectScissorsLineStroke(final double scale) {
		g2d.setStroke(selector.createScissorsLineStrokeForModelView(scale));
	}

	@Override
	public void selectPaperBoundaryStroke(final double scale) {
		g2d.setStroke(selector.createPaperBoundaryStrokeForModelView(scale));
	}

	@Override
	public void selectFaceEdgeStroke(final double scale) {
		g2d.setStroke(selector.createFaceEdgeStrokeForModelView(scale));
	}

	@Override
	public void drawLine(final OriLine line) {
		g2d.draw(converter.toLine2D(line));
	}

	@Override
	public void drawLine(final Vector2d p0, final Vector2d p1) {
		g2d.draw(converter.toLine2D(p0, p1));
	}

	@Override
	public void fillFace(final List<Vector2d> vertices) {
		g2d.fill(converter.toPath2D(vertices));
	}

}
