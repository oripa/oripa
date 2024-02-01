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
package oripa.util.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author OUCHI Koji
 *
 */
public class CollectionUtil {
	public static <T> T getCircular(final List<T> list, final int index) {
		return list.get((index + list.size()) % list.size());
	}

	public static <T> Set<T> newConcurrentHashSet() {
		return ConcurrentHashMap.newKeySet();
	}

	public static <T> Set<T> newConcurrentHashSet(final Collection<T> values) {
		Set<T> set = ConcurrentHashMap.newKeySet();
		set.addAll(values);

		return set;
	}

	public static <T> List<T> partialCopy(final List<T> list, final int fromIndex, final int toIndex) {
		return new ArrayList<T>(list.subList(fromIndex, toIndex));
	}
}
