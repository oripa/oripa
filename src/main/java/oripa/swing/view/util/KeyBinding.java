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

import java.util.HashSet;
import java.util.Set;

import javax.swing.KeyStroke;

/**
 * Manages which key is used for shortcut. This is to work with both JButton's
 * inputMap and JMenuItem's accelerator with independent key bindings. Throws
 * exception if given key is already used.
 *
 * @author OUCHI Koji
 *
 */
public class KeyBinding {
    private final Set<KeyStroke> usedStrokes = new HashSet<>();

    /**
     * Gets the key stroke for the given key code and marks as used.
     *
     * @param keyCode
     *            a value of KeyEvent.VK_A, VK_B,..., and so on.
     * @return a key stroke.
     */
    public KeyStroke use(final int keyCode) {
        return use(KeyStrokes.get(keyCode));
    }

    private KeyStroke use(final KeyStroke stroke) {

        if (usedStrokes.contains(stroke)) {
            throw new IllegalArgumentException("the given key is already used.");
        }
        usedStrokes.add(stroke);

        return stroke;
    }

    /**
     * Gets the key stroke for the given key code with shift key and marks as
     * used.
     *
     * @param keyCode
     *            a value of KeyEvent.VK_A, VK_B,..., and so on.
     * @return a key stroke with shift key down.
     */
    public KeyStroke useWithShiftDown(final int keyCode) {
        return use(KeyStrokes.getWithControlShiftDown(keyCode));
    }

    /**
     * Gets the key stroke for the given key code with control key and marks as
     * used.
     *
     * @param keyCode
     *            a value of KeyEvent.VK_A, VK_B,..., and so on.
     * @return a key stroke with control key down.
     */
    public KeyStroke useWithControlDown(final int keyCode) {
        return use(KeyStrokes.getWithControlDown(keyCode));
    }

    /**
     * Gets the key stroke for the given key code with control key and shift key
     * and marks as used.
     *
     * @param keyCode
     *            a value of KeyEvent.VK_A, VK_B,..., and so on.
     * @return a key stroke with control key and shift key down.
     */
    public KeyStroke useWithControlShiftDown(final int keyCode) {
        return use(KeyStrokes.getWithControlShiftDown(keyCode));
    }

}
