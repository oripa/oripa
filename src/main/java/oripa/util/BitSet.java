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
package oripa.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Fixed length bit set.
 *
 * @author OUCHI Koji
 *
 */
public class BitSet implements Set<Integer> {
	private final BitArray bits;

	private class BitSetIterator implements Iterator<Integer> {
		private int index = 0;

		private final int count;
		private int usedCount;

		public BitSetIterator() {
			index = 0;
			count = size();
			usedCount = 0;
		}

		@Override
		public boolean hasNext() {
			return usedCount < count;
		}

		@Override
		public Integer next() {
			while (!bits.get(index)) {
				index++;
			}
			usedCount++;
			return index;
		}

	}

	public BitSet(final int bitLength) {
		bits = new BitArray(bitLength);
	}

	@Override
	public int size() {
		return bits.countOnes();
	}

	@Override
	public boolean isEmpty() {
		return bits.allZero();
	}

	@Override
	public boolean contains(final Object o) {
		if (o instanceof Integer i) {
			return bits.get(i);
		}
		return false;
	}

	@Override
	public Iterator<Integer> iterator() {
		return new BitSetIterator();
	}

	@Override
	public Object[] toArray() {
		var array = new Object[size()];

		int i = 0;
		for (int index = 0; index < bits.bitLength; index++) {
			if (bits.get(index)) {
				array[i] = index;
				i++;
			}
		}

		return array;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(final T[] a) {
		var size_ = size();
		var array = a.length >= size_ ? a : new Object[size_];

		int i = 0;
		for (int index = 0; index < bits.bitLength; index++) {
			if (bits.get(index)) {
				array[i] = index;
				i++;
			}
		}
		for (int j = size_; j < array.length; j++) {
			array[j] = null;
		}

		return (T[]) array;
	}

	@Override
	public boolean add(final Integer e) {
		return bits.setOneAndGetChange(e);
	}

	public synchronized boolean addSync(final Integer e) {
		return bits.setOneAndGetChange(e);
	}

	@Override
	public boolean remove(final Object o) {
		if (o instanceof Integer i) {
			return bits.setZeroAndGetChange(i);
		}
		return false;
	}

	@Override
	public boolean containsAll(final Collection<?> c) {
		if (c instanceof BitSet other) {
			return bits.and(other.bits).equals(other.bits);
		}

		boolean result = true;
		for (var obj : c) {
			var index = (Integer) obj;
			result &= bits.get(index);
		}

		return result;
	}

	@Override
	public boolean addAll(final Collection<? extends Integer> c) {
		if (c instanceof BitSet other) {
			var newBits = bits.or(other.bits);
			return updateBits(newBits);
		}

		boolean changed = false;

		for (var obj : c) {
			var index = obj;
			changed |= !bits.get(index);
			bits.setOne(index);
		}

		return changed;
	}

	@Override
	public boolean retainAll(final Collection<?> c) {
		if (c instanceof BitSet other) {
			var newBits = bits.and(other.bits);
			return updateBits(newBits);
		}

		boolean changed = false;

		for (int i = 0; i < bits.bitLength; i++) {
			if (bits.get(i)) {
				changed |= !c.contains(i);
				bits.setZero(i);
			}
		}

		return changed;
	}

	@Override
	public boolean removeAll(final Collection<?> c) {
		if (c instanceof BitSet other) {
			var newBits = bits.and(other.bits.not());
			return updateBits(newBits);
		}

		boolean changed = false;
		for (var obj : c) {
			var index = (Integer) obj;
			changed |= bits.get(index);
			bits.setZero(index);
		}

		return changed;
	}

	private boolean updateBits(final BitArray newBits) {
		if (newBits.equals(bits)) {
			return false;
		}
		bits.setAll(newBits);
		return true;
	}

	@Override
	public void clear() {
		bits.clear();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof BitSet bitSet) {
			return bits.equals(bitSet.bits);
		}

		if (obj instanceof Collection collection) {
			if (size() != collection.size()) {
				return false;
			}
			for (var v : collection) {
				if (v instanceof Integer index) {
					if (!contains(index)) {
						return false;
					}
				} else {
					return false;
				}
			}
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return bits.hashCode();
	}
}
