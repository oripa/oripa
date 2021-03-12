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
package oripa.persistent.filetool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author OUCHI Koji
 *
 */
public class MultiTypeAcceptableFileLoadingFilter<Data>
		extends FileAccessSupportFilter<Data> {

	private static final Logger logger = LoggerFactory
			.getLogger(MultiTypeAcceptableFileLoadingFilter.class);

	private final Collection<FileAccessSupportFilter<Data>> filters;

	/**
	 *
	 * Constructor.
	 *
	 * @param filters
	 *            filters whose loading action objects are not null. Filters
	 *            will be copied to this object.
	 * @param msg
	 *            message in filter box
	 * @throws IllegalArgumentException
	 *             if a loading action of a filter is null.
	 */
	public MultiTypeAcceptableFileLoadingFilter(
			final Collection<FileAccessSupportFilter<Data>> filters,
			final String msg) throws IllegalArgumentException {

		super(new MultiTypeProperty<Data>(
				filters.stream()
						.map(f -> f.getTargetType())
						.collect(Collectors.toList())),
				msg);

		filters.forEach(filter -> {
			if (filter.getLoadingAction() == null) {
				throw new IllegalArgumentException("filter should have a loadingAction.");
			}
		});

		this.filters = new ArrayList<>(filters);
	}

	@Deprecated
	@Override
	public AbstractLoadingAction<Data> getLoadingAction() {
		return null;
	}

	@Deprecated
	@Override
	public AbstractSavingAction<Data> getSavingAction() {
		return null;
	}

	/**
	 *
	 * @return acceptable extensions
	 */
	@Override
	public String[] getExtensions() {
		return filters.stream()
				.flatMap(filter -> Arrays.asList(filter.getExtensions()).stream())
				.collect(Collectors.toList()).toArray(new String[0]);
	}

	/**
	 *
	 * @param filePath
	 * @return loading action for the given {@code filePath}.
	 * @throws IllegalArgumentException
	 *             if no loading action is for the given {@code filePath}.
	 */
	public AbstractLoadingAction<Data> getLoadingAction(final String filePath)
			throws IllegalArgumentException {
		return filters.stream()
				.filter(f -> f.getTargetType().extensionsMatch(filePath))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No filter for the given path."))
				.getLoadingAction();
	}
}
