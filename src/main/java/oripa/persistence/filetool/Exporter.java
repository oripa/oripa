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
package oripa.persistence.filetool;

import java.io.IOException;

/**
 * @author Koji
 *
 */
public interface Exporter<Data> {
	/**
	 *
	 * @param data
	 * @param filePath
	 * @return true if the action succeeds, otherwise false.
	 * @throws IOException
	 *             Error on file access.
	 * @throws IllegalArgumentException
	 *             thrown if the {@code data} cannot be converted to the aimed
	 *             data format.
	 */
	public abstract boolean export(Data data, String filePath)
			throws IOException, IllegalArgumentException;
}
