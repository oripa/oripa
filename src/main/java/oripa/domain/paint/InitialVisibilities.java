/**
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2013-     ORIPA OSS Project  https://github.com/oripa/oripa
 * Copyright (C) 2005-2009 Jun Mitani         http://mitani.cs.tsukuba.ac.jp/

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
package oripa.domain.paint;

/**
 * Define the initial values for visibilities of line, vertex and so on.
 *
 * Should be used to initialize PaintContext and view states.
 *
 * @author OUCHI Koji
 *
 */
public class InitialVisibilities {
	public static final boolean ZERO_LINE_WIDTH = false;
	public static final boolean MV = true;
	public static final boolean AUX = true;
	public static final boolean VERTEX = true;
	public static final boolean CROSS = false;
	public static final boolean GRID = true;
}
