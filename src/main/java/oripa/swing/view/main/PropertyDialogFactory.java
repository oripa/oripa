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
package oripa.swing.view.main;

import java.awt.Rectangle;

import javax.swing.JFrame;

import jakarta.inject.Inject;
import oripa.gui.view.FrameView;
import oripa.gui.view.main.PropertyDialogView;
import oripa.resource.ResourceHolder;

/**
 * @author OUCHI Koji
 *
 */
public class PropertyDialogFactory {
    private final ResourceHolder resourceHolder;

    @Inject
    public PropertyDialogFactory(final ResourceHolder resourceHolder) {
        this.resourceHolder = resourceHolder;
    }

    public PropertyDialogView create(final FrameView parent) {
        var frame = (JFrame) parent;
        PropertyDialog dialog = new PropertyDialog(frame, resourceHolder);

        Rectangle rec = frame.getBounds();
        dialog.setLocation(
                (int) (rec.getCenterX() - dialog.getWidth() / 2),
                (int) (rec.getCenterY() - dialog.getHeight() / 2));

        return dialog;
    }
}
