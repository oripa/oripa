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

	public static enum ModelDisplayMode {
//		FILL_COLOR, FILL_WHITE,
		FILL_ALPHA, FILL_NONE
	};

	public final static double DEFAULT_PAPER_SIZE = 400;
	public final static int DEFAULT_GRID_DIV_NUM = 4;
	public final static String UER_HOME_DIR_PATH = System.getProperty("user.home");
}
