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
package oripa.gui.presenter.main;

import oripa.domain.docprop.Property;
import oripa.domain.docprop.PropertyHolder;
import oripa.gui.view.main.PropertyDialogView;

/**
 * @author OUCHI Koji
 *
 */
public class PropertyDialogPresenter {

	private final PropertyDialogView view;

	private final PropertyHolder propertyHolder;

	public PropertyDialogPresenter(final PropertyDialogView view, final PropertyHolder propertyHolder) {
		this.view = view;
		this.propertyHolder = propertyHolder;

		view.addOKButtonListener(this::setValuesToDomain);

		setValuesToView();
	}

	private void setValuesToView() {
		var property = propertyHolder.getProperty();

		view.setModelTitle(property.getTitle());
		view.setEditorName(property.getEditorName());
		view.setOriginalAutor(property.getOriginalAuthorName());
		view.setReference(property.getReference());
		view.setMemo(property.getMemo());
	}

	private void setValuesToDomain() {
		var prop = new Property();

		prop.setTitle(view.getModelTitle());
		prop.setEditorName(view.getEditorName());
		prop.setOriginalAuthorName(view.getOriginalAuthor());
		prop.setReference(view.getReference());
		prop.setMemo(view.getMemo());

		propertyHolder.setProperty(prop);
	}

	public void setViewVisible(final boolean visible) {
		view.setVisible(visible);
	}
}
