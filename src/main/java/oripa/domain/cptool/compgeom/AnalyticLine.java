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

import oripa.value.OriLine;

/**
 *
 * @author OUCHI Koji
 *
 */
public class AnalyticLine {
	private static final double EPS = 1e-5;
	private final OriLine line;
	private final double angle;
	private final double intercept;

	public AnalyticLine(final OriLine line) {
		this.line = line;

		var p0 = line.p0;
		var p1 = line.p1;

		var angle = Math.atan2(p1.y - p0.y, p1.x - p0.x);
		// limit the angle 0 to PI.
		if (angle < 0) {
			angle += Math.PI;
		}
		// a line with angle PI is the same as one with angle 0.
		if (Math.PI - angle < EPS) {
			angle = 0;
		}
		this.angle = angle;

		// vertical line doesn't have intercept.
		if (Math.abs(Math.PI / 2 - angle) < EPS) {
			intercept = Double.MAX_VALUE;
		} else {
			intercept = p0.y - (p1.y - p0.y) / (p1.x - p0.x) * p0.x;
		}
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