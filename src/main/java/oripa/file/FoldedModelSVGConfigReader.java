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
package oripa.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import oripa.persistence.entity.exporter.FoldedModelSVGConfig;
import oripa.persistence.filetool.WrongDataFormatException;

/**
 * @author OUCHI Koji
 *
 */
public class FoldedModelSVGConfigReader {
	public FoldedModelSVGConfig read(final String path) throws IOException, WrongDataFormatException {
		var gson = new Gson();
		try {
			return gson.fromJson(
					Files.readString(Paths.get(path)),
					FoldedModelSVGConfig.class);
		} catch (JsonSyntaxException e) {
			throw new WrongDataFormatException(
					"The file does not follow JSON style.",
					e);
		}
	}
}
