/**
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2005-2009 Jun Mitani http://mitani.cs.tsukuba.ac.jp/

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

package oripa;

import oripa.value.OriLine;
import oripa.vecmath.Vector2d;

//TODO: Move this class to the package of persistence layer in order to remove circular dependency.
//This change disables to read old opx file since serialization target becomes different.
public class OriLineProxy {
	private double x0;
	private double y0;
	private double x1;
	private double y1;
	private int type;

	public OriLineProxy() {
	}

	public OriLineProxy(final OriLine l) {
		x0 = l.getP0().getX();
		y0 = l.getP0().getY();
		x1 = l.getP1().getX();
		y1 = l.getP1().getY();
		type = l.getType().toInt();
	}

	public OriLine getLine() {
		return new OriLine(new Vector2d(x0, y0), new Vector2d(x1, y1), OriLine.Type.fromInt(type));
	}

	public void setX0(final double x0) {
		this.x0 = x0;
	}

	public double getX0() {
		return x0;
	}

	public void setX1(final double x1) {
		this.x1 = x1;
	}

	public double getX1() {
		return x1;
	}

	public void setY0(final double y0) {
		this.y0 = y0;
	}

	public double getY0() {
		return y0;
	}

	public void setY1(final double y1) {
		this.y1 = y1;
	}

	public double getY1() {
		return y1;
	}

	public void setType(final int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

}