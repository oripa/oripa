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
package oripa.gui.presenter.creasepattern;

import jakarta.inject.Inject;
import oripa.domain.paint.byvalue.ByValueContext;
import oripa.gui.presenter.creasepattern.byvalue.LineByValueAction;
import oripa.gui.presenter.creasepattern.copypaste.CopyAndPasteActionFactory;

/**
 * @author OUCHI Koji
 *
 */
public class ComplexActionFactory {
    private final EditOutlineActionFactory outlineFactory;
    private final CopyAndPasteActionFactory copyPasteFactory;
    private final ByValueContext byValueContext;
    private final TypeForChangeContext typeForChangeContext;

    @Inject
    public ComplexActionFactory(final EditOutlineActionFactory outlineFactory,
            final CopyAndPasteActionFactory copyPasteFactory,
            final ByValueContext byValueContext,
            final TypeForChangeContext typeForChangeContext) {
        this.outlineFactory = outlineFactory;
        this.copyPasteFactory = copyPasteFactory;
        this.byValueContext = byValueContext;
        this.typeForChangeContext = typeForChangeContext;
    }

    public GraphicMouseAction createEditOutline() {
        return outlineFactory.create();
    }

    public GraphicMouseAction createCopyAndPaste() {
        return copyPasteFactory.createCopyAndPaste();
    }

    public GraphicMouseAction createCutAndPaste() {
        return copyPasteFactory.createCutAndPaste();
    }

    public GraphicMouseAction createImport() {
        return copyPasteFactory.createImport();
    }

    public GraphicMouseAction createByValue() {
        return new LineByValueAction(byValueContext);
    }

    public GraphicMouseAction createTypeChange() {
        return new LineTypeChangeAction(typeForChangeContext);
    }
}
