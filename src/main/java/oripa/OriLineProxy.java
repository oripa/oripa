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

import javax.vecmath.Vector2d;
import oripa.geom.OriLine;

public class OriLineProxy {
    private double x0;
    private double y0;
    private double x1;
    private double y1;
    private int type;
    
    public OriLineProxy() {}
    public OriLineProxy(OriLine l) {
        x0 = l.p0.x;
        y0 = l.p0.y;
        x1 = l.p1.x;
        y1 = l.p1.y;
        type = l.typeVal;
    }
    
    public OriLine getLine() {
        return new OriLine(new Vector2d(x0, y0), new Vector2d(x1, y1), type);
    }
    
    public void setX0(double x0) { this.x0 = x0; }
    public double getX0() { return x0; }
    public void setX1(double x1) { this.x1 = x1; }
    public double getX1() { return x1; }
    public void setY0(double y0) { this.y0 = y0; }
    public double getY0() { return y0; }
    public void setY1(double y1) { this.y1 = y1; }
    public double getY1() { return y1; }
    public void setType(int type) { this.type = type; }
    public int getType() { return type; }
    
    
}