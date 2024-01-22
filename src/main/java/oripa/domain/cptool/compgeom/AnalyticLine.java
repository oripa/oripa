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
package oripa.domain.cptool.compgeom;

import oripa.util.MathUtil;
import oripa.value.OriLine;

/**
 *
 * @author OUCHI Koji
 *
 */
public class AnalyticLine {
	private final OriLine line;
	private final double angle;
	private final double intercept;

	public AnalyticLine(final OriLine line) {
		this.line = line;

		var p0 = line.getP0();
		var p1 = line.getP1();

		var angle = Math.atan2(p1.getY() - p0.getY(), p1.getX() - p0.getX());
		// limit the angle 0 to PI.
		if (angle < 0) {
			angle += Math.PI;
		}
		// a line with angle PI is the same as one with angle 0.
		if (Math.PI - angle < MathUtil.angleRadianEps()) {
			angle = 0;
		}
		this.angle = angle;

		if (isVertical()) {
			// use x-intercept
			intercept = p0.getX();
		} else {
			// use y-intercept
			intercept = line.getAffineYValueAt(0);
		}
	}

	public boolean isVertical() {
		return MathUtil.areEqual(Math.PI / 2, angle, MathUtil.angleRadianEps());
	}

	/**
	 * @return line
	 */
	public OriLine getLine() {
		return line;
	}

	/**
	 * @return angle
	 */
	public double getAngle() {
		return angle;
	}

	/**
	 * @return intercept
	 */
	public double getIntercept() {
		return intercept;
	}

	/*
	 * (non Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "line: " + line + " angle: " + angle + " intercept: " + intercept;
	}
}