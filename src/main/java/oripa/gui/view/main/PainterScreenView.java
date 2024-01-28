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
package oripa.gui.view.main;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import oripa.geom.RectangleDomain;
import oripa.gui.view.ScreenView;
import oripa.gui.view.creasepattern.PaintComponentGraphics;
import oripa.vecmath.Vector2d;

/**
 * @author OUCHI Koji
 *
 */
public interface PainterScreenView extends ScreenView {

	static final double INITIAL_CAMERA_SCALE = 1.5;

	// PainterScreenSetting getMainScreenSetting();

	void initializeCamera(RectangleDomain domain);

	void setPaintComponentListener(Consumer<PaintComponentGraphics> listener);

	void setMouseLeftClickListener(BiConsumer<Vector2d, Boolean> listener);

	void setMouseRightClickListener(BiConsumer<Vector2d, Boolean> listener);

	void setMousePressListener(BiConsumer<Vector2d, Boolean> listener);

	void setMouseReleaseListener(BiConsumer<Vector2d, Boolean> listener);

	void setMouseDragListener(BiConsumer<Vector2d, Boolean> listener);

	void setMouseMoveListener(BiConsumer<Vector2d, Boolean> listener);

	void setCameraScaleUpdateListener(Consumer<Double> listener);

	void setUsingCtrlKeyOnDragListener(Runnable listener);

	void setZeroLineWidthUpdateListener(Consumer<Boolean> listener);

	void setVertexVisibleUpdateListener(Consumer<Boolean> listener);

	void setMVLineVisibleUpdateListener(Consumer<Boolean> listener);

	void setAuxLineVisibleUpdateListener(Consumer<Boolean> listener);

	void setGridVisibleUpdateListener(Consumer<Boolean> listener);

	void setCrossLineVisibleUpdateListener(Consumer<Boolean> listener);

	void updateCameraCenter(RectangleDomain paperDomain);

	void setUsingCtrlKeyOnDrag(boolean using);
}
