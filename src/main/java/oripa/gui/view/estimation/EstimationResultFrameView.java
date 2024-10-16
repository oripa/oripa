/**
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2013-     ORIPA OSS Project  https://github.com/oripa/oripa
 * Copyright (C) 2005-2009 Jun Mitani         http://mitani.cs.tsukuba.ac.jp/

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General License for more details.

    You should have received a copy of the GNU General License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package oripa.gui.view.estimation;

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import oripa.domain.fold.FoldedModel;
import oripa.gui.view.FrameView;

/**
 * @author OUCHI Koji
 *
 */
public interface EstimationResultFrameView extends FrameView {

	EstimationResultUIView getUI();

	void setModelCount(int count);

	void setColors(final Color front, final Color back);

	void setSaveColorsListener(final BiConsumer<Color, Color> listener);

	void putModelIndexChangeListener(final Object parentOfListener, final PropertyChangeListener listener);

	void setOnCloseListener(final Consumer<FrameView> listener);

	void selectModel(final int index);

	void addModelSwitchListener(final Consumer<Integer> listener);

	void setModel(final FoldedModel foldedModel, double eps);
}
