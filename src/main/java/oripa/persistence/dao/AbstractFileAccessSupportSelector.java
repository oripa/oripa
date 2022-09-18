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
import java.util.function.Supplier;
import java.util.stream.Collectors;

import oripa.persistence.filetool.FileAccessSupport;
import oripa.persistence.filetool.FileTypeProperty;
import oripa.persistence.filetool.MultiTypeAcceptableFileLoadingSupport;
import oripa.resource.ResourceHolder;
import oripa.resource.ResourceKey;

/**
 * A template for managing available file access support objects. Typical usage
 * is: 1) create supports in constructor, and 2) return the filters in
 * {@link #getFileAccessSupports()}
 *
 * @author OUCHI Koji
 *
 */
public abstract class AbstractFileAccessSupportSelector<Data> {
	private final ResourceHolder resourceHolder = ResourceHolder.getInstance();

	/**
	 *
	 * @return available supports.
	 */
	protected abstract SortedMap<FileTypeProperty<Data>, FileAccessSupport<Data>> getFileAccessSupports();

	/**
	 * A utility method for creating support object. This method provides an
	 * explanation text for dialog.
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
	 * Creates and puts a support object for given file type key with given
	 * description to a map obtained by {@link #getFileAccessSupports()}.
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
	 * @return A support object for given key.
	 */
	public FileAccessSupport<Data> getFileAccessSupport(final FileTypeProperty<Data> key) {
		return getFileAccessSupports().get(key);
	}

	/**
	 *
	 * @param key
	 *            A value that describes the file type you want.
	 * @param support
	 *            A support object to be set.
	 * @return The previous support object for given key.
	 */

	public FileAccessSupport<Data> putFileAccessSupport(final FileTypeProperty<Data> key,
			final FileAccessSupport<Data> support) {
		return getFileAccessSupports().put(key, support);
	}

	/**
	 *
	 * @return support objects that can load data from a file, including a
	 *         support object accepting all available types.
	 */
	public List<FileAccessSupport<Data>> getLoadablesWithMultiType() {
		var loadables = getLoadables();

		var multi = new MultiTypeAcceptableFileLoadingSupport<Data>(
				loadables, "Any type");
		loadables.add(multi);

		Collections.sort(loadables);

		return loadables;
	}

	/**
	 *
	 * @return support objects that can load data from a file.
	 */
	public List<FileAccessSupport<Data>> getLoadables() {
		return getFileAccessSupports().values().stream()
				.filter(support -> support.getLoadingAction() != null)
				.sorted()
				.collect(Collectors.toList());
	}

	public List<FileTypeProperty<Data>> getTargetTypes(final Collection<FileAccessSupport<Data>> supports) {
		return supports.stream()
				.map(support -> support.getTargetType())
				.collect(Collectors.toList());
	}

	/**
	 * @param path
	 * @return a support object that can load the file at the path.
	 * @throws IllegalArgumentException
	 *             No support object is available for the given path. Or, the
	 *             path is null or is for a directory.
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

		return find(getLoadables(), nullableCanonicalPath(file),
				() -> new IllegalArgumentException(
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
	 * @return support objects that can save a data object.
	 */
	public List<FileAccessSupport<Data>> getSavables() {
		return getFileAccessSupports().values().stream()
				.filter(support -> support.getSavingAction() != null)
				.sorted()
				.collect(Collectors.toList());
	}

	/**
	 *
	 * @param path
	 *            file path to save
	 * @return A support object that can save a data object.
	 */
	public FileAccessSupport<Data> getSavableOf(final String path) {
		return find(getSavables(), path,
				() -> new IllegalArgumentException(
						"The file type guessed from the extension is not supported."));
	}

	private FileAccessSupport<Data> find(final List<FileAccessSupport<Data>> supports, final String path,
			final Supplier<IllegalArgumentException> exceptionSupplier) {
		return supports.stream()
				.filter(support -> support.getTargetType().extensionsMatch(path))
				.findFirst()
				.orElseThrow(exceptionSupplier);
	}
}