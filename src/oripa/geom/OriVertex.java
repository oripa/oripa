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

import java.util.ArrayList;
import javax.vecmath.Vector2d;

public class OriVertex {

    public Vector2d p = new Vector2d();
    public Vector2d preP = new Vector2d();
    public Vector2d tmpVec = new Vector2d();
    public ArrayList<OriEdge> edges = new ArrayList<>();
    public boolean tmpFlg = false;
    public boolean hasProblem = false;
    public int tmpInt = 0;

    public OriVertex(Vector2d p) {
        this.p.set(p);
        preP.set(p);
    }

    public OriVertex(double x, double y) {
        p.set(x, y);
        preP.set(p);
    }

    // To store and sort in a clockwise direction
    public void addEdge(OriEdge edge) {
        double angle = getAngle(edge);
        int egNum = edges.size();
        boolean added = false;
        for (int i = 0; i < egNum; i++) {
            double tmpAngle = getAngle(edges.get(i));
            if (tmpAngle > angle) {
                edges.add(i, edge);
                added = true;
                break;
            }
        }

        if (!added) {
            edges.add(edge);
        }

    }

    private double getAngle(OriEdge edge) {
        Vector2d dir = new Vector2d();
        if (edge.sv == this) {
            dir.set(edge.ev.p.x - this.p.x, edge.ev.p.y - this.p.y);
        } else {
            dir.set(edge.sv.p.x - this.p.x, edge.sv.p.y - this.p.y);
        }

        return Math.atan2(dir.y, dir.x);

    }

    public OriEdge getPrevEdge(OriEdge e) {
        int index = edges.lastIndexOf(e);
        int eNum = edges.size();
        return edges.get((index - 1 + eNum) % eNum);
    }
}
