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

package oripa.project;

import java.io.File;
import java.util.List;
import java.util.Optional;

import oripa.domain.projectprop.Property;
import oripa.domain.projectprop.PropertyHolder;
import oripa.persistence.doc.Doc;
import oripa.persistence.doc.DocFileTypes;
import oripa.persistence.filetool.FileTypeProperty;

/**
 * Manages project data.
 *
 * @author OUCHI Koji
 *
 */
public class Project implements PropertyHolder {

	/**
	 * Project property
	 */
	private Property property = new Property();

	private String dataFilePath = "";

	private static final List<FileTypeProperty<Doc>> projectFileTypes = List.of(
			DocFileTypes.opx(),
			DocFileTypes.fold());

	public static List<FileTypeProperty<Doc>> projectFileTypes() {
		return projectFileTypes;
	}

	public static boolean projectFileTypeMatch(final String path) {
		if (path == null || path.isEmpty()) {
			return false;
		}
		return projectFileTypes.stream().anyMatch(type -> type.extensionsMatch(path));
	}

	public Project() {
	}

	public Project(final Property property, final String filePath) {
		setProperty(property);
		dataFilePath = filePath;
	}

	public String getDataFilePath() {
		return dataFilePath;
	}

	public String getDataFileName() {
		File file = new File(dataFilePath);
		String fileName = file.getName();

		return fileName;

	}

	public Optional<FileTypeProperty<Doc>> getProjectFileType() {
		return projectFileTypes.stream()
				.filter(type -> type.extensionsMatch(dataFilePath))
				.findFirst();
	}

	public boolean isProjectFile() {
		return projectFileTypeMatch(dataFilePath);
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