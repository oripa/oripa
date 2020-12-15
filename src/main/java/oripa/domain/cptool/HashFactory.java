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
package oripa.domain.cptool;

import java.util.ArrayList;
import java.util.function.Function;

/**
 * @author OUCHI Koji
 *
 */
class HashFactory {

	/**
	 * creates a hash table whose key is the value obtained by keyExtractor. the
	 * difference between the next key is strictly larger than eps.
	 *
	 * @param <T>
	 * @param sortedItems
	 * @param keyExtractor
	 * @param eps
	 * @return
	 */
	public <T> ArrayList<ArrayList<T>> create(
			final ArrayList<T> sortedItems,
			final Function<T, Double> keyExtractor,
			final double eps) {
		var hash = new ArrayList<ArrayList<T>>();

		int split_i = 0;
		hash.add(new ArrayList<T>());
		hash.get(split_i).add(sortedItems.get(0));
		for (int i = 1; i < sortedItems.size(); i++) {
			var line1 = sortedItems.get(i);
			var line0 = hash.get(split_i).get(0);
			if (keyExtractor.apply(line1) - keyExtractor.apply(line0) > eps) {
				split_i++;
				hash.add(new ArrayList<T>());
			}
			hash.get(split_i).add(line1);
		}

		return hash;
	}

}
