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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import oripa.persistence.filetool.FileAccessSupport;
import oripa.persistence.filetool.FileTypeProperty;
import oripa.persistence.filetool.MultiTypeAcceptableFileLoadingSupport;
import oripa.resource.ResourceHolder;
import oripa.resource.ResourceKey;

/**
 * A template for managing available filters for file access. Typical usage is:
 * 1) create filters in constructor, and 2) return the filters in getFilters().
 *
 * @author OUCHI Koji
 *
 */
public abstract class AbstractFileAccessSupportSelector<Data> {
	private final ResourceHolder resourceHolder = ResourceHolder.getInstance();

	/**
	 *
	 * @return available filters.
	 */
	protected abstract SortedMap<FileTypeProperty<Data>, FileAccessSupport<Data>> getFileAccessSupports();

	/**
	 * A utility method for creating filter. This method provides an explanation
	 * text for dialog.
	 *
	 * @param fileTypeKey
	 * @param resourceKey
	 * @return explanation text for dialog.
	 */
	protected String createDescription(final FileTypeProperty<Data> fileTypeKey,
			final String resourceKey) {
		return createDefaultDescription(fileTypeKey,
				resourceHolder.getString(ResourceKey.LABEL, resourceKey));

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
		builder.append("(");
		builder.append("*.");
		builder.append(String.join(",*.", extensions));
		builder.append(") ");
		builder.append(explanation);

		return builder.toString();
	}

	/**
	 * Creates and puts a filter for given file type key with given description
	 * to filter map obtained by {@link #getFileAccessSupports()}.
	 *
	 * @param key
	 * @param desctiption
	 */
	protected void putFileAccessSupport(final FileTypeProperty<Data> key, final String desctiption) {
		this.putFileAccessSupport(key, new FileAccessSupport<Data>(key, desctiption));
	}

	/**
	 *
	 * @param key
	 *            A value that describes the file type you want.
	 * @return A filter for given key.
	 */
	public FileAccessSupport<Data> getFileAccessSupport(final FileTypeProperty<Data> key) {
		return getFileAccessSupports().get(key);
	}

	/**
	 *
	 * @param key
	 *            A value that describes the file type you want.
	 * @param filter
	 *            A filter to be set.
	 * @return The previous filter for given key.
	 */

	public FileAccessSupport<Data> putFileAccessSupport(final FileTypeProperty<Data> key,
			final FileAccessSupport<Data> filter) {
		return getFileAccessSupports().put(key, filter);
	}

	/**
	 *
	 * @return all filters in this instance.
	 */
	public FileAccessSupport<Data>[] toArray() {
		@SuppressWarnings("unchecked")
		FileAccessSupport<Data>[] array = new FileAccessSupport[getFileAccessSupports()
				.size()];

		return getFileAccessSupports().values().toArray(array);
	}

	/**
	 *
	 * @return filters that can load data from a file.
	 */
	public List<FileAccessSupport<Data>> getLoadablesWithMultiType() {
		var loadables = getLoadables();

		var multi = new MultiTypeAcceptableFileLoadingSupport<Data>(
				loadables, "Any type");
		loadables.add(multi);

		Collections.sort(loadables);

		return loadables;
	}

	public List<FileAccessSupport<Data>> getLoadables() {
		return getFileAccessSupports().values().stream()
				.filter(f -> f.getLoadingAction() != null)
				.sorted()
				.collect(Collectors.toList());
	}

	public List<FileTypeProperty<Data>> getTargetTypes(final Collection<FileAccessSupport<Data>> supports) {
		return supports.stream()
				.map(support -> support.getTargetType())
				.collect(Collectors.toList());
	}

	public FileAccessSupport<Data> findFirst(
			final Collection<FileAccessSupport<Data>> supports,
			final FileTypeProperty<Data> type) {
		return supports.stream()
				.filter(support -> support.getTargetType().equals(type))
				.findFirst()
				.get();
	}

	/**
	 * @param path
	 * @return a filter that can load the file at the path.
	 * @throws IllegalArgumentException
	 *             No filter is available for the given path. Or, the path is
	 *             null or is for a directory.
	 */
	public FileAccessSupport<Data> getLoadableOf(final String path)
			throws IllegalArgumentException {
		if (path == null) {
			throw new IllegalArgumentException("Wrong path (null)");
		}

		File file = new File(path);
		if (file.isDirectory()) {
			throw new IllegalArgumentException("The path is for directory.");
		}

		return Stream.of(toArray())
				.filter(f -> f.getTargetType().extensionsMatch(nullableCanonicalPath(file))
						&& f.getLoadingAction() != null)
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException(
						"cannot load the file with the extension."));
	}

	private String nullableCanonicalPath(final File file) {
		try {
			return file.getCanonicalPath();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 *
	 * @return filters that can save a data object.
	 */
	public List<FileAccessSupport<Data>> getSavables() {
		return getFileAccessSupports().values().stream()
				.filter(f -> f.getSavingAction() != null)
				.sorted()
				.collect(Collectors.toList());
	}

}