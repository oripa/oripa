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
package oripa.util.gui;

import java.util.LinkedList;

import javax.swing.JFrame;

/**
 * @author Koji
 *
 */
public class ChildFrameList {

	private LinkedList<JFrame> childFrames = new LinkedList<>();;

	public void addChild(JFrame frame) {
		childFrames.add(frame);
	}

	public void remove(JFrame frame) {
		childFrames.remove(frame);
	}

	public void clear() {
		closeAll();
		childFrames.clear();
	}
	
	public void closeAll() {
		for (JFrame frame : childFrames) {
			// TODO make all frames short-life object
			// then change the following to dispose()
			frame.setVisible(false);
		}
	}
}
