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

public class RingElement<V> {
	private RingElement<V> next;
	private RingElement<V> previous;

	private final int index;
	private final V value;

	RingElement(final int index, final V value) {
		this.index = index;
		this.value = value;
	}

	public RingElement<V> getNext() {
		return next;
	}

	void setNext(final RingElement<V> next) {
		this.next = next;
	}

	public RingElement<V> getPrevious() {
		return previous;
	}

	void setPrevious(final RingElement<V> previous) {
		this.previous = previous;
	}

	public int getRingIndex() {
		return index;
	}

	public V getValue() {
		return value;
	}
}