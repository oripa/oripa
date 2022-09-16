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
package oripa.swing.view.util;

import java.awt.event.InputEvent;

import javax.swing.KeyStroke;

/**
 * KeyStroke utility
 *
 * @author OUCHI Koji
 *
 */
public class KeyStrokes {

	/**
	 * Gets the key stroke for the given key code.
	 *
	 * @param keyCode
	 *            a value of KeyEvent.VK_A, VK_B,..., and so on.
	 * @return a key stroke.
	 */
	public static KeyStroke get(final int keyCode) {
		return KeyStroke.getKeyStroke(keyCode, 0);
	}

	/**
	 * Gets the key stroke for the given key code with shift key.
	 *
	 * @param keyCode
	 *            a value of KeyEvent.VK_A, VK_B,..., and so on.
	 * @return a key stroke with shift key down.
	 */
	public static KeyStroke getWithShiftDown(final int keyCode) {
		return KeyStroke.getKeyStroke(keyCode, InputEvent.SHIFT_DOWN_MASK);
	}

	/**
	 * Gets the key stroke for the given key code with control key.
	 *
	 * @param keyCode
	 *            a value of KeyEvent.VK_A, VK_B,..., and so on.
	 * @return a key stroke with control key down.
	 */
	public static KeyStroke getWithControlDown(final int keyCode) {
		return KeyStroke.getKeyStroke(keyCode, InputEvent.CTRL_DOWN_MASK);
	}
}
