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

import java.awt.BasicStroke;
import java.awt.Color;

/**
 * @author OUCHI Koji
 *
 */
public class OrigamiModelElementSelector {
	public Color getScissorsLineColorForModelView() {
		return Color.RED;
	}

	public Color getEdgeColor() {
		return Color.BLACK;
	}

	public Color getFaceColor() {
		return new Color(100, 100, 100);
	}

	private float createThinLineWidth(final double scale) {
		return 1.5f / (float) scale;
	}

	private float createThickLineWidth(final double scale) {
		return 4.0f / (float) scale;
	}

	public BasicStroke createDefaultStroke(final double scale) {
		return new BasicStroke(createThinLineWidth(scale), BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER);
	}

	public BasicStroke createScissorsLineStrokeForModelView(final double scale) {
		return new BasicStroke(createThinLineWidth(scale), BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER);
	}

	public BasicStroke createPaperBoundaryStrokeForModelView(final double scale) {
		return new BasicStroke(createThickLineWidth(scale), BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER);
	}

	public BasicStroke createFaceEdgeStrokeForModelView(final double scale) {
		return new BasicStroke(createThinLineWidth(scale), BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER);
	}
}
