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
package oripa.util.file;

import java.lang.invoke.MethodHandles;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author OUCHI Koji
 *
 */
public class ExtensionCorrector {
	final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	/**
	 * this method does not change {@code path}.
	 *
	 * @param path
	 * @param extensions
	 *            example: "png"
	 * @return path string with new extension
	 */
	public String correct(final String path, final String[] extensions) {

		logger.debug("extensions[0] for correction: {}", extensions[0]);
		if (List.of(extensions).stream()
				.noneMatch(ext -> path.endsWith("." + ext))) {
			return replaceExtension(path, extensions[0]);
		}

		return path;
	}

	private String replaceExtension(final String path, final String ext) {
		// drop the old extension and
		// append the new extension
		return path.replaceAll("\\.\\w+$", "." + ext);
	}

}
