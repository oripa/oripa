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

package oripa.domain.paint.core;

import oripa.resource.Constants;

public class PaintConfig {

	// FIXME not to be here.
	public static Constants.ModelDispMode modelDispMode = Constants.ModelDispMode.FILL_ALPHA;

	public static boolean dispMVLines = true;
	public static boolean dispAuxLines = true;
	public static boolean bDispCrossLine = false;
	public static int inputLineType = oripa.value.OriLine.TYPE_RIDGE;

	// FIXME not to be here. move to oripa.domain.fold package.
	public static boolean bDoFullEstimation = true;

}
