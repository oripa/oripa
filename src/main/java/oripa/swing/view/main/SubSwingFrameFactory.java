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
package oripa.swing.view.main;

import java.beans.PropertyChangeListener;

import oripa.gui.view.FrameView;
import oripa.gui.view.foldability.FoldabilityCheckFrameFactory;
import oripa.gui.view.foldability.FoldabilityCheckFrameView;
import oripa.gui.view.main.SubFrameFactory;
import oripa.gui.view.model.ModelViewFrameFactory;
import oripa.gui.view.model.ModelViewFrameView;

/**
 * @author OUCHI Koji
 *
 */
public class SubSwingFrameFactory implements SubFrameFactory {

	private final FoldabilityCheckFrameFactory foldabilityFrameFactory;
	private final ModelViewFrameFactory modelViewFrameFactory;

	public SubSwingFrameFactory(
			final FoldabilityCheckFrameFactory foldaFrameFactory,
			final ModelViewFrameFactory modelViewFrameFactory) {

		this.foldabilityFrameFactory = foldaFrameFactory;
		this.modelViewFrameFactory = modelViewFrameFactory;
	}

	@Override
	public FoldabilityCheckFrameView createFoldabilityFrame(final FrameView parent) {
		return foldabilityFrameFactory.createFrame(parent);
	}

	@Override
	public ModelViewFrameView createModelViewFrame(final FrameView parent,
			final PropertyChangeListener onChangePaperDomain) {

		return modelViewFrameFactory.createFrame(parent, onChangePaperDomain);
	}

}
