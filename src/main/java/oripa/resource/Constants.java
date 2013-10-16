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

package oripa.resource;

public class Constants {

    public static enum EditMode {

        NONE,
        INPUT_LINE,
        CHANGE_LINE_TYPE,
        DELETE_LINE,
        DIVIDE_LINE,
        PICK_LINE,
        ADD_VERTEX,
        DELETE_VERTEX,
        EDIT_OUTLINE
    };

    public static enum ModelEditMode {

        NONE,
        INPUT_CROSS_LINE
    };

    public static enum LineInputMode {

        DIRECT_V,
        ON_V,
        OVERLAP_V,
        OVERLAP_E,
        TRIANGLE_SPLIT,
        BISECTOR,
        VERTICAL_LINE,
        SYMMETRIC_LINE,
        BY_VALUE,
        MIRROR,
        COPY_AND_PASTE,
        PBISECTOR // perpendicular bisector
    };

    public static enum SubLineInputMode {

        NONE,
        PICK_LENGTH,
        PICK_ANGLE
    };

    public static enum ModelDispMode {

        FILL_COLOR,
        FILL_WHITE,
        FILL_ALPHA,
        FILL_NONE
    };
    final public static double EPS = 1.0e-6;
    final public static double DEFAULT_PAPER_SIZE = 400;
}
