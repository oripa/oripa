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
package oripa.domain.paint;

import java.util.Collection;

import oripa.domain.creasepattern.CreasePatternInterface;
import oripa.util.history.AbstractUndoManager;
import oripa.util.history.UndoInfo;
import oripa.value.OriLine;

/**
 * @author Koji
 *
 */
public class CreasePatternUndoer implements CreasePatternUndoerInterface {
	private final AbstractUndoManager<Collection<OriLine>> undoManager = new CreasePatternUndoManager();

	private final CreasePatternHolder owner;

	public CreasePatternUndoer(final CreasePatternHolder aOwner) {
		owner = aOwner;
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.domain.paint.CreasePatternUndoerInterface#pushUndoInfo()
	 */
	@Override
	public synchronized void pushUndoInfo() {
		undoManager.push(owner.getCreasePattern());
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.domain.paint.CreasePatternUndoerInterface#loadUndoInfo()
	 */
	@Override
	public synchronized void undo() {
		UndoInfo<Collection<OriLine>> info = undoManager.undo(owner.getCreasePattern());

		if (info == null) {
			return;
		}

		CreasePatternInterface creasePattern = owner.getCreasePattern();
		creasePattern.clear();
		creasePattern.addAll(info.getInfo());
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.domain.paint.CreasePatternUndoerInterface#canUndo()
	 */
	@Override
	public boolean canUndo() {
		return undoManager.canUndo();
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.domain.paint.CreasePatternUndoerInterface#redo()
	 */
	@Override
	public void redo() {
		UndoInfo<Collection<OriLine>> info = undoManager.redo();

		if (info == null) {
			return;
		}

		CreasePatternInterface creasePattern = owner.getCreasePattern();
		creasePattern.clear();
		creasePattern.addAll(info.getInfo());
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.domain.paint.CreasePatternUndoerInterface#canRedo()
	 */
	@Override
	public boolean canRedo() {
		return undoManager.canRedo();
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.domain.paint.CreasePatternUndoerInterface#isChanged()
	 */
	@Override
	public boolean changeExists() {
		return undoManager.isChanged();
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.domain.paint.CreasePatternUndoerInterface#clearChanged()
	 */
	@Override
	public void clearChanged() {
		undoManager.clearChanged();
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.domain.paint.CreasePatternUndoerInterface#clear()
	 */
	@Override
	public void clear() {
		undoManager.clear();
	}
}
