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
import java.util.NavigableMap;
import java.util.NavigableSet;
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

	/**
	 * Returns a shallow copy of the specified range of the given list. The copy
	 * is modifiable.
	 *
	 * @param fromIndex
	 *            inclusive.
	 * @param toIndex
	 *            exclusive.
	 */
	public static <T> List<T> partialCopy(final List<T> list, final int fromIndex, final int toIndex) {
		return new ArrayList<T>(list.subList(fromIndex, toIndex));
	}

	/**
	 * Returns a view of the portion of the given map whose keys are greater
	 * than or equal to {@code fromKey} and strictly less than {@code toKey}.
	 * Changes in the returned map are reflected in the given map, and
	 * vice-versa.
	 *
	 * @param <K>
	 * @param <V>
	 * @param map
	 * @param fromKey
	 *            inclusive.
	 * @param toKey
	 *            exclusive.
	 * @return
	 */
	public static <K, V> NavigableMap<K, V> rangeMap(final NavigableMap<K, V> map,
			final K fromKey, final K toKey) {
		return map.subMap(fromKey, true, toKey, false);
	}

	/**
	 * Returns a view of the portion of the given map whose keys are greater
	 * than or equal to {@code fromKey} and less than or equal to {@code toKey}.
	 * Changes in the returned map are reflected in the given map, and
	 * vice-versa.
	 *
	 * @param <K>
	 * @param <V>
	 * @param map
	 * @param fromKey
	 *            inclusive.
	 * @param toKey
	 *            inclusive.
	 * @return
	 */
	public static <K, V> NavigableMap<K, V> rangeMapInclusive(final NavigableMap<K, V> map,
			final K fromKey, final K toKey) {
		return map.subMap(fromKey, true, toKey, true);
	}

	/**
	 * Returns a view of the portion of the given map whose keys are greater
	 * than {@code fromKey} and less than {@code toKey}. Changes in the returned
	 * map are reflected in the given map, and vice-versa.
	 *
	 * @param <K>
	 * @param <V>
	 * @param map
	 * @param fromKey
	 *            exclusive.
	 * @param toKey
	 *            exclusive.
	 * @return
	 */
	public static <K, V> NavigableMap<K, V> rangeMapExclusive(final NavigableMap<K, V> map,
			final K fromKey, final K toKey) {
		return map.subMap(fromKey, false, toKey, false);
	}

	/**
	 * Returns a view of the portion of the given set whose values are greater
	 * than or equal to {@code from} and less than or equal to {@code to}.
	 * Changes in the returned set are reflected in the given set, and
	 * vice-versa.
	 *
	 * @param <T>
	 * @param set
	 * @param from
	 *            inclusive.
	 * @param to
	 *            inclusive.
	 * @return
	 */
	public static <T> NavigableSet<T> rangeSetInclusive(final NavigableSet<T> set,
			final T from, final T to) {
		return set.subSet(from, true, to, true);
	}
}
