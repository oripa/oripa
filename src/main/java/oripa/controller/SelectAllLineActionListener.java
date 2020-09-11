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
package oripa.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import oripa.domain.paint.PaintContextInterface;

/**
 * @author OUCHI Koji
 *
 */
public class SelectAllLineActionListener implements ActionListener {
	private final PaintContextInterface context;

	/**
	 * Constructor
	 */
	public SelectAllLineActionListener(final PaintContextInterface context) {
		this.context = context;
	}

	/*
	 * (non Javadoc)
	 *
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(final ActionEvent e) {
		context.creasePatternUndo().pushUndoInfo();
		context.getPainter().selectAllOriLines();
		context.getCreasePattern().stream()
				.filter(l -> l.selected).forEach(l -> context.pushLine(l));
	}

}
