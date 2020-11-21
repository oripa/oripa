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
package oripa.util.history;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Koji
 *
 * @param <Backup>
 */
public abstract class AbstractUndoManager<Backup> {

	private final List<UndoInfo<Backup>> undoList = Collections
			.synchronizedList(new ArrayList<UndoInfo<Backup>>());
	private int index = 0;
	private int endIndex = 0;
	private boolean changed = false;

	/**
	 * Constructor
	 */
	public AbstractUndoManager() {
	}

	protected abstract UndoInfo<Backup> createUndoInfo(Backup info);

	public void push(final Backup info) {
		push(createUndoInfo(info));
	}

	private void set(final int i, final UndoInfo<Backup> info) {
		if (i == undoList.size()) {
			undoList.add(info);
		} else {
			undoList.set(i, info);
		}
	}

	public void push(final UndoInfo<Backup> info) {
		set(index, info);
		index++;
		endIndex = index;

		changed = true;
	}

	/**
	 *
	 * @param info
	 *            current data which may be stored as the start of undo
	 *            sequence.
	 * @return
	 */
	public UndoInfo<Backup> undo(final Backup info) {
		if (index == 0) {
			return null;
		}

		if (index == endIndex) {
			set(index, createUndoInfo(info));
		}

		changed = true;

		return undoList.get(--index);
	}

	public UndoInfo<Backup> redo() {
		if (index == endIndex) {
			return null;
		}

		changed = true;

		return undoList.get(++index);
	}

	public boolean isChanged() {
		return changed;
	}

	public void clearChanged() {
		changed = false;
	}

	public void clear() {
		clearChanged();
		undoList.clear();
		index = 0;
		endIndex = 0;
	}

	public boolean canUndo() {
		return index > 0;
	}

	public boolean canRedo() {
		return index < endIndex;
	}
}