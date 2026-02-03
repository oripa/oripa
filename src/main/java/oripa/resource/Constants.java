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

import java.io.File;

public class Constants {

    public static final double DEFAULT_PAPER_SIZE = 400;
    public static final int DEFAULT_GRID_DIV_NUM = 4;
    public static final String USER_HOME_DIR_PATH = System.getProperty("user.home");
    public static final String INI_FILE_PATH = USER_HOME_DIR_PATH + File.separator + "oripa.ini";
    public static final String FOLDED_SVG_CONFIG_PATH = USER_HOME_DIR_PATH + File.separator + "oripa-folded-svg.config";
    public static final boolean FOR_STUDY = false;
    public static final int MRUFILE_NUM = 10;
}
