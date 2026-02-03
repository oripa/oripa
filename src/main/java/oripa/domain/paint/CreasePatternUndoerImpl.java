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

import oripa.util.history.AbstractUndoManager;
import oripa.value.OriLine;

/**
 * @author Koji
 *
 */
public class CreasePatternUndoerImpl implements CreasePatternUndoer {
    private final AbstractUndoManager<Collection<OriLine>> undoManager = new CreasePatternUndoManager();

    private final CreasePatternHolder owner;

    public CreasePatternUndoerImpl(final CreasePatternHolder aOwner) {
        owner = aOwner;
    }

    @Override
    public synchronized void pushUndoInfo() {
        undoManager.push(owner.getCreasePattern());
    }

    @Override
    public synchronized void undo() {
        var infoOpt = undoManager.undo(owner.getCreasePattern());

        infoOpt.ifPresent(info -> owner.getCreasePattern().replaceWith(info.getInfo()));
    }

    @Override
    public boolean canUndo() {
        return undoManager.canUndo();
    }

    @Override
    public synchronized void redo() {
        var infoOpt = undoManager.redo();

        infoOpt.ifPresent(info -> owner.getCreasePattern().replaceWith(info.getInfo()));
    }

    @Override
    public boolean canRedo() {
        return undoManager.canRedo();
    }

    @Override
    public boolean changeExists() {
        return undoManager.isChanged();
    }

    @Override
    public void clearChanged() {
        undoManager.clearChanged();
    }

    @Override
    public void clear() {
        undoManager.clear();
    }
}
