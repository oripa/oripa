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

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * @author Koji
 *
 * @param <Backup>
 */
public abstract class AbstractUndoManager<Backup> {

	private final Deque<UndoInfo<Backup>> undoStack = new ConcurrentLinkedDeque<UndoInfo<Backup>>();
	private UndoInfo<Backup> cache;
	private boolean changed = false;
	protected int max = 1000;

	/**
	 * Constructor
	 */
	public AbstractUndoManager() {
		super();
	}

	protected abstract UndoInfo<Backup> createUndoInfo(Backup info);

	public void push(final Backup info) {

		push(createUndoInfo(info));

	}

	public void push(final UndoInfo<Backup> info) {

		undoStack.addLast(info);

		if (undoStack.size() > max) {
			// forget the oldest data
			undoStack.removeFirst();
		}

		changed = true;
	}

	public UndoInfo<Backup> pop() {
		if (undoStack.isEmpty()) {
			return null;
		}

		changed = true;

		return undoStack.removeLast();
	}

	public UndoInfo<Backup> peek() {
		if (undoStack.isEmpty()) {
			return null;
		}

		return undoStack.peekLast();
	}

	public boolean isChanged() {
		return changed;
	}

	public void clearChanged() {
		changed = false;
	}

	public void clear() {
		clearChanged();
		undoStack.clear();
	}

	public boolean canUndo() {
		return !undoStack.isEmpty();
	}

	public void setCache(final Backup info) {
		cache = createUndoInfo(info);
	}

	public UndoInfo<Backup> getCache() {
		return cache;
	}

	public void pushCachedInfo() {
		this.push(cache);
	}

	public int size() {
		return undoStack.size();
	}

}