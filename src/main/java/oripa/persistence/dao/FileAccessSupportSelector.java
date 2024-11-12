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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.SortedMap;
import java.util.function.Supplier;

import oripa.persistence.filetool.FileAccessSupport;
import oripa.persistence.filetool.FileTypeProperty;
import oripa.persistence.filetool.MultiTypeAcceptableFileLoadingSupport;
import oripa.util.file.FileFactory;

/**
 * manages available file access support objects.
 *
 * @author OUCHI Koji
 *
 */
public class FileAccessSupportSelector<Data> {
	private final SortedMap<FileTypeProperty<Data>, FileAccessSupport<Data>> fileAccessSupports;
	private final FileFactory fileFactory;

	public FileAccessSupportSelector(
			final SortedMap<FileTypeProperty<Data>, FileAccessSupport<Data>> supports,
			final FileFactory fileFactory) {
		this.fileAccessSupports = supports;
		this.fileFactory = fileFactory;
	}

	/**
	 *
	 * @param key
	 *            A value that describes the file type you want.
	 * @return A support object for given key. Empty if no support for the key.
	 */
	public Optional<FileAccessSupport<Data>> getFileAccessSupport(final FileTypeProperty<Data> key) {
		return Optional.ofNullable(fileAccessSupports.get(key));
	}

	/**
	 *
	 * @return support objects that can load data from a file, including a
	 *         support object accepting all available types. empty if no support
	 *         is available for loading.
	 */
	public List<FileAccessSupport<Data>> getLoadablesWithMultiType() {
		var loadables = new ArrayList<>(getLoadables());

		if (loadables.isEmpty()) {
			return List.of();
		}

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
		return fileAccessSupports.values().stream()
				.filter(support -> support.getLoadingAction() != null)
				.sorted()
				.toList();
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

		var file = fileFactory.create(path);
		if (file.isDirectory()) {
			throw new IllegalArgumentException("The path is for directory.");
		}

		return find(getLoadables(), nullableCanonicalPath(file),
				() -> new IllegalArgumentException(
						"cannot load the file with the extension. " + file.getPath()));
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
		return fileAccessSupports.values().stream()
				.filter(support -> support.getSavingAction() != null)
				.sorted()
				.toList();
	}

	public List<FileAccessSupport<Data>> getSavablesOf(final Collection<FileTypeProperty<Data>> types) {
		return getSavables().stream()
				.filter(support -> types.contains(support.getTargetType()))
				.toList();
	}

	/**
	 *
	 * @param path
	 *            file path to save
	 * @return A support object that can save a data object.
	 */
	public FileAccessSupport<Data> getSavableOf(final String path) {
		if (path == null) {
			throw new IllegalArgumentException("path should not be null.");
		}

		return find(getSavables(), path,
				() -> new IllegalArgumentException(
						"The file type guessed from the extension is not supported."));
	}

	private FileAccessSupport<Data> find(final List<FileAccessSupport<Data>> supports, final String path,
			final Supplier<IllegalArgumentException> exceptionSupplier) {
		return supports.stream()
				.filter(support -> support.extensionsMatch(path))
				.findFirst()
				.orElseThrow(exceptionSupplier);
	}
}