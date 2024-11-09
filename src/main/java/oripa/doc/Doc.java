/**
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2005-2009 Jun Mitani http://mitani.cs.tsukuba.ac.jp/

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

package oripa.doc;

import java.io.File;

import oripa.domain.docprop.Property;
import oripa.domain.docprop.PropertyHolder;

/**
 * Manages project data.
 *
 * @author Koji
 *
 */
public class Doc implements PropertyHolder {

	/**
	 * Project property
	 */
	private Property property = new Property();

	private String dataFilePath = "";

	public Doc() {
	}

	public Doc(final Property property, final String filePath) {
		setProperty(property);
		setDataFilePath(filePath);
	}

	public void setDataFilePath(final String path) {
		dataFilePath = path;
	}

	public String getDataFilePath() {
		return dataFilePath;
	}

	public String getDataFileName() {
		File file = new File(dataFilePath);
		String fileName = file.getName();

		return fileName;

	}

	/**
	 * @return property
	 */
	@Override
	public Property getProperty() {
		return property;
	}

	/**
	 * @param property
	 *            Sets property
	 */
	@Override
	public void setProperty(final Property property) {
		this.property = property;
	}

}