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

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author OUCHI Koji
 *
 */
public class MultiTypeAcceptableFileLoadingSupport<Data>
		extends FileAccessSupport<Data> {

	private static final Logger logger = LoggerFactory
			.getLogger(MultiTypeAcceptableFileLoadingSupport.class);

	private final Collection<FileAccessSupport<Data>> fileAccessSupports;

	/**
	 *
	 * Constructor.
	 *
	 * @param supports
	 *            filters whose loading action objects are not null. Filters
	 *            will be copied to this object.
	 * @param msg
	 *            message in filter box
	 * @throws IllegalArgumentException
	 *             if a loading action of a filter is null.
	 */
	public MultiTypeAcceptableFileLoadingSupport(
			final Collection<FileAccessSupport<Data>> supports,
			final String msg) throws IllegalArgumentException {

		super(new MultiTypeProperty<Data>(
				supports.stream()
						.map(support -> support.getTargetType())
						.toList()),
				msg);

		supports.forEach(support -> {
			if (support.getLoadingAction() == null) {
				throw new IllegalArgumentException("file access support should have a loadingAction.");
			}
		});

		fileAccessSupports = new ArrayList<>(supports);
	}

	@Deprecated
	@Override
	public LoadingAction<Data> getLoadingAction() {
		return null;
	}

	@Deprecated
	@Override
	public SavingAction<Data> getSavingAction() {
		return null;
	}

	/**
	 *
	 * @return acceptable extensions
	 */
	@Override
	public String[] getExtensions() {
		return fileAccessSupports.stream()
				.flatMap(support -> Stream.of(support.getExtensions()))
				.toList()
				.toArray(new String[0]);
	}
}
