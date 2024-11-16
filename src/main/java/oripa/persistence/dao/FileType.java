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
package oripa.persistence.dao;

import oripa.persistence.filetool.FileTypeProperty;

/**
 * This is a value object. {@code equals()} and {@code hashCode()} uses the
 * given {@code property}'s ones.
 *
 * @author OUCHI Koji
 *
 */
public class FileType<Data> {
	private final FileTypeProperty<Data> property;

	public FileType(final FileTypeProperty<Data> property) {
		this.property = property;
	}

	FileTypeProperty<Data> getFileTypeProperty() {
		return property;
	}

	public String[] getExtensions() {
		return property.getExtensions();
	}

	public boolean extensionsMatch(final String path) {
		return property.extensionsMatch(path);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof FileType f) {
			return property.equals(f.getFileTypeProperty());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return property.hashCode();
	}
}
