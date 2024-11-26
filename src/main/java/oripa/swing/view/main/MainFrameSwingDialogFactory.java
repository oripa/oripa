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

import jakarta.inject.Inject;
import oripa.gui.view.FrameView;
import oripa.gui.view.main.ArrayCopyDialogView;
import oripa.gui.view.main.CircleCopyDialogView;
import oripa.gui.view.main.MainFrameDialogFactory;
import oripa.gui.view.main.PropertyDialogView;

/**
 * @author OUCHI Koji
 *
 */
public class MainFrameSwingDialogFactory implements MainFrameDialogFactory {

	private final ArrayCopyDialogFactory arrayCopyDialogFactory;
	private final CircleCopyDialogFactory circleCopyDialogFactory;
	private final PropertyDialogFactory propertyDialogFactory;

	@Inject
	public MainFrameSwingDialogFactory(final ArrayCopyDialogFactory arrayCopyDialogFactory,
			final CircleCopyDialogFactory circleCopyDialogFactory,
			final PropertyDialogFactory propertyDialogFactory) {
		this.arrayCopyDialogFactory = arrayCopyDialogFactory;
		this.circleCopyDialogFactory = circleCopyDialogFactory;
		this.propertyDialogFactory = propertyDialogFactory;
	}

	@Override
	public ArrayCopyDialogView createArrayCopyDialog(final FrameView owner) {
		return arrayCopyDialogFactory.create(owner);
	}

	@Override
	public CircleCopyDialogView createCircleCopyDialog(final FrameView owner) {
		return circleCopyDialogFactory.create(owner);
	}

	@Override
	public PropertyDialogView createPropertyDialog(final FrameView parent) {
		return propertyDialogFactory.create(parent);
	}

}
