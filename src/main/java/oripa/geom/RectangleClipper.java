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

public class RectangleClipper {

    final static int LEFT = 1;
    final static int RIGHT = 2;
    final static int TOP = 4;
    final static int BOTTOM = 8;
    private double m_minX;
    private double m_minY;
    private double m_maxX;
    private double m_maxY;

    public RectangleClipper(double x0, double y0, double x1, double y1) {
        m_minX = x0;
        m_minY = y0;
        m_maxX = x1;
        m_maxY = y1;
    }

    private int calcCode(double x, double y) {
        int code = 0;
        if (x < m_minX) {
            code += LEFT;
        }
        if (x > m_maxX) {
            code += RIGHT;
        }
        if (y < m_minY) {
            code += TOP;
        }
        if (y > m_maxY) {
            code += BOTTOM;
        }

        return code;
    }

    /*
     * finding the coordinates after clipping
     */
    private int calcClippedPoint(int code, OriLine l, Vector2d p) {
        double cx, cy;

        // Outside from the left edge of the window
        if ((code & LEFT) != 0) {
            cy = (l.p1.y - l.p0.y) * (m_minX - l.p0.x) / (l.p1.x - l.p0.x) + l.p0.y; 
            if ((cy >= m_minY) && (cy <= m_maxY)) {
                p.x = m_minX;
                p.y = cy;
                return 1;
            }
        }

        //Outside the right edge of the window
        if ((code & RIGHT) != 0) {
            cy = (l.p1.y - l.p0.y) * (m_maxX - l.p0.x) / (l.p1.x - l.p0.x) + l.p0.y;
            if ((cy >= m_minY) && (cy <= m_maxY)) {
                p.x = m_maxX;
                p.y = cy;
                return 1;
            }
        }

        // Outside from the top of the window
        if ((code & TOP) != 0) {
            cx = (l.p1.x - l.p0.x) * (m_minY - l.p0.y) / (l.p1.y - l.p0.y) + l.p0.x;
            if ((cx >= m_minX) && (cx <= m_maxX)) {
                p.x = cx;
                p.y = m_minY;
                return 1;
            }
        }

        // Outside from the bottom of the window
        if ((code & BOTTOM) != 0) {
            cx = (l.p1.x - l.p0.x) * (m_maxY - l.p0.y) / (l.p1.y - l.p0.y) + l.p0.x;
            if ((cx >= m_minX) && (cx <= m_maxX)) {
                p.x = cx;
                p.y = m_maxY;
                return 1;
            }
        }

        return -1;  // If it is not clipping, line segment is completely invisible
    }

    // Returns false if not included in the area
    public boolean clip(OriLine l) {
        if (Math.abs(l.p0.x - m_minX) < oripa.resource.Constants.EPS && Math.abs(l.p1.x - m_minX) < oripa.resource.Constants.EPS) {
            return false;
        }
        if (Math.abs(l.p0.x - m_maxX) < oripa.resource.Constants.EPS && Math.abs(l.p1.x - m_maxX) < oripa.resource.Constants.EPS) {
            return false;
        }
        if (Math.abs(l.p0.y - m_minY) < oripa.resource.Constants.EPS && Math.abs(l.p1.y - m_minY) < oripa.resource.Constants.EPS) {
            return false;
        }
        if (Math.abs(l.p0.y - m_maxY) < oripa.resource.Constants.EPS && Math.abs(l.p1.y - m_maxY) < oripa.resource.Constants.EPS) {
            return false;
        }

        int s_code = calcCode(l.p0.x, l.p0.y);
        int e_code = calcCode(l.p1.x, l.p1.y);

        if ((s_code == 0) && (e_code == 0)) {
            return true;
        }

        if ((s_code & e_code) != 0) {
            return false;
        }

        if (s_code != 0) {
            if (calcClippedPoint(s_code, l, l.p0) < 0) {
                return false;
            }
        }

        if (e_code != 0) {
            if (calcClippedPoint(e_code, l, l.p1) < 0) {
                return false;
            }
        }

        return true;
    }

    public boolean clipTest(OriLine l) {
        int s_code = calcCode(l.p0.x, l.p0.y);  
        int e_code = calcCode(l.p1.x, l.p1.y); 
        if ((s_code == 0) && (e_code == 0)) {
            return true;
        }

        if ((s_code & e_code) != 0) {
            return false;
        }

        OriLine lcopy = new OriLine(l);
        if (s_code != 0) {
            if (calcClippedPoint(s_code, lcopy, lcopy.p0) < 0) {
                return false;
            }
        }

        if (e_code != 0) {
            if (calcClippedPoint(e_code, lcopy, lcopy.p1) < 0) {
                return false;
            }
        }

        return true;

    }
}
