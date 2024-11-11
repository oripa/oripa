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

import java.util.stream.Stream;

import oripa.persistence.filetool.FileAccessSupport;
import oripa.persistence.filetool.FileTypeProperty;
import oripa.resource.ResourceHolder;
import oripa.resource.ResourceKey;

/**
 * @author OUCHI Koji
 *
 */
public class FileAccessSupportFactory<Data> {
	private final ResourceHolder resourceHolder = ResourceHolder.getInstance();

	/**
	 * Creates a support object for given file type key with an explanation text
	 * obtained via given label resource key.
	 *
	 * @param key
	 * @param description
	 */
	public FileAccessSupport<Data> createFileAccessSupport(final FileTypeProperty<Data> key,
			final String labelResourceKey, final String... appendings) {

		var description = createDescription(key, labelResourceKey, appendings);

		return new FileAccessSupport<Data>(key, description);
	}

	/**
	 * A utility method for creating support object. This method provides an
	 * explanation text for dialog.
	 *
	 * @param fileTypeKey
	 * @param resourceKey
	 * @return explanation text for dialog.
	 */
	protected String createDescription(final FileTypeProperty<Data> fileTypeKey,
			final String resourceKey, final String... appendings) {
		var description = createDefaultDescription(fileTypeKey,
				resourceHolder.getString(ResourceKey.LABEL, resourceKey));

		return Stream.of(appendings)
				.reduce(description, String::concat);
	}

	/**
	 *
	 * @param type
	 *            file type
	 * @param explanation
	 * @return in the style of "(*.extension1, *.extension2, ...)
	 *         ${explanation}"
	 */
	protected String createDefaultDescription(final FileTypeProperty<?> type,
			final String explanation) {
		String[] extensions = type.getExtensions();

		StringBuilder builder = new StringBuilder();
		builder.append("(")
				.append("*.")
				.append(String.join(",*.", extensions))
				.append(") ")
				.append(explanation);

		return builder.toString();
	}

}
