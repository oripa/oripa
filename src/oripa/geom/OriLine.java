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

package oripa.geom;

import javax.vecmath.Vector2d;


public class OriLine {

	public enum Type{NONE, CUT, RIDGE, VALLEY};

	final public static int TYPE_NONE = 0;
    final public static int TYPE_CUT = 1;
    final public static int TYPE_RIDGE = 2;
    final public static int TYPE_VALLEY = 3;
    public boolean selected;
    public int type = TYPE_NONE;
    public Vector2d p0 = new Vector2d();
    public Vector2d p1 = new Vector2d();

    public OriLine() {
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public OriLine(OriLine l) {
        selected = l.selected;
        p0.set(l.p0);
        p1.set(l.p1);
        type = l.type;
    }

    public OriLine(Vector2d p0, Vector2d p1, int type) {
        this.type = type;
        this.p0.set(p0);
        this.p1.set(p1);
    }

    public OriLine(double x0, double y0, double x1, double y1, int type) {
        this.type = type;
        this.p0.set(x0, y0);
        this.p1.set(x1, y1);
    }

    @Override
    public String toString() {
        return "" + p0 + "" + p1;
    }

    public void changeToNextType() {
        switch (type) {
            case 3:
                type = 0;
                break;
            case 2:
                type = 3;
                break;
            case 1:
                type = 2;
                break;
            case 0:
                type = 2;
                break;
        }
    }

    public Segment getSegment() {
        return new Segment(p0, p1);
    }

    public Line getLine() {
        return new Line(p0, new Vector2d(p1.x - p0.x, p1.y - p0.y));
    }
}
