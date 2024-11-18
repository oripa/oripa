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
package oripa.gui.view.util;

import java.awt.Color;

/**
 * @author OUCHI Koji
 *
 */
public class ColorUtil {
	/**
	 *
	 * @param color
	 * @return color code in the style of "#000000"
	 */
	public static String convertColorToCode(final Color color) {
		return String.format("#%06X", color.getRGB() & 0x00FFFFFF);
	}

	public static Color convertCodeToColor(final String code) {
		if (code == null) {
			return null;
		}

		try {
			return new Color(Integer.decode(code));
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
