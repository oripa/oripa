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
package oripa.domain.fold.foldability.ring;

import java.util.ArrayList;
import java.util.List;

import oripa.util.collection.CollectionUtil;

public class RingArrayList<Data> {

	private final List<RingElement<Data>> list = new ArrayList<>();
	private int headIndex = -1;
	private int tailIndex = -1;

	private int count = 0;

	private final List<Boolean> existences = new ArrayList<>();

	public RingArrayList(final List<Data> values) {
		for (int i = 0; i < values.size(); i++) {
			add(i, values.get(i));
		}

		headIndex = 0;
		tailIndex = values.size() - 1;
	}

	private void add(final int index, final Data value) {
		var element = new RingElement<>(index, value);

		list.add(element);
		existences.add(true);

		if (list.size() == 1) {
			element.setNext(element);
			element.setPrevious(element);

			count = 1;
			return;
		}

		var next = getElement(index + 1);
		var previous = getElement(index - 1);

		next.setPrevious(element);
		previous.setNext(element);

		element.setNext(next);
		element.setPrevious(previous);

		count++;
	}

	public Data dropConnection(final int index) {

		if (!exists(index)) {
			throw new IllegalArgumentException("does not exist.");
		}

		var element = getElement(index);
		existences.set(index, false);
		count--;

		if (list.size() == 2) {
			var other = element.getNext();

			other.setNext(other);
			other.setPrevious(other);

			if (index == headIndex) {
				headIndex = tailIndex;
			} else {
				tailIndex = headIndex;
			}

			return element.getValue();
		}

		var next = element.getNext();
		var previous = element.getPrevious();

		previous.setNext(next);
		next.setPrevious(previous);

		if (index == headIndex) {
			headIndex = next.getRingIndex();
		}
		if (index == tailIndex) {
			tailIndex = previous.getRingIndex();
		}

		return element.getValue();
	}

	public Data get(final int index) {
		return getElement(index).getValue();
	}

	public RingElement<Data> getElement(final int index) {
		if (!exists(index)) {
			throw new IllegalArgumentException("does not not exist.");
		}
		return CollectionUtil.getCircular(list, index);
	}

	public RingElement<Data> getNext(final int index) {
		return getElement(index).getNext();
	}

	public RingElement<Data> getPrevious(final int index) {
		return getElement(index).getPrevious();
	}

	public Data head() {
		return get(headIndex);
	}

	public Data tail() {
		return get(tailIndex);
	}

	public int size() {
		return count;
	}

	public boolean exists(final int index) {
		return CollectionUtil.getCircular(existences, index);
	}

	@Override
	public String toString() {
		var builder = new StringBuilder();

		for (int i = 0; i < list.size(); i++) {
			if (exists(i)) {
				builder.append(i + "th ");
				builder.append(get(i));
				builder.append(",");
			}
		}

		return builder.toString();
	}

}