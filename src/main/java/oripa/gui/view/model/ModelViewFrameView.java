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
package oripa.gui.view.model;

import java.beans.PropertyChangeListener;
import java.util.function.Consumer;

import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.gui.view.FrameView;

/**
 * @author OUCHI Koji
 *
 */
public interface ModelViewFrameView extends FrameView {

	public ModelViewScreenView getModelScreenView();

	public void setModelCount(int count);

	public void setModel(final OrigamiModel origamiModel);

	public void putPaperDomainChangeListener(final Object parentOfListener, final PropertyChangeListener listener);

	public void putModelIndexChangeListener(final Object parentOfListener, final PropertyChangeListener listener);

	public void setOnCloseListener(final Consumer<FrameView> listener);

	public void selectModel(final int index);

	public void addFlipModelButtonListener(Runnable listener);

	public void addCrossLineButtonListener(Runnable listener);

	public void addExportDXFButtonListener(Runnable listener);

	public void addExportOBJButtonListener(Runnable listener);

	public void addExportSVGButtonListener(Runnable listener);

	public void addFillAlphaButtonListener(Runnable listener);

	public void addFillNoneButtonListener(Runnable listener);

	public boolean isCrossLineVisible();

	public void setModelDisplayMode(ModelDisplayMode mode);

	public void addModelSwitchListener(Consumer<Integer> listener);

	public void showExportErrorMessage(Exception e);
}
