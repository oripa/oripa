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
package oripa.view.main;

import javax.swing.JFrame;

import oripa.doc.Doc;
import oripa.doc.Property;

/**
 * @author Koji
 * 
 */
public class PropertyDialog extends AbstractPropertyDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4194441379667838528L;
	private final Doc document;

	/**
	 * Constructor
	 */
	public PropertyDialog(JFrame parent, Doc doc) {
		super(parent, doc.getProperty());
		document = doc;
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see
	 * oripa.view.main.AbstractPropertyDialog#onClickOKButton(oripa.persistent
	 * .doc.Property)
	 */
	@Override
	protected void onClickOKButton(Property newProperty) {
		document.setProperty(newProperty);
	}

}
