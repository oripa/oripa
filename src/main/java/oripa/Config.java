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

import java.awt.BasicStroke;
import java.awt.Color;

public class Config {

    final public static Color LINE_COLOR_CUT = Color.BLACK;
    final public static Color LINE_COLOR_VALLEY = Color.BLUE;
    final public static Color LINE_COLOR_RIDGE = Color.RED;
    final public static Color LINE_COLOR_AUX = Color.LIGHT_GRAY;
    final public static Color LINE_COLOR_GRID = Color.LIGHT_GRAY;
    final public static Color LINE_COLOR_CANDIDATE = Color.GREEN;
    final public static Color LINE_COLOR_CANDIDATE2 = Color.MAGENTA;
    final public static Color LINE_COLOR_PICKED = Color.GREEN;
    
    final static float dash[] = {3.0f};
    
    final public static BasicStroke STROKE_CUT = new BasicStroke(0.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
    final public static BasicStroke STROKE_VALLEY = new BasicStroke(0.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
    final public static BasicStroke STROKE_RIDGE = new BasicStroke(0.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
    final public static BasicStroke STROKE_PICKED = new BasicStroke(0.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
    final public static BasicStroke STROKE_GRID = new BasicStroke(0.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
    final public static BasicStroke MODEL_STROKE_CUT = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
    final public static BasicStroke STROKE_MOVING = new BasicStroke(0.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
    final public static BasicStroke STROKE_SELECT_BY_AREA = new BasicStroke(0.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
    // Editing outlines (?)
    final public static BasicStroke STROKE_TMP_OUTLINE = new BasicStroke(3.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
    
    final public static boolean FOR_STUDY = false;
    final public static int DEFAULT_GRID_DIV_NUM = 4;
    final public static int MRUFILE_NUM = 10;
}
